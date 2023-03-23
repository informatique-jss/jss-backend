import { AfterContentChecked, ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateForSortTable, formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
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
export class InvoiceListComponent implements OnInit, AfterContentChecked {

  @Input() invoiceSearch: InvoiceSearch = {} as InvoiceSearch;
  @Input() isForDashboard: boolean = false;
  @Input() isForTiersIntegration: boolean = false;
  @Input() isForPaymentAssocationIntegration: boolean = false;
  invoices: InvoiceSearchResult[] | undefined;
  availableColumns: SortTableColumn[] = [];
  columnToDisplayOnDashboard: string[] = ["description", "affaires", "invoicePayer", "totalPrice"];
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];
  @Output() actionBypass: EventEmitter<InvoiceSearchResult> = new EventEmitter<InvoiceSearchResult>();
  @Input() overrideIconAction: string = "";
  @Input() overrideTooltipAction: string = "";
  @Input() defaultStatusFilter: InvoiceStatus[] | undefined;

  bookmark: InvoiceSearch | undefined;

  invoiceStatusSend = this.constantService.getInvoiceStatusSend();

  constructor(
    private appService: AppService,
    private invoiceSearchResultService: InvoiceSearchResultService,
    private constantService: ConstantService,
    private changeDetectorRef: ChangeDetectorRef,
    private formBuilder: FormBuilder,
    private habilitationService: HabilitationsService,
    private userPreferenceService: UserPreferenceService,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  canAddNewInvoice() {
    return this.habilitationService.canAddNewInvoice();
  }

  ngOnInit() {
    if (!this.defaultStatusFilter && !this.isForDashboard && !this.isForTiersIntegration)
      this.defaultStatusFilter = [this.invoiceStatusSend];

    this.bookmark = this.userPreferenceService.getUserSearchBookmark("invoices") as InvoiceSearch;
    if (this.bookmark && !this.isForDashboard && !this.isForTiersIntegration) {
      this.invoiceSearch = {} as InvoiceSearch;
      this.invoiceSearch.invoiceStatus = this.bookmark.invoiceStatus;
      this.defaultStatusFilter = this.bookmark.invoiceStatus;
      this.invoiceSearch.maxAmount = this.bookmark.maxAmount;
      this.invoiceSearch.minAmount = this.bookmark.minAmount;
    }

    if (!this.isForDashboard && !this.isForTiersIntegration)
      this.appService.changeHeaderTitle("Factures & paiements");
    this.availableColumns = [];
    this.availableColumns.push({ id: "id", fieldName: "invoiceId", label: "N° de facture" } as SortTableColumn);
    this.availableColumns.push({ id: "status", fieldName: "invoiceStatus", label: "Status" } as SortTableColumn);
    this.availableColumns.push({ id: "customerOrderId", fieldName: "customerOrderId", label: "N° de commande", actionLinkFunction: getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la commande associée" } as SortTableColumn);
    this.availableColumns.push({ id: "customerOrderName", fieldName: "customerOrderLabel", label: "Donneur d'ordre", actionLinkFunction: getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la fiche du donneur d'ordre" } as SortTableColumn);
    this.availableColumns.push({ id: "tiers", fieldName: "tiersLabel", label: "Tiers", actionLinkFunction: getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la fiche du tiers" } as SortTableColumn);
    this.availableColumns.push({ id: "responsable", fieldName: "responsableLabel", label: "Responsable" } as SortTableColumn);
    this.availableColumns.push({ id: "affaires", fieldName: "affaireLabel", label: "Affaire(s)", isShrinkColumn: true } as SortTableColumn);
    this.availableColumns.push({ id: "providerLabel", fieldName: "providerLabel", label: "Fournisseur" } as SortTableColumn);
    this.availableColumns.push({ id: "invoicePayer", fieldName: "billingLabel", label: "Payeur" } as SortTableColumn);
    this.availableColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date d'émission", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Montant TTC", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "description", fieldName: "customerOrderDescription", label: "Description" } as SortTableColumn);
    this.availableColumns.push({ id: "payments", fieldName: "paymentId", label: "Paiement(s) associé(s)" } as SortTableColumn);
    this.availableColumns.push({ id: "dueDate", fieldName: "dueDate", label: "Date d'échéance", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "firstReminderDateTime", fieldName: "firstReminderDateTime", label: "Date de première relance", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "secondReminderDateTime", fieldName: "secondReminderDateTime", label: "Date de seconde relance", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "thirdReminderDateTime", fieldName: "thirdReminderDateTime", label: "Date de troisième relance", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "lastFollowupDate", fieldName: "lastFollowupDate", label: "Dernier suivi", valueFonction: formatDateForSortTable } as SortTableColumn);

    this.setColumns();

    if (this.overrideIconAction == "") {
      this.tableAction.push({
        actionIcon: "point_of_sale", actionName: "Voir le détail de la facture / associer", actionLinkFunction: (action: SortTableAction, element: any) => {
          if (element)
            return ['/invoicing/view/', element.invoiceId];
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

    if ((this.isForDashboard || this.isForTiersIntegration) && !this.invoices && this.invoiceSearch) {
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

  searchInvoices() {
    if (this.invoiceForm.valid) {
      if (!this.isForDashboard)
        this.userPreferenceService.setUserSearchBookmark(this.invoiceSearch, "invoices");

      this.invoiceSearchResultService.getInvoicesList(this.invoiceSearch).subscribe(response => {
        this.invoices = response;
      })
    }
  }

  addInvoice(event: any) {
    this.appService.openRoute(event, 'invoicing/add/null', null);
  }
}
