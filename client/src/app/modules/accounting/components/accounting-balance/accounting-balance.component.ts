import { CdkDragEnd, Point } from '@angular/cdk/drag-drop';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { AccountingBalance } from '../../model/AccountingBalance';
import { AccountingBalanceSearch } from '../../model/AccountingBalanceSearch';
import { AccountingBalanceService } from '../../services/accounting.balance.service';

@Component({
  selector: 'accounting-balance',
  templateUrl: './accounting-balance.component.html',
  styleUrls: ['./accounting-balance.component.css']
})
export class AccountingBalanceComponent implements OnInit {

  accountingBalanceSearch: AccountingBalanceSearch = {} as AccountingBalanceSearch;

  constructor(
    private formBuilder: FormBuilder,
    private accountingBalanceService: AccountingBalanceService,
    private userPreferenceService: UserPreferenceService,
    private appService: AppService,
  ) { }

  accountingRecordDataSource = new MatTableDataSource<AccountingBalance>();
  accumulatedDataSource = new MatTableDataSource<AccountingBalance>();
  @ViewChild(MatSort) sort!: MatSort;
  accountingBalances: AccountingBalance[] | undefined;
  displayedColumns: string[] = ['accountingAccountNumber', 'accountingAccountLabel', 'debitAmount', 'creditAmount', 'echoir30', 'echoir60', 'echoir90', 'echu30', 'echu60', 'echu90'];
  displayedColumnsTotal: string[] = ['label', 'debit', 'credit'];
  currentUserPosition: Point = { x: 0, y: 0 };


  ngOnInit() {
    this.accountingBalanceSearch.startDate = new Date(new Date().getFullYear(), 0, 1)
    this.accountingBalanceSearch.endDate = new Date(new Date().getFullYear(), 11, 31)
  }

  accountingBalanceForm = this.formBuilder.group({
  });

  putEndDateSameYear() {
    if (this.accountingBalanceSearch.startDate && this.accountingBalanceSearch.endDate
      && this.accountingBalanceSearch.startDate.getFullYear() != this.accountingBalanceSearch.endDate.getFullYear()) {
      this.accountingBalanceSearch.endDate = new Date(this.accountingBalanceSearch.startDate.getFullYear(), 11, 31);
    }
  }

  exportBalance() {
    this.accountingBalanceService.exportBalance(this.accountingBalanceSearch);
  }

  searchRecords() {
    this.restoreTotalDivPosition();
    if (!this.accountingBalanceSearch.startDate || !this.accountingBalanceSearch.endDate) {
      this.appService.displaySnackBar("🙄 Merci de saisir une plage de recherche", false, 10);
      return;
    }
    this.accountingBalanceService.searchAccountingBalance(this.accountingBalanceSearch).subscribe(response => {
      this.accountingBalances = response;
      this.computeBalanceAndDebitAndCreditAccumulation();
      this.accountingBalances.sort((a, b) => this.sortRecords(a, b));
      this.accountingRecordDataSource.data = this.accountingBalances;

      setTimeout(() => {
        this.accountingRecordDataSource.sort = this.sort;
        this.accountingRecordDataSource.sortingDataAccessor = (accountingRecord: AccountingBalance, property) => {
          switch (property) {
            case 'accountingAccountNumber': return accountingRecord.accountingAccountNumber + accountingRecord.accountingAccountSubNumber;
            case 'accountingAccountLabel': return accountingRecord.accountingAccountLabel;
            case 'creditAmount': return accountingRecord.creditAmount;
            case 'debitAmount': return accountingRecord.debitAmount;
            case 'echoir30': return accountingRecord.echoir30;
            case 'echoir60': return accountingRecord.echoir60;
            case 'echoir90': return accountingRecord.echoir90;
            case 'echu30': return accountingRecord.echu30;
            case 'echu60': return accountingRecord.echu60;
            case 'echu90': return accountingRecord.echu90;
            default: return accountingRecord.accountingAccountNumber + accountingRecord.accountingAccountSubNumber;
          }
        };

        this.accountingRecordDataSource.filterPredicate = (data: any, filter) => {
          const dataStr = JSON.stringify(data).toLowerCase();
          return dataStr.indexOf(filter) != -1;
        }
      });
    })
  }

  sortRecords(a: AccountingBalance, b: AccountingBalance): number {
    if (a && !b)
      return 1;
    if (!a && b)
      return -1;
    if (!a && !b)
      return 0;
    let aNumber = a.accountingAccountNumber + a.accountingAccountSubNumber;
    let bNumber = b.accountingAccountNumber + b.accountingAccountSubNumber;
    return aNumber.localeCompare(bNumber);
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

  computeBalanceAndDebitAndCreditAccumulation() {
    if (this.accountingBalances) {
      let credit: number = 0;
      let debit: number = 0;
      let balance: number = 0;
      for (let accountingBalance of this.accountingBalances) {
        if (accountingBalance.creditAmount) {
          credit += accountingBalance.creditAmount;
          balance += accountingBalance.creditAmount;
        }
        if (accountingBalance.debitAmount) {
          debit += accountingBalance.debitAmount;
          balance -= accountingBalance.debitAmount;
        }
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

}
