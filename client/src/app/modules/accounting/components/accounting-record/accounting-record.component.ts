import { CdkDragEnd, Point } from '@angular/cdk/drag-drop';
import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { formatDateForSortTable, formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { AccountingRecord } from '../../model/AccountingRecord';
import { AccountingRecordSearch } from '../../model/AccountingRecordSearch';
import { AccountingRecordService } from '../../services/accounting.record.service';

@Component({
  selector: 'accounting-record',
  templateUrl: './accounting-record.component.html',
  styleUrls: ['./accounting-record.component.css']
})
export class AccountingRecordComponent implements OnInit {

  accountingRecordSearch: AccountingRecordSearch = {} as AccountingRecordSearch;

  constructor(
    private formBuilder: FormBuilder,
    private accountingRecordService: AccountingRecordService,
    private userPreferenceService: UserPreferenceService,
    private appService: AppService,
  ) { }

  accountingRecords: AccountingRecord[] | undefined;
  displayedColumnsTotal: string[] = ['label', 'debit', 'credit'];
  accumulatedDataSource = new MatTableDataSource<AccountingRecord>();
  currentUserPosition: Point = { x: 0, y: 0 };
  displayedColumns: SortTableColumn[] = [] as Array<SortTableColumn>;


  ngOnInit() {
    this.accountingRecordSearch.startDate = new Date(new Date().getFullYear(), 0, 1);
    this.accountingRecordSearch.endDate = new Date(new Date().getFullYear(), 11, 31);

    // Column init
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "operationId", fieldName: "operationId", label: "N° d'écriture" } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingDateTime", fieldName: "accountingDateTime", label: "Date d'écriture", valueFonction: this.formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "operationDateTime", fieldName: "operationDateTime", label: "Date d'opération", valueFonction: this.formatDateTimeForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingJournal", fieldName: "accountingJournal", label: "Journal", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column) return element.accountingJournal.label; return "" } } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingAccountNumber", fieldName: "accountingAccountNumber", label: "N° de compte", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column) return element.accountingAccount.accountingAccountNumber + "-" + element.accountingAccount.accountingAccountSubNumber; return "" } } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingAccountLabel", fieldName: "accountingAccountLabel", label: "Libellé du compte", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column) return element.accountingAccount.label; return "" }, isShrinkColumn: true } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingDocumentNumber", fieldName: "accountingDocumentNumber", label: "N° de pièce justificative" } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingDocumentDate", fieldName: "accountingDocumentDate", label: "Date pièce justificative", valueFonction: this.formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "debitAmount", fieldName: "debitAmount", label: "Débit", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "creditAmount", fieldName: "creditAmount", label: "Crédit", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "letteringNumber", fieldName: "letteringNumber", label: "Lettrage" } as SortTableColumn);
    this.displayedColumns.push({ id: "letteringDate", fieldName: "letteringDate", label: "Date de lettrage", valueFonction: this.formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "debitAccumulation", fieldName: "debitAccumulation", label: "Cumul débit", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "creditAccumulation", fieldName: "creditAccumulation", label: "Cumul crédit", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "balance", fieldName: "balance", label: "Solde", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
  }

  formatEurosForSortTable = formatEurosForSortTable;
  formatDateForSortTable = formatDateForSortTable;
  formatDateTimeForSortTable = formatDateTimeForSortTable;

  accountingRecordForm = this.formBuilder.group({
  });

  putEndDateSameYear() {
    if (this.accountingRecordSearch.startDate && this.accountingRecordSearch.endDate
      && this.accountingRecordSearch.startDate.getFullYear() != this.accountingRecordSearch.endDate.getFullYear()) {
      this.accountingRecordSearch.endDate = new Date(this.accountingRecordSearch.startDate.getFullYear(), 11, 31);
    }
  }

  exportGrandLivre() {
    this.accountingRecordService.exportGrandLivre(this.accountingRecordSearch.accountingClass, this.accountingRecordSearch.startDate!, this.accountingRecordSearch.endDate!);
  }

  exportAllGrandLivre() {
    this.accountingRecordService.exportAllGrandLivre(this.accountingRecordSearch.startDate!, this.accountingRecordSearch.endDate!);
  }

  exportJournal() {
    this.accountingRecordService.exportJournal(this.accountingRecordSearch.accountingJournal!, this.accountingRecordSearch.startDate!, this.accountingRecordSearch.endDate!);
  }

  exportAccountingAccount() {
    this.accountingRecordService.exportAccountingAccount(this.accountingRecordSearch.accountingAccount!, this.accountingRecordSearch.startDate!, this.accountingRecordSearch.endDate!);
  }

  searchRecords() {
    this.restoreTotalDivPosition();
    if (!this.accountingRecordSearch.startDate || !this.accountingRecordSearch.endDate) {
      this.appService.displaySnackBar("🙄 Merci de saisir une plage de recherche", false, 10);
      return;
    }
    if (!this.accountingRecordSearch.accountingAccount && !this.accountingRecordSearch.accountingClass && !this.accountingRecordSearch.accountingJournal) {
      this.appService.displaySnackBar("🙄 Merci de saisir au moins un critère de recherche", false, 10);
      return;
    }
    this.accountingRecordService.searchAccountingRecords(this.accountingRecordSearch).subscribe(response => {
      this.accountingRecords = response;
      this.accountingRecords.sort((a, b) => this.sortRecords(a, b));
      this.computeBalanceAndDebitAndCreditAccumulation();

    });
  }

  sortRecords(a: AccountingRecord, b: AccountingRecord): number {
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
      if (a.operationDateTime && b.operationDateTime && a.operationDateTime.getTime() != b.operationDateTime.getTime()) {
        return (a.operationDateTime > b.operationDateTime) ? 1 : -1;
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
        accountingRecord.balance = balance;
        accountingRecord.debitAccumulation = debit;
        accountingRecord.creditAccumulation = credit;
      }

      let accumulatedData = [];
      let totalLine = {} as any;
      totalLine.label = "Total";
      totalLine.debit = debit;
      totalLine.credit = credit;
      accumulatedData.push(totalLine);

      let balanceLine = {} as any;
      balanceLine.label = "Balance";
      balanceLine.credit = balance;
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

}
