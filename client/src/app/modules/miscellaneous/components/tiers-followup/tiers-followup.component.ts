import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { formatDateForSortTable } from 'src/app/libs/FormatHelper';
import { ICSEvent } from 'src/app/libs/ICSEvent';
import { createEvent } from 'src/app/libs/ICSHelper';
import { instanceOfResponsable, instanceOfTiers } from 'src/app/libs/TypeHelper';
import { Gift } from 'src/app/modules/miscellaneous/model/Gift';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { environment } from 'src/environments/environment';
import { Invoice } from '../../../quotation/model/Invoice';
import { SortTableColumn } from '../../model/SortTableColumn';
import { TiersFollowup } from '../../model/TiersFollowup';
import { TiersFollowupType } from '../../model/TiersFollowupType';
import { ConstantService } from '../../services/constant.service';
import { TiersFollowupService } from '../../services/tiers.followup.service';

@Component({
  selector: 'tiers-followup',
  templateUrl: './tiers-followup.component.html',
  styleUrls: ['./tiers-followup.component.css']
})
export class TiersFollowupComponent implements OnInit {

  @Input() tiers: Tiers | Responsable | undefined;
  @Input() invoice: Invoice | undefined;
  @Input() affaire: Affaire | undefined;
  @Input() editMode: boolean = false;

  newFollowup = {} as TiersFollowup;
  followUpTypes = [] as Array<TiersFollowupType>;
  salesEmployees = [] as Array<Employee>;

  gifts: Gift[] = [] as Array<Gift>;

  @ViewChild(MatSort) sort!: MatSort;

  followupDataSource: MatTableDataSource<TiersFollowup> = new MatTableDataSource<TiersFollowup>();

  filterValue: string = "";

  reminderDatetime: string = "";

  displayedColumns: SortTableColumn<TiersFollowup>[] = [];
  searchText: string | undefined;

  constructor(private formBuilder: UntypedFormBuilder,
    protected tiersFollowupService: TiersFollowupService,
    private constantService: ConstantService,
    private employeeService: EmployeeService) { }

  ngOnInit() {
    this.employeeService.getEmployees().subscribe(response => {
      this.salesEmployees = response;
    })

    this.displayedColumns = [];
    this.displayedColumns.push({ id: "followupDate", fieldName: "followupDate", label: "Date", valueFonction: formatDateForSortTable } as SortTableColumn<TiersFollowup>);
    this.displayedColumns.push({ id: "name", fieldName: "tiersFollowupType.label", label: "Type" } as SortTableColumn<TiersFollowup>);
    this.displayedColumns.push({ id: "salesEmployee", fieldName: "salesEmployee", label: "Par", valueFonction: (element: TiersFollowup, column: SortTableColumn<TiersFollowup>) => { return (element && element.salesEmployee) ? element.salesEmployee.firstname + " " + element.salesEmployee.lastname : "" } } as SortTableColumn<TiersFollowup>);
    this.displayedColumns.push({ id: "gift", fieldName: "gift.label", label: "Cadeau" } as SortTableColumn<TiersFollowup>);
    this.displayedColumns.push({ id: "giftNumber", fieldName: "giftNumber", label: "Nombre" } as SortTableColumn<TiersFollowup>);
    this.displayedColumns.push({ id: "observations", fieldName: "observations", label: "Observations" } as SortTableColumn<TiersFollowup>);
  }

  formatDateForSortTable = formatDateForSortTable;

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tiers != undefined || changes.invoice != undefined || changes.affaire != undefined) {
      this.followupForm.markAllAsTouched();
      this.setData();

    }
  }

  setData() {
    if (this.tiers && this.tiers.tiersFollowups != null) {
      this.tiers.tiersFollowups.sort(function (a: TiersFollowup, b: TiersFollowup) {
        return new Date(b.followupDate).getTime() - new Date(a.followupDate).getTime();
      });
      // By default, select Salesman of Tiers
      if (this.newFollowup && !this.newFollowup.salesEmployee)
        this.newFollowup.salesEmployee = this.tiers.salesEmployee;
    } else if (this.invoice && this.invoice.tiersFollowups != null) {
      this.invoice.tiersFollowups.sort(function (a: TiersFollowup, b: TiersFollowup) {
        return new Date(b.followupDate).getTime() - new Date(a.followupDate).getTime();
      });
      // By default, select Invoice recover responsible
      if (this.newFollowup && !this.newFollowup.salesEmployee)
        this.newFollowup.salesEmployee = this.constantService.getEmployeeInvoiceReminderResponsible();

      if (this.newFollowup)
        this.newFollowup.giftNumber = 1;
    } else if (this.affaire && this.affaire.tiersFollowups != null) {
      this.affaire.tiersFollowups.sort(function (a: TiersFollowup, b: TiersFollowup) {
        return new Date(b.followupDate).getTime() - new Date(a.followupDate).getTime();
      });
      // By default, select Invoice recover responsible
      if (this.newFollowup && !this.newFollowup.salesEmployee)
        this.newFollowup.salesEmployee = this.constantService.getEmployeeInvoiceReminderResponsible();

      if (this.newFollowup)
        this.newFollowup.giftNumber = 1;
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

    if (this.tiers) {
      if (this.tiers.tiersFollowups == null || this.tiers.tiersFollowups == undefined)
        this.tiers.tiersFollowups = [] as Array<TiersFollowup>;
    } else if (this.invoice) {
      if (this.invoice.tiersFollowups == null || this.invoice.tiersFollowups == undefined)
        this.invoice.tiersFollowups = [] as Array<TiersFollowup>;
    } else if (this.affaire) {
      if (this.affaire.tiersFollowups == null || this.affaire.tiersFollowups == undefined)
        this.affaire.tiersFollowups = [] as Array<TiersFollowup>;
    }
    // Remove UTC delay
    this.newFollowup.followupDate = new Date(this.newFollowup.followupDate.setHours(12));

    let promise;
    if (this.invoice)
      promise = this.tiersFollowupService.addFollowupForInvoice(this.newFollowup, this.invoice);
    else if (this.affaire)
      promise = this.tiersFollowupService.addFollowupForAffaire(this.newFollowup, this.affaire);
    else if (instanceOfTiers(this.tiers))
      promise = this.tiersFollowupService.addFollowupForTiers(this.newFollowup, this.tiers);
    else if (instanceOfResponsable(this.tiers))
      promise = this.tiersFollowupService.addFollowupForResponsable(this.newFollowup, this.tiers);

    if (promise)
      promise.subscribe(response => {
        if (this.tiers)
          this.tiers.tiersFollowups = response;
        else if (this.invoice)
          this.invoice.tiersFollowups = response;
        else if (this.affaire)
          this.affaire.tiersFollowups = response;
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

    if (this.tiers) {
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
    } else if (this.invoice) {
      event.description = "Bonjour, \n\nMerci de relancer la facture n°" + this.invoice.id + ".";
      event.htmlDescription = "<!DOCTYPE HTML PUBLIC -//W3C//DTD HTML 3.2//EN><html><body><p>Bonjour,</p><p>Merci de relancer la <a href=\"" + environment.frontendUrl + "invoicing/view/" + this.invoice.id + "\">facture n°" + this.invoice.id + "</a></p></html></body>";

      event.summary = "[Relance Facture] : n°" + this.invoice.id;
      event.location = "";
      event.url = "";
      event.start = new Date(this.reminderDatetime);
      let d = new Date(event.start.getTime());
      d.setMinutes(d.getMinutes() + 60);
      event.end = d;

      createEvent([event], 'Rappel factue n°' + this.invoice.id + '.ics');
    } else if (this.affaire) {
      event.description = "Bonjour, \n\nMerci de relancer l'affaire " + this.affaire.id + " " + this.affaire.denomination + ".";
      event.htmlDescription = "<!DOCTYPE HTML PUBLIC -//W3C//DTD HTML 3.2//EN><html><body><p>Bonjour,</p><p>Merci de relancer l'<a href=\"" + environment.frontendUrl + "quotation/affaire/" + this.affaire.id + "\">affaire n°" + this.affaire.id + "</a></p></html></body>";

      event.summary = "[Relance Facture] : n°" + this.affaire.id;
      event.location = "";
      event.url = "";
      event.start = new Date(this.reminderDatetime);
      let d = new Date(event.start.getTime());
      d.setMinutes(d.getMinutes() + 60);
      event.end = d;

      createEvent([event], 'Rappel affaire n°' + this.affaire.id + '.ics');
    }
  }
}
