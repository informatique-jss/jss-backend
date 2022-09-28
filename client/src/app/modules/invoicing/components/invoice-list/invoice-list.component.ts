import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { INVOICING_STATUS_SENT } from 'src/app/libs/Constants';
import { formatDateTimeForSortTable, formatEurosForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { ITiers } from 'src/app/modules/tiers/model/ITiers';
import { AppService } from 'src/app/services/app.service';
import { InvoiceSearch } from '../../model/InvoiceSearch';
import { InvoiceService } from '../../services/invoice.service';

@Component({
  selector: 'invoice-list',
  templateUrl: './invoice-list.component.html',
  styleUrls: ['./invoice-list.component.css']
})
export class InvoiceListComponent implements OnInit {

  invoiceSearch: InvoiceSearch = {} as InvoiceSearch;
  invoices: Invoice[] | undefined;
  displayedColumns: SortTableColumn[] = [];
  INVOICING_STATUS_SENT = INVOICING_STATUS_SENT;
  tableAction: SortTableAction[] = [];
  @Output() actionBypass: EventEmitter<Invoice> = new EventEmitter<Invoice>();
  @Input() overrideIconAction: string = "";
  @Input() overrideTooltipAction: string = "";
  @Input() defaultStatusFilter: string[] | undefined;

  constructor(
    private appService: AppService,
    private invoiceService: InvoiceService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Factures & paiements");
    this.putDefaultPeriod();
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N° de facture" } as SortTableColumn);
    this.displayedColumns.push({ id: "status", fieldName: "invoiceStatus.label", label: "Status" } as SortTableColumn);
    this.displayedColumns.push({ id: "customerOrderId", fieldName: "customerOrder.id", label: "N° de commande", actionLinkFunction: InvoiceListComponent.getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la commande associée" } as SortTableColumn);
    this.displayedColumns.push({ id: "customerOrderName", fieldName: "customerOrder.tiers", label: "Donneur d'ordre", valueFonction: InvoiceListComponent.getCustomerOrderName, actionLinkFunction: InvoiceListComponent.getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la fiche du donneur d'ordre" } as SortTableColumn);
    this.displayedColumns.push({ id: "responsable", fieldName: "customerOrder.tiers", label: "Responsable", valueFonction: InvoiceListComponent.getResponsableName } as SortTableColumn);
    this.displayedColumns.push({ id: "affaires", fieldName: "customerOrder.affaire", label: "Affaire(s)", valueFonction: InvoiceListComponent.getAffaireList, isShrinkColumn: true } as SortTableColumn);
    this.displayedColumns.push({ id: "invoicePayer", fieldName: "billingLabel", label: "Payeur" } as SortTableColumn);
    this.displayedColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date d'émission", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Montant TTC", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "description", fieldName: "customerOrder.description", label: "Description" } as SortTableColumn);
    this.displayedColumns.push({ id: "payments", fieldName: "payments", label: "Paiement(s) associé(s)", valueFonction: this.getPaymentLabel } as SortTableColumn);

    if (this.overrideIconAction == "") {
      this.tableAction.push({
        actionIcon: "settings", actionName: "Voir le détail de la facture / associer", actionLinkFunction: (action: SortTableAction, element: any) => {
          if (element)
            return ['/invoicing', element.id];
          return undefined;
        }, display: true,
      } as SortTableAction);
    } else {
      this.tableAction.push({
        actionIcon: this.overrideIconAction, actionName: this.overrideTooltipAction, actionClick: (action: SortTableAction, element: any) => {
          this.actionBypass.emit(element);
        }, display: true,
      } as SortTableAction);
    };
  }

  invoiceForm = this.formBuilder.group({
  });

  public static getColumnLink(column: SortTableColumn, element: any) {
    if (element && column.id == "customerOrderName") {
      if (element.customerOrder.responsable)
        return ['/tiers/responsable/', element.customerOrder.responsable.id];
      if (element.customerOrder.tiers)
        return ['/tiers/', element.customerOrder.tiers.id];
    }
    if (element && column.id == "customerOrderId") {
      if (element.isQuotation)
        return ['/quotation/', element.customerOrder.id];
      return ['/order/', element.customerOrder.id];
    }
    return ['/tiers', element.customerOrder.tiers.id];
  }

  public static getCustomerOrderName(element: Invoice) {
    if (element.customerOrder) {
      if (element.customerOrder.confrere)
        return element.customerOrder.confrere.label
      if (element.customerOrder.responsable)
        return element.customerOrder.responsable.firstname + " " + element.customerOrder.responsable.lastname;
      if (element.customerOrder.tiers)
        return element.customerOrder.tiers.firstname + " " + element.customerOrder.tiers.lastname;
    }
    return "";
  }

  public static getCustomerOrder(invoice: Invoice): ITiers {
    if (invoice.customerOrder) {
      if (invoice.customerOrder.confrere)
        return invoice.customerOrder.confrere;
      if (invoice.customerOrder.responsable)
        return invoice.customerOrder.responsable;
      if (invoice.customerOrder.tiers)
        return invoice.customerOrder.tiers;
    }
    return {} as ITiers;
  }

  public static getResponsableName(element: any) {
    if (element.customerOrder) {
      if (element.customerOrder.responsable)
        return element.customerOrder.responsable.tiers.firstname + " " + element.customerOrder.responsable.tiers.lastname;
    }
    return "";
  }

  public static getAffaireListArray(invoice: any) {
    let affaires = [];
    if (invoice.customerOrder && invoice.customerOrder.provisions) {
      for (let provision of invoice.customerOrder.provisions) {
        if (provision.affaire) {
          affaires.push(provision.affaire);
        }
      }
    }
    return affaires;
  }

  public static getAffaireList(invoice: any) {
    return InvoiceListComponent.getAffaireListArray(invoice).map(affaire => {
      if (affaire.denomination) {
        return affaire.denomination;
      } else {
        return affaire.firstname + " " + affaire.lastname;
      }
    }).join(", ");
  }

  public static getAmountPayed(invoice: Invoice) {
    let payed = 0;
    if (invoice.payments && invoice.payments.length)
      for (let payment of invoice.payments)
        payed += payment.paymentAmount;
    if (invoice.deposits && invoice.deposits.length)
      for (let deposit of invoice.deposits)
        payed += deposit.depositAmount;

    payed = Math.round(payed * 100) / 100;
    return payed;
  }

  public static getAmountRemaining(invoice: Invoice) {
    return invoice.totalPrice - InvoiceListComponent.getAmountPayed(invoice);
  }

  getPaymentLabel(element: any) {
    if (element && element.payments)
      return element.payments.map((e: { id: any; }) => e.id).join(", ");
    return "";
  }

  putDefaultPeriod() {
    if (!this.invoiceSearch.startDate && !this.invoiceSearch.endDate) {
      this.invoiceSearch.startDate = new Date();
      this.invoiceSearch.endDate = new Date();
      this.invoiceSearch.startDate.setDate(this.invoiceSearch.endDate.getDate() - 30);
    }
  }

  searchInvoices() {


    if (this.invoiceForm.valid && this.invoiceSearch.startDate && this.invoiceSearch.endDate) {
      this.invoiceSearch.startDate = new Date(toIsoString(this.invoiceSearch.startDate));
      this.invoiceSearch.endDate = new Date(toIsoString(this.invoiceSearch.endDate));
      this.invoiceService.getInvoicesList(this.invoiceSearch).subscribe(response => {
        this.invoices = response;
      })
    }
  }
}
