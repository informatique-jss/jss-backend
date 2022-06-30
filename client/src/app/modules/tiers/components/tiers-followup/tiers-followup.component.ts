import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { ICSEvent } from 'src/app/libs/ICSEvent';
import { createEvent } from 'src/app/libs/ICSHelper';
import { instanceOfResponsable, instanceOfTiers } from 'src/app/libs/TypeHelper';
import { Gift } from 'src/app/modules/miscellaneous/model/Gift';
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

  displayedColumns: string[] = ['followupDate', 'name', 'salesEmployee', 'gift', 'observations'];

  followupDataSource: MatTableDataSource<TiersFollowup> = new MatTableDataSource<TiersFollowup>();

  filterValue: string = "";

  reminderDatetime: string = "";

  constructor(private formBuilder: UntypedFormBuilder,
    protected tiersFollowupService: TiersFollowupService,
    protected tiersFollowupTypeService: TiersFollowupTypeService,
    private employeeService: EmployeeService) { }

  ngOnInit() {
    this.employeeService.getSalesEmployees().subscribe(response => {
      this.salesEmployees = response;
    })
    this.tiersFollowupTypeService.getTiersFollowupTypes().subscribe(response => {
      this.followUpTypes = response;
      this.newFollowup.tiersFollowupType = this.followUpTypes[0];
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tiers != undefined) {
      this.followupForm.markAllAsTouched();
      this.setData();
      // By default, select Salesman of Tiers
      this.newFollowup.salesEmployee = this.tiers.salesEmployee;
    }
  }

  setData() {
    if (this.tiers.tiersFollowups != null) {
      this.tiers.tiersFollowups.sort(function (a: TiersFollowup, b: TiersFollowup) {
        return new Date(b.followupDate).getTime() - new Date(a.followupDate).getTime();
      });

      this.followupDataSource = new MatTableDataSource(this.tiers.tiersFollowups);
      setTimeout(() => {
        this.followupDataSource.sort = this.sort;
        this.followupDataSource.sortingDataAccessor = (item: TiersFollowup, property) => {
          switch (property) {
            case 'followupDate': return new Date(item.followupDate).getTime() + "";
            case 'name': return item.tiersFollowupType.label;
            case 'salesEmployee': return (item.salesEmployee != null) ? item.salesEmployee.firstname + item.salesEmployee.lastname : "";
            case 'gift': return item.gift.label;
            case 'observations': return item.observations;
            default: return item.tiersFollowupType.label;
          }
        };

        this.followupDataSource.filterPredicate = (data: any, filter) => {
          const dataStr = JSON.stringify(data).toLowerCase();
          return dataStr.indexOf(filter) != -1;
        }
      });
    }
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    filterValue = filterValue.toLowerCase();
    this.followupDataSource.filter = filterValue;
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
