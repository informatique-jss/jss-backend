import { CdkDragEnd, Point } from '@angular/cdk/drag-drop';
import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { formatDateForSortTable, formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { instanceOfConfrere } from '../../../../libs/TypeHelper';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { ITiers } from '../../../tiers/model/ITiers';
import { AccountingRecord } from '../../model/AccountingRecord';
import { AccountingRecordSearch } from '../../model/AccountingRecordSearch';
import { AccountingRecordSearchResult } from '../../model/AccountingRecordSearchResult';
import { AccountingRecordSearchResultService } from '../../services/accounting.record.search.result.service';
import { AccountingRecordService } from '../../services/accounting.record.service';

@Component({
  selector: 'accounting-record',
  templateUrl: './accounting-record.component.html',
  styleUrls: ['./accounting-record.component.css']
})
export class AccountingRecordComponent implements OnInit {

  // Used for integration in tiers and responsable component
  @Input() tiersToDisplay: ITiers | undefined;
  accountingRecordSearch: AccountingRecordSearch = {} as AccountingRecordSearch;

  constructor(
    private formBuilder: FormBuilder,
    private accountingRecordService: AccountingRecordService,
    private userPreferenceService: UserPreferenceService,
    private appService: AppService,
    private accountingRecordSearchService: AccountingRecordSearchResultService,
    public deleteAccountingRecordDialog: MatDialog,
    private habilitationService: HabilitationsService,
  ) { }

  accountingRecords: AccountingRecordSearchResult[] | undefined;
  displayedColumnsTotal: string[] = ['label', 'debit', 'credit'];
  accumulatedDataSource = new MatTableDataSource<AccountingRecord>();
  currentUserPosition: Point = { x: 0, y: 0 };
  displayedColumns: SortTableColumn[] = [] as Array<SortTableColumn>;
  tableAction: SortTableAction[] = [] as Array<SortTableAction>;

  canAddNewAccountingRecord() {
    return this.habilitationService.canAddNewAccountingRecord();
  }

  ngOnInit() {
    // Column init
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "invoice", fieldName: "invoiceId", label: "Facture", actionLinkFunction: this.getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la facture associée" } as SortTableColumn);
    this.displayedColumns.push({ id: "customerOrder", fieldName: "customerId", label: "Commande", actionLinkFunction: this.getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la commande associée" } as SortTableColumn);
    this.displayedColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire(s)" } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingDateTime", fieldName: "accountingDateTime", label: "Date d'écriture", valueFonction: this.formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "operationDateTime", fieldName: "operationDateTime", label: "Date d'opération", valueFonction: this.formatDateTimeForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "operationId", fieldName: "operationId", label: "N° d'écriture" } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingJournal", fieldName: "accountingJournalLabel", label: "Journal" } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingAccountNumber", fieldName: "accountingAccountNumber", label: "N° de compte", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column) return element.principalAccountingAccountCode + element.accountingAccountSubNumber; return "" } } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingAccountLabel", fieldName: "accountingAccountLabel", label: "Libellé du compte", isShrinkColumn: true } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingDocumentNumber", fieldName: "manualAccountingDocumentNumber", label: "N° de pièce justificative" } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingDocumentDate", fieldName: "manualAccountingDocumentDate", label: "Date pièce justificative", valueFonction: this.formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "debitAmount", fieldName: "debitAmount", label: "Débit", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "creditAmount", fieldName: "creditAmount", label: "Crédit", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé", isShrinkColumn: true } as SortTableColumn);
    this.displayedColumns.push({ id: "letteringNumber", fieldName: "letteringNumber", label: "Lettrage" } as SortTableColumn);
    this.displayedColumns.push({ id: "letteringDate", fieldName: "letteringDate", label: "Date de lettrage", valueFonction: this.formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "debitAccumulation", fieldName: "debitAccumulation", label: "Cumul débit", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "creditAccumulation", fieldName: "creditAccumulation", label: "Cumul crédit", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "balance", fieldName: "balance", label: "Solde", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "payment", fieldName: "paymentId", label: "Paiement", actionIcon: "visibility", actionTooltip: "Voir le paiement associé" } as SortTableColumn);
    this.displayedColumns.push({ id: "deposit", fieldName: "depositId", label: "Acompte", actionIcon: "visibility", actionTooltip: "Voir l'acompte associé" } as SortTableColumn);

    if (this.tiersToDisplay && this.tiersToDisplay.id) {
      if (instanceOfConfrere(this.tiersToDisplay))
        this.accountingRecordSearch.confrereId = this.tiersToDisplay.id;
      else
        this.accountingRecordSearch.tiersId = this.tiersToDisplay.id;
      this.accountingRecordSearch.hideLettered = true;
      this.searchRecords();
    }
  }

  formatEurosForSortTable = formatEurosForSortTable;
  formatDateForSortTable = formatDateForSortTable;
  formatDateTimeForSortTable = formatDateTimeForSortTable;

  accountingRecordForm = this.formBuilder.group({
  });

  getColumnLink(column: SortTableColumn, element: any) {
    if (element && column.id == "invoice" && element.invoiceId) {
      return ['/invoicing/view', element.invoiceId];
    }
    if (element && column.id == "customerOrder" && element.customerId) {
      return ['/order', element.customerId];
    }
    return null;
  }

  putEndDateSameYear() {
    if (this.accountingRecordSearch.startDate && this.accountingRecordSearch.endDate
      && this.accountingRecordSearch.startDate.getFullYear() != this.accountingRecordSearch.endDate.getFullYear()) {
      this.accountingRecordSearch.endDate = new Date(this.accountingRecordSearch.startDate.getFullYear(), 11, 31);
    }
  }

  exportGrandLivre() {
    if (this.accountingRecordSearch.startDate)
      this.accountingRecordService.exportGrandLivre(this.accountingRecordSearch.accountingClass, new Date(this.accountingRecordSearch.startDate.setHours(12)), this.accountingRecordSearch.endDate!);
  }

  exportAllGrandLivre() {
    if (this.accountingRecordSearch.startDate)
      this.accountingRecordService.exportAllGrandLivre(new Date(this.accountingRecordSearch.startDate.setHours(12)), this.accountingRecordSearch.endDate!);
  }

  exportJournal() {
    if (this.accountingRecordSearch.startDate)
      this.accountingRecordService.exportJournal(this.accountingRecordSearch.accountingJournal!, new Date(this.accountingRecordSearch.startDate.setHours(12)), this.accountingRecordSearch.endDate!);
  }

  exportAccountingAccount() {
    if (this.accountingRecordSearch.startDate)
      this.accountingRecordService.exportAccountingAccount(this.accountingRecordSearch.accountingAccount!, new Date(this.accountingRecordSearch.startDate.setHours(12)), this.accountingRecordSearch.endDate!);
  }

  createAccountingRecords(event: any) {
    this.appService.openRoute(event, '/accounting/add', null);
  }

  searchRecords() {
    this.restoreTotalDivPosition();
    if (!this.accountingRecordSearch.tiersId && !this.accountingRecordSearch.confrereId) {
      if (this.tiersToDisplay == undefined && (!this.accountingRecordSearch.startDate || !this.accountingRecordSearch.endDate)) {
        this.appService.displaySnackBar("🙄 Merci de saisir une plage de recherche", false, 10);
        return;
      }
      if (this.tiersToDisplay == undefined && !this.accountingRecordSearch.accountingAccount && !this.accountingRecordSearch.accountingClass && !this.accountingRecordSearch.accountingJournal) {
        this.appService.displaySnackBar("🙄 Merci de saisir au moins un critère de recherche", false, 10);
        return;
      }
    }
    if (this.accountingRecordSearch.startDate)
      this.accountingRecordSearch.startDate = new Date(this.accountingRecordSearch.startDate.setHours(12));
    this.accountingRecordSearchService.searchAccountingRecords(this.accountingRecordSearch).subscribe(response => {
      this.accountingRecords = response;
      this.accountingRecords.sort((a, b) => this.sortRecords(a, b));
      this.computeBalanceAndDebitAndCreditAccumulation();

    });
  }

  sortRecords(a: AccountingRecordSearchResult, b: AccountingRecordSearchResult): number {
    if (a && !b)
      return 1;
    if (!a && b)
      return -1;
    if (!a && !b)
      return 0;
    // First, by operation id
    if (a.operationId && b.operationId) {
      return (a.operationId > b.operationId) ? 1 : -1;
    } else {
      // Next by operation date
      if (a.operationDateTime && b.operationDateTime && (new Date(a.operationDateTime)).getTime() != (new Date(b.operationDateTime)).getTime()) {
        return (new Date(a.operationDateTime) > new Date(b.operationDateTime)) ? 1 : -1;
      } else {
        return (a.id > b.id) ? 1 : -1;
      }
    }
  }

  computeBalanceAndDebitAndCreditAccumulation() {
    if (this.accountingRecords) {
      this.accountingRecords.sort((a, b) => this.sortRecords(a, b));
      let credit: number = 0;
      let debit: number = 0;
      let balance: number = 0;
      for (let accountingRecord of this.accountingRecords) {
        if (accountingRecord.creditAmount) {
          credit += accountingRecord.creditAmount;
          balance += accountingRecord.creditAmount;
        }
        if (accountingRecord.debitAmount) {
          debit += accountingRecord.debitAmount;
          balance -= accountingRecord.debitAmount;
        }
        accountingRecord.balance = Math.round(balance * 100) / 100;
        accountingRecord.debitAccumulation = Math.round(debit * 100) / 100;
        accountingRecord.creditAccumulation = Math.round(credit * 100) / 100;
      }

      let accumulatedData = [];
      let totalLine = {} as any;
      totalLine.label = "Total";
      totalLine.debit = Math.round(debit * 100) / 100;
      totalLine.credit = Math.round(credit * 100) / 100;
      accumulatedData.push(totalLine);

      let balanceLine = {} as any;
      balanceLine.label = "Balance";
      balanceLine.credit = Math.round(balance * 100) / 100;
      accumulatedData.push(balanceLine);

      this.accumulatedDataSource.data = accumulatedData;

    }
  }

  dropTotalDiv(event: CdkDragEnd) {
    this.userPreferenceService.setUserTotalDivPosition(event.source.getFreeDragPosition());
  }

  restoreTotalDivPosition() {
    this.currentUserPosition = this.userPreferenceService.getUserTotalDivPosition();
  }

  restoreDefaultTotalDivPosition() {
    this.userPreferenceService.setUserTotalDivPosition({ x: 0, y: 0 });
    this.restoreTotalDivPosition();
  }

  downloadBillingClosureReceipt() {
    if (this.tiersToDisplay)
      this.accountingRecordService.downloadBillingClosureReceipt(this.tiersToDisplay);
  }

}
