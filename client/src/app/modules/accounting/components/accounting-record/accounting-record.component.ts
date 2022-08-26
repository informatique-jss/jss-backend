import { CdkDragEnd, Point } from '@angular/cdk/drag-drop';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
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

  accountingRecordDataSource = new MatTableDataSource<AccountingRecord>();
  accumulatedDataSource = new MatTableDataSource<AccountingRecord>();
  @ViewChild(MatSort) sort!: MatSort;
  accountingRecords: AccountingRecord[] | undefined;
  displayedColumns: string[] = ['operationId', 'accountingDateTime', 'operationDateTime', 'accountingJournal', 'accountingAccountNumber', 'accountingAccountLabel', 'accountingDocumentNumber', 'accountingDocumentDate', 'label', 'debitAmount', 'creditAmount', 'letteringNumber', 'letteringDate', 'debitAccumulation', 'creditAccumulation', 'balance'];
  displayedColumnsTotal: string[] = ['label', 'debit', 'credit'];
  currentUserPosition: Point = { x: 0, y: 0 };


  ngOnInit() {
    this.accountingRecordSearch.startDate = new Date(new Date().getFullYear(), 0, 1)
    this.accountingRecordSearch.endDate = new Date(new Date().getFullYear(), 11, 31)
  }

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
      this.accountingRecordDataSource.data = this.accountingRecords;

      setTimeout(() => {
        this.accountingRecordDataSource.sort = this.sort;
        this.accountingRecordDataSource.sortingDataAccessor = (accountingRecord: AccountingRecord, property) => {
          switch (property) {
            case 'label': return accountingRecord.label;
            case 'operationId': return accountingRecord.operationId;
            case 'accountingDateTime': return accountingRecord.accountingDateTime.getTime();
            case 'operationDateTime': return accountingRecord.operationDateTime.getTime();
            case 'accountingAccountNumber': return accountingRecord.accountingAccount.accountingAccountNumber + accountingRecord.accountingAccount.accountingAccountSubNumber;
            case 'accountingAccountLabel': return accountingRecord.accountingAccount.label;
            case 'accountingDocumentNumber': return (accountingRecord.invoice ? accountingRecord.invoice.id : accountingRecord.manualAccountingDocumentNumber);
            case 'accountingDocumentDate': return (accountingRecord.invoice ? accountingRecord.invoice.createdDate.getTime() : accountingRecord.manualAccountingDocumentDate.getTime());
            case 'creditAmount': return accountingRecord.creditAmount;
            case 'debitAmount': return accountingRecord.debitAmount;
            case 'letteringNumber': return accountingRecord.letteringNumber;
            case 'letteringDate': return accountingRecord.letteringDate.getTime();
            case 'debitAccumulation': return accountingRecord.debitAccumulation;
            case 'creditAccumulation': return accountingRecord.creditAccumulation;
            default: return accountingRecord.label;
          }
        };

        this.accountingRecordDataSource.filterPredicate = (data: any, filter) => {
          const dataStr = JSON.stringify(data).toLowerCase();
          return dataStr.indexOf(filter) != -1;
        }
      });
    })
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
