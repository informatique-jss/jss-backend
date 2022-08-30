import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { INVOICING_STATUS_SENT, QUOTATION_LABEL_TYPE_AFFAIRE_CODE, QUOTATION_LABEL_TYPE_CLIENT_CODE } from 'src/app/libs/Constants';
import { formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
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

  constructor(
    private appService: AppService,
    private invoiceService: InvoiceService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Factures");
    this.putDefaultPeriod();
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N° de facture" } as SortTableColumn);
    this.displayedColumns.push({ id: "customerOrderId", fieldName: "customerOrder.id", label: "N° de commande" } as SortTableColumn);
    this.displayedColumns.push({ id: "customerOrderName", fieldName: "customerOrder.tiers", label: "Donneur d'ordre", valueFonction: this.getCustomerOrderName } as SortTableColumn);
    this.displayedColumns.push({ id: "responsable", fieldName: "customerOrder.tiers", label: "Responsable", valueFonction: this.getResponsableName } as SortTableColumn);
    this.displayedColumns.push({ id: "affaires", fieldName: "customerOrder.affaire", label: "Affaire(s)", valueFonction: this.getAffaireList, isShrinkColumn: true } as SortTableColumn);
    this.displayedColumns.push({ id: "invoicePayer", fieldName: "customerOrder.id", label: "Payeur", valueFonction: this.getPayer } as SortTableColumn);
    this.displayedColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date d'émission", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Montant TTC", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "description", fieldName: "customerOrder.description", label: "Description" } as SortTableColumn);
  }

  invoiceForm = this.formBuilder.group({
  });

  getCustomerOrderName(element: any) {
    if (element.customerOrder) {
      if (element.customerOrder.confrere)
        return element.customerOrder.confrere.denomination
      if (element.customerOrder.tiers)
        return element.customerOrder.tiers.firstname + " " + element.customerOrder.tiers.lastname;
    }
  }

  getResponsableName(element: any) {
    if (element.customerOrder) {
      if (element.customerOrder.responsable)
        return element.customerOrder.responsable.tiers.firstname + " " + element.customerOrder.responsable.tiers.lastname;
    }
    return "";
  }

  getAffaireList(element: any) {
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

  getPayer(element: any) {
    if (element.billingLabelType && element.customerOrder) {
      if (element.billingLabelType.code == QUOTATION_LABEL_TYPE_CLIENT_CODE) {
        if (element.customerOrder.responsable)
          return element.customerOrder.responsable.tiers.firstname + " " + element.customerOrder.responsable.tiers.lastname;
        if (element.customerOrder.confrere)
          return element.customerOrder.confrere.denomination
        if (element.customerOrder.tiers)
          return element.customerOrder.tiers.firstname + " " + element.customerOrder.tiers.lastname;
      }
      if (element.billingLabelType.code == QUOTATION_LABEL_TYPE_AFFAIRE_CODE) {
        if (element.customerOrder.affaires && element.customerOrder.affaires[0])
          if (element.customerOrder.affaires[0].denomination)
            return element.customerOrder.affaires[0].denomination;
        return element.customerOrder.affaires[0].firstname + " " + element.customerOrder.affaires[0].lastname;
      }
      return element.billingLabel;
    }
  }

  putDefaultPeriod() {
    if (!this.invoiceSearch.startDate || !this.invoiceSearch.endDate) {
      this.invoiceSearch.startDate = new Date();
      this.invoiceSearch.endDate = new Date();
      this.invoiceSearch.startDate.setDate(this.invoiceSearch.endDate.getDate() - 30);
    }
  }

  searchInvoices() {
    this.invoiceService.getInvoicesList(this.invoiceSearch).subscribe(response => {
      this.invoices = response;
    })
  }
}
