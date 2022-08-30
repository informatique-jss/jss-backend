import { CdkDragEnd, Point } from '@angular/cdk/drag-drop';
import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { AccountingBalance } from '../../model/AccountingBalance';
import { AccountingBalanceSearch } from '../../model/AccountingBalanceSearch';
import { AccountingBalanceService } from '../../services/accounting.balance.service';

@Component({
  selector: 'accounting-balance-generale',
  templateUrl: './accounting-balance-generale.component.html',
  styleUrls: ['./accounting-balance-generale.component.css']
})
export class AccountingBalanceGeneraleComponent implements OnInit {

  accountingBalanceSearch: AccountingBalanceSearch = {} as AccountingBalanceSearch;

  displayedColumns: SortTableColumn[] = [] as Array<SortTableColumn>;

  constructor(
    private formBuilder: FormBuilder,
    private accountingBalanceService: AccountingBalanceService,
    private userPreferenceService: UserPreferenceService,
    private appService: AppService,
  ) { }

  accumulatedDataSource = new MatTableDataSource<AccountingBalance>();
  accountingBalances: AccountingBalance[] | undefined;
  displayedColumnsTotal: string[] = ['label', 'debit', 'credit'];
  currentUserPosition: Point = { x: 0, y: 0 };


  ngOnInit() {
    this.accountingBalanceSearch.startDate = new Date(new Date().getFullYear(), 0, 1);
    this.accountingBalanceSearch.endDate = new Date(new Date().getFullYear(), 11, 31);

    // Column init
    this.displayedColumns.push({ id: "accountingAccountNumber", fieldName: "accountingAccountNumber", label: "N° de compte", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column) return element.accountingAccountNumber; return "" } } as SortTableColumn);
    this.displayedColumns.push({ id: "debitAmount", fieldName: "debitAmount", label: "Débit", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "creditAmount", fieldName: "creditAmount", label: "Crédit", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "echoir30", fieldName: "echoir30", label: "Créances à échoir à -30 j", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "echoir60", fieldName: "echoir60", label: "Créances à échoir à -60 j", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "echoir90", fieldName: "echoir90", label: "Créances à échoir à +60 j", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "echu30", fieldName: "echu30", label: "Créances échues à -30 j", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "echu60", fieldName: "echu60", label: "Créances échues à -60 j", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "echu90", fieldName: "echu90", label: "Créances échues à +60 j", valueFonction: this.formatEurosForSortTable } as SortTableColumn);
  }

  formatEurosForSortTable = formatEurosForSortTable;

  accountingBalanceForm = this.formBuilder.group({
  });

  putEndDateSameYear() {
    if (this.accountingBalanceSearch.startDate && this.accountingBalanceSearch.endDate
      && this.accountingBalanceSearch.startDate.getFullYear() != this.accountingBalanceSearch.endDate.getFullYear()) {
      this.accountingBalanceSearch.endDate = new Date(this.accountingBalanceSearch.startDate.getFullYear(), 11, 31);
    }
  }

  exportBalance() {
    this.accountingBalanceService.exportBalanceGenerale(this.accountingBalanceSearch);
  }

  searchRecords() {
    this.restoreTotalDivPosition();
    if (!this.accountingBalanceSearch.startDate || !this.accountingBalanceSearch.endDate) {
      this.appService.displaySnackBar("🙄 Merci de saisir une plage de recherche", false, 10);
      return;
    }
    this.accountingBalanceService.searchAccountingBalanceGenerale(this.accountingBalanceSearch).subscribe(response => {
      this.accountingBalances = response;
      this.computeBalanceAndDebitAndCreditAccumulation();
      this.accountingBalances.sort((a, b) => this.sortRecords(a, b));

    });
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
