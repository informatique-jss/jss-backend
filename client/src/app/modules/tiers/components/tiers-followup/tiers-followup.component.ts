import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { formatDateForSortTable } from 'src/app/libs/FormatHelper';
import { ICSEvent } from 'src/app/libs/ICSEvent';
import { createEvent } from 'src/app/libs/ICSHelper';
import { instanceOfResponsable, instanceOfTiers } from 'src/app/libs/TypeHelper';
import { Gift } from 'src/app/modules/miscellaneous/model/Gift';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { environment } from 'src/environments/environment';
import { ITiers } from '../../model/ITiers';
import { TiersFollowup } from '../../model/TiersFollowup';
import { TiersFollowupType } from '../../model/TiersFollowupType';
import { TiersFollowupService } from '../../services/tiers.followup.service';
import { TiersFollowupTypeService } from '../../services/tiers.followup.type.service';

@Component({
  selector: 'tiers-followup',
  templateUrl: './tiers-followup.component.html',
  styleUrls: ['./tiers-followup.component.css']
})
export class TiersFollowupComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() tiers: ITiers = {} as ITiers;
  @Input() editMode: boolean = false;

  newFollowup = {} as TiersFollowup;
  followUpTypes = [] as Array<TiersFollowupType>;
  salesEmployees = [] as Array<Employee>;

  gifts: Gift[] = [] as Array<Gift>;

  @ViewChild(MatSort) sort!: MatSort;

  followupDataSource: MatTableDataSource<TiersFollowup> = new MatTableDataSource<TiersFollowup>();

  filterValue: string = "";

  reminderDatetime: string = "";

  displayedColumns: SortTableColumn[] = [];
  searchText: string | undefined;

  constructor(private formBuilder: UntypedFormBuilder,
    protected tiersFollowupService: TiersFollowupService,
    protected tiersFollowupTypeService: TiersFollowupTypeService,
    private employeeService: EmployeeService) { }

  ngOnInit() {
    this.employeeService.getEmployees().subscribe(response => {
      this.salesEmployees = response;
    })
    this.tiersFollowupTypeService.getTiersFollowupTypes().subscribe(response => {
      this.followUpTypes = response;
      this.newFollowup.tiersFollowupType = this.followUpTypes[0];
    });

    this.displayedColumns = [];
    this.displayedColumns.push({ id: "followupDate", fieldName: "followupDate", label: "Date", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "name", fieldName: "tiersFollowupType.label", label: "Type" } as SortTableColumn);
    this.displayedColumns.push({ id: "salesEmployee", fieldName: "salesEmployee", label: "Par", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { return (element && element.salesEmployee) ? element.salesEmployee.firstname + " " + element.salesEmployee.lastname : "" } } as SortTableColumn);
    this.displayedColumns.push({ id: "gift", fieldName: "gift.label", label: "Cadeau" } as SortTableColumn);
    this.displayedColumns.push({ id: "observations", fieldName: "observations", label: "Observations" } as SortTableColumn);
  }

  formatDateForSortTable = formatDateForSortTable;

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tiers != undefined) {
      this.followupForm.markAllAsTouched();
      this.setData();

    }
  }

  setData() {
    if (this.tiers.tiersFollowups != null) {
      this.tiers.tiersFollowups.sort(function (a: TiersFollowup, b: TiersFollowup) {
        return new Date(b.followupDate).getTime() - new Date(a.followupDate).getTime();
      });
      // By default, select Salesman of Tiers
      if (this.newFollowup && !this.newFollowup.salesEmployee)
        this.newFollowup.salesEmployee = this.tiers.salesEmployee;
    }
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
  }

  addNewFollowUp() {
    if (this.getFormStatus() == false)
      return;

    if (this.tiers.tiersFollowups == null || this.tiers.tiersFollowups == undefined)
      this.tiers.tiersFollowups = [] as Array<TiersFollowup>;

    if (instanceOfTiers(this.tiers))
      this.newFollowup.tiers = this.tiers;

    if (instanceOfResponsable(this.tiers))
      this.newFollowup.responsable = this.tiers;

    this.tiersFollowupService.addFollowup(this.newFollowup).subscribe(response => {
      this.tiers.tiersFollowups = response;
      this.setData();
    });
    this.newFollowup = {} as TiersFollowup;
    this.newFollowup.tiersFollowupType = this.followUpTypes[0];
    this.setData();
  }

  followupForm = this.formBuilder.group({
  })

  getFormStatus(): boolean {
    this.followupForm.markAllAsTouched();
    return this.followupForm.valid;
  }

  createEvent() {
    let event = {} as ICSEvent;

    if (instanceOfTiers(this.tiers)) {
      event.description = "Bonjour, \n\nMerci de rappeler le tiers " + this.tiers.denomination + ".";
      event.htmlDescription = "<!DOCTYPE HTML PUBLIC -//W3C//DTD HTML 3.2//EN><html><body><p>Bonjour,</p><p>Merci de rappeler le tiers <a href=\"" + environment.frontendUrl + "tiers/" + this.tiers.id + "\">" + this.tiers.denomination + "</a></p></html></body>";

      event.summary = "[Relance Tiers] : " + this.tiers.denomination;
      event.location = "";
      event.url = "";
      event.start = new Date(this.reminderDatetime);
      let d = new Date(event.start.getTime());
      d.setMinutes(d.getMinutes() + 60);
      event.end = d;

      createEvent([event], 'Rappel ' + this.tiers.denomination + '.ics');
    }

    if (instanceOfResponsable(this.tiers)) {
      event.description = "Bonjour, \n\nMerci de rappeler le responsable " + this.tiers.firstname + " " + this.tiers.lastname + " (" + this.tiers.id + ").";
      event.htmlDescription = "<!DOCTYPE HTML PUBLIC -//W3C//DTD HTML 3.2//EN><html><body><p>Bonjour,</p><p>Merci de rappeler le tiers <a href=\"" + environment.frontendUrl + "tiers/responsable/" + this.tiers.id + "\">" + this.tiers.firstname + " " + this.tiers.lastname + "</a></p></html></body>";

      event.summary = "[Relance Responsable] : " + this.tiers.firstname + " " + this.tiers.lastname;
      event.location = "";
      event.url = "";
      event.start = new Date(this.reminderDatetime);
      let d = new Date(event.start.getTime());
      d.setMinutes(d.getMinutes() + 60);
      event.end = d;

      createEvent([event], 'Rappel ' + this.tiers.firstname + " " + this.tiers.lastname + '.ics');
    }

  }

}
