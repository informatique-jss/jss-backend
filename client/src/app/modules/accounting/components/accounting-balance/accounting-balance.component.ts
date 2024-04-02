import { CdkDragEnd, Point } from '@angular/cdk/drag-drop';
import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { AccountingAccountClass } from '../../model/AccountingAccountClass';
import { AccountingBalance } from '../../model/AccountingBalance';
import { AccountingBalanceSearch } from '../../model/AccountingBalanceSearch';
import { AccountingAccountClassService } from '../../services/accounting.account.class.service';
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
    private accountingAccountClassService: AccountingAccountClassService
  ) { }

  accumulatedDataSource = new MatTableDataSource<AccountingBalance>();
  accountingBalances: AccountingBalance[] | undefined;
  displayedColumnsTotal: string[] = ['label', 'debit', 'credit'];
  currentUserPosition: Point = { x: 0, y: 0 };
  bookmark: AccountingBalanceSearch | undefined;
  classes: AccountingAccountClass[] = [];

  displayedColumns: SortTableColumn<AccountingBalance>[] = [] as Array<SortTableColumn<AccountingBalance>>;
  displayedColumnsClassTotal: SortTableColumn<AccountingBalance>[] = [] as Array<SortTableColumn<AccountingBalance>>;
  classTotals: AccountingBalance[] = [] as Array<AccountingBalance>;

  ngOnInit() {
    this.accountingAccountClassService.getAccountingAccountClasses().subscribe(response => this.classes = response);
    // Column init
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "accountingAccountNumber", fieldName: "accountingAccountNumber", label: "N° de compte", valueFonction: (element: AccountingBalance, column: SortTableColumn<AccountingBalance>) => { if (element && column) return element.principalAccountingAccountCode + element.accountingAccountSubNumber; return "" } } as SortTableColumn<AccountingBalance>);
    this.displayedColumns.push({ id: "accountingAccountLabel", fieldName: "accountingAccountLabel", label: "Libellé du compte" } as SortTableColumn<AccountingBalance>);
    this.displayedColumns.push({ id: "debitAmount", fieldName: "debitAmount", label: "Débit", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingBalance>);
    this.displayedColumns.push({ id: "creditAmount", fieldName: "creditAmount", label: "Crédit", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingBalance>);
    this.displayedColumns.push({ id: "debitAmountSolde", fieldName: "debitAmountSolde", label: "Solde débit", valueFonction: (element: AccountingBalance, column: SortTableColumn<AccountingBalance>) => { if (element && column) return (element.debitAmount > element.creditAmount ? (element.debitAmount - element.creditAmount).toFixed(2) + " €" : ''); return "" } } as SortTableColumn<AccountingBalance>);
    this.displayedColumns.push({ id: "creditAmountSolde", fieldName: "creditAmountSolde", label: "Solde crédit", valueFonction: (element: AccountingBalance, column: SortTableColumn<AccountingBalance>) => { if (element && column) return (element.debitAmount <= element.creditAmount ? (element.creditAmount - element.debitAmount).toFixed(2) + " €" : ''); return "" } } as SortTableColumn<AccountingBalance>);
    this.displayedColumns.push({ id: "echoir30", fieldName: "echoir30", label: "Créances à échoir à -30 j", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingBalance>);
    this.displayedColumns.push({ id: "echoir60", fieldName: "echoir60", label: "Créances à échoir à -60 j", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingBalance>);
    this.displayedColumns.push({ id: "echoir90", fieldName: "echoir90", label: "Créances à échoir à +60 j", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingBalance>);
    this.displayedColumns.push({ id: "echu30", fieldName: "echu30", label: "Créances échues à -30 j", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingBalance>);
    this.displayedColumns.push({ id: "echu60", fieldName: "echu60", label: "Créances échues à -60 j", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingBalance>);
    this.displayedColumns.push({ id: "echu90", fieldName: "echu90", label: "Créances échues à +60 j", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingBalance>);

    this.displayedColumnsClassTotal = [];
    this.displayedColumnsClassTotal.push({ id: "accountingAccountLabel", fieldName: "accountingAccountLabel", label: "Libellé" } as SortTableColumn<AccountingBalance>);
    this.displayedColumnsClassTotal.push({ id: "debitAmount", fieldName: "debitAmount", label: "Débit", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingBalance>);
    this.displayedColumnsClassTotal.push({ id: "creditAmount", fieldName: "creditAmount", label: "Crédit", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingBalance>);
    this.displayedColumnsClassTotal.push({ id: "soldeAmount", fieldName: "soldeAmount", label: "Solde", valueFonction: (element: AccountingBalance, column: SortTableColumn<AccountingBalance>) => { return ((element.creditAmount - element.debitAmount).toFixed(2) + "").replace(".", ",") + " €" } } as SortTableColumn<AccountingBalance>);

    this.bookmark = this.userPreferenceService.getUserSearchBookmark("accounting-balance") as AccountingBalanceSearch;
    if (this.bookmark) {
      this.accountingBalanceSearch = this.bookmark;
      if (this.accountingBalanceSearch.startDate)
        this.accountingBalanceSearch.startDate = new Date(this.accountingBalanceSearch.startDate);
      if (this.accountingBalanceSearch.endDate)
        this.accountingBalanceSearch.endDate = new Date(this.accountingBalanceSearch.endDate);
      this.searchRecords();
    }
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
    this.accountingBalanceService.exportBalance(this.accountingBalanceSearch);
  }

  searchRecords() {
    this.restoreTotalDivPosition();
    if (!this.accountingBalanceSearch.startDate || !this.accountingBalanceSearch.endDate) {
      this.appService.displaySnackBar("🙄 Merci de saisir une plage de recherche", false, 10);
      return;
    }
    this.accountingBalanceSearch.startDate = new Date(this.accountingBalanceSearch.startDate.setHours(12));
    this.userPreferenceService.setUserSearchBookmark(this.accountingBalanceSearch, "accounting-balance");
    this.accountingBalanceService.searchAccountingBalance(this.accountingBalanceSearch).subscribe(response => {
      this.accountingBalances = response;
      this.accountingBalances.sort((a, b) => this.sortRecords(a, b));
      this.computeBalanceAndDebitAndCreditAccumulation();
      this.computeBalanceClassTotals();
    });
  }

  sortRecords(a: AccountingBalance, b: AccountingBalance): number {
    if (a && !b)
      return 1;
    if (!a && b)
      return -1;
    if (!a && !b)
      return 0;
    let aNumber = a.principalAccountingAccountCode + a.accountingAccountSubNumber;
    let bNumber = b.principalAccountingAccountCode + b.accountingAccountSubNumber;
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

          if (accountingBalance.debitAmount) {
            debit += accountingBalance.debitAmount;
            balance -= accountingBalance.debitAmount;
          }
        }
      }

      let accumulatedData = [];
      let totalLine = {} as any;
      totalLine.label = "Total";
      totalLine.debit = debit.toFixed(2);
      totalLine.credit = credit.toFixed(2);
      totalLine.balance = balance.toFixed(2);
      accumulatedData.push(totalLine);

      this.accumulatedDataSource.data = accumulatedData;

    }
  }

  computeBalanceClassTotals() {
    if (this.accountingBalances && this.classes) {
      this.classTotals = [];
      for (let classe of this.classes) {
        let credit: number = 0;
        let debit: number = 0;

        for (let balance of this.accountingBalances) {
          if (balance.accountingAccountClassLabel == classe.label) {
            credit += balance.creditAmount;
            debit += balance.debitAmount;
          }
        }
        let balanceClass = {} as AccountingBalance;
        balanceClass.creditAmount = credit;
        balanceClass.debitAmount = debit;
        balanceClass.accountingAccountLabel = classe.label;
        this.classTotals.push(balanceClass);
      }
    }
  }

  setCurentMonth() {
    let d = new Date();
    this.accountingBalanceSearch.startDate = new Date(d.getFullYear(), d.getMonth(), 1, 12, 0, 0);
    let d2 = new Date();
    this.accountingBalanceSearch.endDate = new Date(d2.getFullYear(), d2.getMonth() + 1, 0, 12, 0, 0);
  }

  setCurentFiscalYear() {
    let d = new Date();
    this.accountingBalanceSearch.startDate = new Date(d.getFullYear() - 1, 11, 31, 12, 0, 0);
    let d2 = new Date();
    this.accountingBalanceSearch.endDate = new Date(d2.getFullYear() + 1, 0, 1, 12, 0, 0);
  }

}
