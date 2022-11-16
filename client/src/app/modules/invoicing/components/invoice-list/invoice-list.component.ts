import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { AppService } from 'src/app/services/app.service';
import { InvoiceSearch } from '../../model/InvoiceSearch';
import { InvoiceSearchResult } from '../../model/InvoiceSearchResult';
import { InvoiceStatus } from '../../model/InvoiceStatus';
import { InvoiceSearchResultService } from '../../services/invoice.search.result.service';
import { getColumnLink } from '../invoice-tools';

@Component({
  selector: 'invoice-list',
  templateUrl: './invoice-list.component.html',
  styleUrls: ['./invoice-list.component.css']
})
export class InvoiceListComponent implements OnInit {

  @Input() invoiceSearch: InvoiceSearch = {} as InvoiceSearch;
  @Input() isForDashboard: boolean = false;
  invoices: InvoiceSearchResult[] | undefined;
  availableColumns: SortTableColumn[] = [];
  columnToDisplayOnDashboard: string[] = ["description", "affaires", "invoicePayer", "totalPrice"];
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];
  @Output() actionBypass: EventEmitter<Invoice> = new EventEmitter<Invoice>();
  @Input() overrideIconAction: string = "";
  @Input() overrideTooltipAction: string = "";
  @Input() defaultStatusFilter: InvoiceStatus[] | undefined;

  invoiceStatusSend = this.constantService.getInvoiceStatusSend();

  constructor(
    private appService: AppService,
    private invoiceSearchResultService: InvoiceSearchResultService,
    private constantService: ConstantService,
    private formBuilder: FormBuilder,
    private router: Router,
  ) { }

  ngOnInit() {
    if (!this.defaultStatusFilter && !this.isForDashboard)
      this.defaultStatusFilter = [this.invoiceStatusSend];
    if (!this.isForDashboard)
      this.appService.changeHeaderTitle("Factures & paiements");
    this.putDefaultPeriod();
    this.availableColumns = [];
    this.availableColumns.push({ id: "id", fieldName: "invoiceId", label: "N° de facture" } as SortTableColumn);
    this.availableColumns.push({ id: "status", fieldName: "invoiceStatus", label: "Status" } as SortTableColumn);
    this.availableColumns.push({ id: "customerOrderId", fieldName: "customerOrderId", label: "N° de commande", actionLinkFunction: getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la commande associée" } as SortTableColumn);
    this.availableColumns.push({ id: "customerOrderName", fieldName: "customerOrderLabel", label: "Donneur d'ordre", actionLinkFunction: getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la fiche du donneur d'ordre" } as SortTableColumn);
    this.availableColumns.push({ id: "responsable", fieldName: "responsableLabel", label: "Responsable" } as SortTableColumn);
    this.availableColumns.push({ id: "affaires", fieldName: "affaireLabel", label: "Affaire(s)", isShrinkColumn: true } as SortTableColumn);
    this.availableColumns.push({ id: "invoicePayer", fieldName: "billingLabel", label: "Payeur" } as SortTableColumn);
    this.availableColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date d'émission", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Montant TTC", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "description", fieldName: "customerOrderDescription", label: "Description" } as SortTableColumn);
    this.availableColumns.push({ id: "payments", fieldName: "paymentId", label: "Paiement(s) associé(s)" } as SortTableColumn);

    this.setColumns();

    if (this.overrideIconAction == "") {
      this.tableAction.push({
        actionIcon: "settings", actionName: "Voir le détail de la facture / associer", actionLinkFunction: (action: SortTableAction, element: any) => {
          if (element)
            return ['/invoicing', element.invoiceId];
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

    if (this.isForDashboard && !this.invoices && this.invoiceSearch) {
      this.putDefaultPeriod();
      this.searchInvoices();
    }
  }

  invoiceForm = this.formBuilder.group({
  });

  setColumns() {
    this.displayedColumns = [];
    if (this.availableColumns && this.columnToDisplayOnDashboard && this.isForDashboard) {
      for (let availableColumn of this.availableColumns)
        for (let columnToDisplay of this.columnToDisplayOnDashboard)
          if (availableColumn.id == columnToDisplay)
            this.displayedColumns.push(availableColumn);
    }
    else
      this.displayedColumns.push(...this.availableColumns);
  }

  putDefaultPeriod() {
    if (!this.invoiceSearch.startDate && !this.invoiceSearch.endDate) {
      this.invoiceSearch.startDate = new Date();
      this.invoiceSearch.endDate = new Date();
      this.invoiceSearch.startDate.setDate(this.invoiceSearch.endDate.getDate() - 90);
    }
  }

  searchInvoices() {
    if (this.invoiceForm.valid && this.invoiceSearch.startDate && this.invoiceSearch.endDate) {
      this.invoiceSearchResultService.getInvoicesList(this.invoiceSearch).subscribe(response => {
        this.invoices = response;
      })
    }
  }

  addInvoice() {
    this.router.navigate(['/invoicing/add/', ""]);
  }
}
