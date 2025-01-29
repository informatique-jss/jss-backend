import { Point } from '@angular/cdk/drag-drop';
import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { formatDateForSortTable, formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { UploadAttachementDialogComponent } from 'src/app/modules/miscellaneous/components/upload-attachement-dialog/upload-attachement-dialog.component';
import { IAttachment } from 'src/app/modules/miscellaneous/model/IAttachment';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { SAGE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { AccountingRecordSearch } from '../../model/AccountingRecordSearch';
import { AccountingRecordSearchResult } from '../../model/AccountingRecordSearchResult';
import { AccountingRecordSearchResultService } from '../../services/accounting.record.search.result.service';

@Component({
  selector: 'accounting-pay-journal',
  templateUrl: './accounting-pay-journal.component.html',
  styleUrls: ['./accounting-pay-journal.component.css']
})
export class AccountingPayJournalComponent implements OnInit {

  @Input() accountingRecordSearch: AccountingRecordSearch = {} as AccountingRecordSearch;

  constructor(
    private formBuilder: FormBuilder,
    private userPreferenceService: UserPreferenceService,
    private appService: AppService,
    private accountingRecordSearchService: AccountingRecordSearchResultService,
    public uploadAttachementDialog: MatDialog,
    private habilitationService: HabilitationsService,
    private constantService: ConstantService,
  ) { }

  displayedColumns: SortTableColumn<AccountingRecordSearchResult>[] = [] as Array<SortTableColumn<AccountingRecordSearchResult>>;
  bookmark: AccountingRecordSearch | undefined;
  currentUserPosition: Point = { x: 0, y: 0 };
  accountingRecords: AccountingRecordSearchResult[] | undefined;
  uploadAttachementDialogRef: MatDialogRef<UploadAttachementDialogComponent> | undefined;

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "operationDateTime", fieldName: "operationDateTime", label: "Date d'opération", valueFonction: this.formatDateTimeForSortTable } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "operationId", fieldName: "operationId", label: "N° d'écriture" } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "accountingJournal", fieldName: "accountingJournalLabel", label: "Journal" } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "accountingAccountNumber", fieldName: "accountingAccountNumber", label: "N° de compte", valueFonction: (element: AccountingRecordSearchResult, column: SortTableColumn<AccountingRecordSearchResult>) => { if (element && column) return element.principalAccountingAccountCode + element.accountingAccountSubNumber; return "" } } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "accountingAccountLabel", fieldName: "accountingAccountLabel", label: "Libellé du compte", isShrinkColumn: true } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "debitAmount", fieldName: "debitAmount", label: "Débit", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "creditAmount", fieldName: "creditAmount", label: "Crédit", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé", isShrinkColumn: true } as SortTableColumn<AccountingRecordSearchResult>);

    this.bookmark = this.userPreferenceService.getUserSearchBookmark("accounting-record") as AccountingRecordSearch;
    if (this.bookmark) {
      this.accountingRecordSearch = this.bookmark;
      if (this.accountingRecordSearch.startDate)
        this.accountingRecordSearch.startDate = new Date(this.accountingRecordSearch.startDate);
      if (this.accountingRecordSearch.endDate)
        this.accountingRecordSearch.endDate = new Date(this.accountingRecordSearch.endDate);
      this.searchRecords();
    }
  }
  accountingRecordForm = this.formBuilder.group({
  });

  formatEurosForSortTable = formatEurosForSortTable;
  formatDateForSortTable = formatDateForSortTable;
  formatDateTimeForSortTable = formatDateTimeForSortTable;

  searchRecords() {
    if (!this.accountingRecordSearch.startDate || !this.accountingRecordSearch.endDate) {
      this.appService.displaySnackBar("🙄 Merci de saisir une plage de recherche", false, 10);
      return;
    }

    if (this.accountingRecordSearch.startDate)
      this.accountingRecordSearch.startDate = new Date(this.accountingRecordSearch.startDate.setHours(12));
    if (this.accountingRecordSearch.endDate)
      this.accountingRecordSearch.endDate = new Date(this.accountingRecordSearch.endDate.setHours(12));

    if (!this.accountingRecordSearch)
      this.userPreferenceService.setUserSearchBookmark(this.accountingRecordSearch, "accounting-record");

    this.accountingRecordSearch.isFromSage = true;
    this.accountingRecordSearchService.searchAccountingRecords(this.accountingRecordSearch).subscribe(response => {
      this.accountingRecords = response;
    });
  }

  setCurentMonth() {
    let d = new Date();
    this.accountingRecordSearch.startDate = new Date(d.getFullYear(), d.getMonth(), 1, 12, 0, 0);
    let d2 = new Date();
    this.accountingRecordSearch.endDate = new Date(d2.getFullYear(), d2.getMonth() + 1, 0, 12, 0, 0);
  }

  setCurentFiscalYear() {
    let d = new Date();
    this.accountingRecordSearch.startDate = new Date(d.getFullYear(), 0, 1, 12, 0, 0);
    let d2 = new Date();
    this.accountingRecordSearch.endDate = new Date(d2.getFullYear(), 11, 31, 12, 0, 0);
  }

  getColumnLink(column: SortTableColumn<AccountingRecordSearchResult>, element: AccountingRecordSearchResult) {
    if (element && column.id == "invoice" && element.invoiceId) {
      return ['/invoicing/view', element.invoiceId];
    }
    if (element && column.id == "customerOrder" && element.customerId) {
      return ['/order', element.customerId];
    }
    return null;
  }

  canImportPnmFile() {
    return this.habilitationService.canImportPnmFile();
  }

  importPnmFile() {
    this.uploadAttachementDialogRef = this.uploadAttachementDialog.open(UploadAttachementDialogComponent, {
    });
    this.uploadAttachementDialogRef.componentInstance.entity = { id: 1 } as IAttachment;;
    this.uploadAttachementDialogRef.componentInstance.entityType = SAGE_ENTITY_TYPE.entityType;
    this.uploadAttachementDialogRef.componentInstance.forcedAttachmentType = this.constantService.getAttachmentTypeBillingClosure();
    this.uploadAttachementDialogRef.afterClosed().subscribe(response => { });
  }
}
