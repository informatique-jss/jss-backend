import { AfterContentChecked, ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateForSortTable, formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { UploadAttachmentService } from 'src/app/modules/miscellaneous/services/upload.attachment.service';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { ITiers } from '../../../tiers/model/ITiers';
import { InvoiceSearch } from '../../model/InvoiceSearch';
import { InvoiceSearchResult } from '../../model/InvoiceSearchResult';
import { InvoiceStatus } from '../../model/InvoiceStatus';
import { InvoiceSearchResultService } from '../../services/invoice.search.result.service';
import { InvoiceService } from '../../services/invoice.service';
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
  availableColumns: SortTableColumn<InvoiceSearchResult>[] = [];
  columnToDisplayOnDashboard: string[] = ["description", "affaires", "invoicePayer", "totalPrice"];
  displayedColumns: SortTableColumn<InvoiceSearchResult>[] = [];
  tableAction: SortTableAction<InvoiceSearchResult>[] = [];
  @Output() actionBypass: EventEmitter<InvoiceSearchResult> = new EventEmitter<InvoiceSearchResult>();
  @Input() overrideIconAction: string = "";
  @Input() overrideTooltipAction: string = "";
  @Input() defaultStatusFilter: InvoiceStatus[] | undefined;
  searchedTiers: IndexEntity | undefined;

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
    private uploadAttachmentService: UploadAttachmentService,
    private invoiceService: InvoiceService
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  canAddNewInvoice() {
    return this.habilitationService.canAddNewInvoice();
  }

  ngOnChanges(change: SimpleChanges) {
  }

  ngOnInit() {

    if (!this.defaultStatusFilter && !this.isForDashboard && !this.isForTiersIntegration)
      this.defaultStatusFilter = [this.invoiceStatusSend];

    this.bookmark = this.userPreferenceService.getUserSearchBookmark("invoices") as InvoiceSearch;
    if (this.bookmark && !this.isForDashboard && !this.isForTiersIntegration && !this.isForPaymentAssocationIntegration) {
      this.defaultStatusFilter = this.bookmark.invoiceStatus;
      this.invoiceSearch = this.bookmark;
      this.invoiceSearch.invoiceStatus = this.bookmark.invoiceStatus;
      if (this.invoiceSearch.startDate)
        this.invoiceSearch.startDate = new Date(this.invoiceSearch.startDate);
      if (this.invoiceSearch.endDate)
        this.invoiceSearch.endDate = new Date(this.invoiceSearch.endDate);
      this.searchInvoices();
    }

    if (!this.isForDashboard && !this.isForTiersIntegration)
      this.appService.changeHeaderTitle("Factures & paiements");
    this.availableColumns = [];
    this.availableColumns.push({ id: "id", fieldName: "invoiceId", label: "N° de facture" } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "status", fieldName: "invoiceStatus", label: "Status", statusFonction: (element: InvoiceSearchResult) => { return element.invoiceStatusCode }, displayAsStatus: true } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "customerOrderId", fieldName: "customerOrderId", label: "N° de commande", actionLinkFunction: getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la commande associée" } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "customerOrderName", fieldName: "customerOrderLabel", label: "Donneur d'ordre", actionLinkFunction: getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la fiche du donneur d'ordre" } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "tiers", fieldName: "tiersLabel", label: "Tiers", actionLinkFunction: getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la fiche du tiers" } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "responsable", fieldName: "responsableLabel", label: "Responsable" } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "salesEmployeeId", fieldName: "salesEmployeeId", label: "Commercial", displayAsEmployee: true } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "affaires", fieldName: "affaireLabel", label: "Affaire(s)", isShrinkColumn: true } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "providerLabel", fieldName: "providerLabel", label: "Fournisseur" } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "invoicePayer", fieldName: "billingLabel", label: "Payeur" } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "invoiceRecipient", fieldName: "invoiceRecipient", label: "Destinataire" } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "invoiceBillingType", fieldName: "invoiceBillingType", label: "Libellé à" } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date d'émission", valueFonction: formatDateTimeForSortTable } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Montant TTC", valueFonction: formatEurosForSortTable } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "manualAccountingDocumentNumber", fieldName: "manualAccountingDocumentNumber", label: "N° pièce" } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "description", fieldName: "customerOrderDescription", label: "Description" } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "payments", fieldName: "paymentId", label: "Paiement(s) associé(s)" } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "dueDate", fieldName: "dueDate", label: "Date d'échéance", valueFonction: formatDateForSortTable } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "manualAccountingDocumentDate", fieldName: "manualAccountingDocumentDate", label: "Date pièce", valueFonction: formatDateForSortTable } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "firstReminderDateTime", fieldName: "firstReminderDateTime", label: "Date de première relance", valueFonction: formatDateForSortTable } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "secondReminderDateTime", fieldName: "secondReminderDateTime", label: "Date de seconde relance", valueFonction: formatDateForSortTable } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "thirdReminderDateTime", fieldName: "thirdReminderDateTime", label: "Date de troisième relance", valueFonction: formatDateForSortTable } as SortTableColumn<InvoiceSearchResult>);
    this.availableColumns.push({ id: "lastFollowupDate", fieldName: "lastFollowupDate", label: "Dernier suivi", valueFonction: formatDateForSortTable } as SortTableColumn<InvoiceSearchResult>);

    this.setColumns();

    if (this.overrideIconAction == "") {
      this.tableAction.push({
        actionIcon: "point_of_sale", actionName: "Voir le détail de la facture / associer", actionLinkFunction: (action: SortTableAction<InvoiceSearchResult>, element: InvoiceSearchResult) => {
          if (element)
            return ['/invoicing/view', element.invoiceId];
          return undefined;
        }, display: true,
      } as SortTableAction<InvoiceSearchResult>);
    } else {
      this.tableAction.push({
        actionIcon: this.overrideIconAction, actionName: this.overrideTooltipAction, actionClick: (column: SortTableAction<InvoiceSearchResult>, element: InvoiceSearchResult, event: any) => {
          this.actionBypass.emit(element);
        }, display: true,
      } as SortTableAction<InvoiceSearchResult>);

    };

    if ((this.isForDashboard || this.isForTiersIntegration) && !this.invoices && this.invoiceSearch) {
      if (this.isForTiersIntegration && !this.invoiceSearch.invoiceStatus) {
        this.invoiceSearch.invoiceStatus = [this.constantService.getInvoiceStatusSend()];
      }
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
      if (this.searchedTiers) {
        this.invoiceSearch.customerOrders = [];
        this.invoiceSearch.customerOrders.push({ id: this.searchedTiers.entityId } as ITiers);
      }

      if (!this.isForDashboard && !this.isForTiersIntegration && !this.isForPaymentAssocationIntegration)
        this.userPreferenceService.setUserSearchBookmark(this.invoiceSearch, "invoices");

      this.invoiceSearchResultService.getInvoicesList(this.invoiceSearch).subscribe(response => {
        this.invoices = response;
      })
    }
  }

  addInvoice(event: any) {
    this.appService.openRoute(event, 'invoicing/add/null', null);
  }

  downloadAllFiles() {
    let count = 0;
    if (this.invoices)
      for (let invoice of this.invoices) {
        this.invoiceService.getInvoiceById(invoice.invoiceId).subscribe(completeInvoice => {
          if (completeInvoice.attachments) {
            for (let attachement of completeInvoice.attachments) {
              if (attachement.attachmentType.id == this.constantService.getAttachmentTypeInvoice().id && count < 100) {
                this.uploadAttachmentService.downloadAttachment(attachement);
                count++;
              }
            }
          }
        })
      }
  }
}
