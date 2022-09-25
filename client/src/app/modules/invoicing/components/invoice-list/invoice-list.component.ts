import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { INVOICING_STATUS_SENT } from 'src/app/libs/Constants';
import { formatDateTimeForSortTable, formatEurosForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
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

  constructor(
    private appService: AppService,
    private invoiceService: InvoiceService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Factures & paiements");
    this.putDefaultPeriod();
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

    this.tableAction.push({ actionIcon: "settings", actionName: "Voir le détail de la facture / associer", actionLinkFunction: this.getActionLink, display: true, } as SortTableAction);
  }

  invoiceForm = this.formBuilder.group({
  });

  getActionLink(action: SortTableAction, element: any) {
    if (element)
      return ['/invoicing', element.id];
    return undefined;
  }

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

  public static getCustomerOrderName(element: any) {
    if (element.customerOrder) {
      if (element.customerOrder.confrere)
        return element.customerOrder.confrere.denomination
      if (element.customerOrder.responsable)
        return element.customerOrder.responsable.firstname + " " + element.customerOrder.responsable.lastname;
      if (element.customerOrder.tiers)
        return element.customerOrder.tiers.firstname + " " + element.customerOrder.tiers.lastname;
    }
  }

  public static getResponsableName(element: any) {
    if (element.customerOrder) {
      if (element.customerOrder.responsable)
        return element.customerOrder.responsable.tiers.firstname + " " + element.customerOrder.responsable.tiers.lastname;
    }
    return "";
  }

  public static getAffaireList(element: any) {
    let names = [];
    if (element.customerOrder && element.customerOrder.provisions) {
      for (let provision of element.customerOrder.provisions) {
        if (provision.affaire) {
          let affaire = provision.affaire;
          if (affaire.denomination) {
            names.push(affaire.denomination);
          } else {
            names.push(affaire.firstname + " " + affaire.lastname);
          }
        }
      }
    }
    return names.join(", ");
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
