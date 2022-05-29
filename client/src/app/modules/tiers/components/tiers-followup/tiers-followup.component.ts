import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { ICSEvent } from 'src/app/libs/ICSEvent';
import { createEvent } from 'src/app/libs/ICSHelper';
import { Gift } from 'src/app/modules/miscellaneous/model/Gift';
import { GiftService } from 'src/app/modules/miscellaneous/services/gift.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { environment } from 'src/environments/environment';
import { ITiers } from '../../model/ITiers';
import { TiersFollowup } from '../../model/TiersFollowup';
import { TiersFollowupType } from '../../model/TiersFollowupType';
import { TiersFollowupService } from '../../services/tiers.followup.service';
import { TiersFollowupTypeService } from '../../services/tiers.followup.type.service';
import { TiersComponent } from '../tiers/tiers.component';

@Component({
  selector: 'tiers-followup',
  templateUrl: './tiers-followup.component.html',
  styleUrls: ['./tiers-followup.component.css']
})
export class TiersFollowupComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() tiers: ITiers = {} as ITiers;
  @Input() editMode: boolean = false;

  tiersFollowupTypes: TiersFollowupType[] = [] as Array<TiersFollowupType>;
  newFollowup: TiersFollowup = {} as TiersFollowup;

  salesEmployees: Employee[] = [] as Array<Employee>;
  filteredSalesEmployees: Observable<Employee[]> | undefined;

  gifts: Gift[] = [] as Array<Gift>;

  @ViewChild(MatSort) sort!: MatSort;

  displayedColumns: string[] = ['followupDate', 'name', 'salesEmployee', 'gift', 'observations'];

  followupDataSource: MatTableDataSource<TiersFollowup> = new MatTableDataSource<TiersFollowup>();

  filterValue: string = "";

  reminderDatetime: string = "";

  constructor(private formBuilder: FormBuilder,
    protected tiersFollowupTypeService: TiersFollowupTypeService,
    protected tiersFollowupService: TiersFollowupService,
    private employeeService: EmployeeService,
    private giftService: GiftService) { }

  ngOnInit() {
    this.tiersFollowupTypeService.getTiersFollowupTypes().subscribe(response => {
      this.tiersFollowupTypes = response;
    })
    this.employeeService.getSalesEmployees().subscribe(response => {
      this.salesEmployees = response;
    })
    this.giftService.getGifts().subscribe(response => {
      this.gifts = response;
    })

    // Initialize autocomplete fields
    this.filteredSalesEmployees = this.followupForm.get("salesEmployee")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterEmployee(this.salesEmployees, value)),
    );
  }

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
    if (this.tiers.tiersFollowups == null || this.tiers.tiersFollowups == undefined)
      this.tiers.tiersFollowups = [] as Array<TiersFollowup>;

    if (TiersComponent.instanceOfTiers(this.tiers))
      this.newFollowup.tiers = this.tiers;

    if (TiersComponent.instanceOfResponsable(this.tiers))
      this.newFollowup.responsable = this.tiers;

    this.tiersFollowupService.addFollowup(this.newFollowup).subscribe(response => {
      this.tiers.tiersFollowups = response;
      this.setData();
    });
    this.newFollowup = {} as TiersFollowup;
    this.setData();
  }

  followupForm = this.formBuilder.group({
    tiersFollowupType: ['', Validators.required],
    salesEmployee: ['', Validators.required],
    gift: [''],
    followupDatetime: ['', Validators.required],
    observations: [''],
  })

  private _filterEmployee(employees: Employee[], value: string): Employee[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return employees.filter(employee => employee.firstname != undefined && employee.lastname != undefined && employee.firstname.toLowerCase().includes(filterValue) || employee.lastname.toLowerCase().includes(filterValue));
  }

  public displayEmployee(employee: Employee): string {
    return employee ? employee.firstname + " " + employee.lastname : '';
  }

  compareWithId = compareWithId;

  getFormStatus(): boolean {
    this.followupForm.markAllAsTouched();
    return this.followupForm.valid;
  }

  createEvent() {
    let event = {} as ICSEvent;

    if (TiersComponent.instanceOfTiers(this.tiers)) {
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

    if (TiersComponent.instanceOfResponsable(this.tiers)) {
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
