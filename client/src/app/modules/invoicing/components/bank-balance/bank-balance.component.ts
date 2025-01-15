import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { combineLatest, map } from 'rxjs';
import { BankBalanceService } from 'src/app/modules/accounting/services/bank.balance.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AccountingAccount } from '../../../accounting/model/AccountingAccount';

@Component({
  selector: 'bank-balance',
  templateUrl: './bank-balance.component.html',
  styleUrls: ['./bank-balance.component.css']
})
export class BankBalanceComponent implements OnInit {
  accountingAccountJss: AccountingAccount = this.constantService.getAccountingAccountBankJss();
  accountingRecordBalance = {} as any;
  totalBankTransfert = {} as any;
  totalRefund = {} as any;
  totalCheck = {} as any;
  totalDirectDebitTransfert = {} as any;
  finalBankBalance = {} as any;
  totalJssBankBalance: any[] = [] as Array<any>;
  dataSource = new MatTableDataSource<any>;
  displayedColumnsTotal: string[] = ['label', 'amount'];
  tableName: string = "Solde compte BNP JSS";

  constructor(private bankBalanceService: BankBalanceService,
    private constantService: ConstantService
  ) { }

  ngOnInit() {
    if (this.accountingAccountJss.id) {
      combineLatest([
        this.bankBalanceService.getAccountingRecordBalanceByAccountingAccountId(this.accountingAccountJss.id),
        this.bankBalanceService.getBankTransfertTotal(),
        this.bankBalanceService.getRefundTotal(),
        this.bankBalanceService.getCheckTotal(),
        this.bankBalanceService.getDirectDebitTransfertTotal()
      ]).pipe(
        map(([accountingRecordBalance, totalBankTransfert, totalRefund, totalCheck, totalDirectDebitTransfert]) => ({ accountingRecordBalance, totalBankTransfert, totalRefund, totalCheck, totalDirectDebitTransfert })),
      ).subscribe(response => {
        this.accountingRecordBalance.label = "Solde du compte";
        this.accountingRecordBalance.amount = response.accountingRecordBalance;
        this.totalJssBankBalance.push(this.accountingRecordBalance);
        this.totalBankTransfert.label = "Virements émis non rapprochés";
        this.totalBankTransfert.amount = response.totalBankTransfert;
        this.totalJssBankBalance.push(this.totalBankTransfert);
        this.totalRefund.label = "Remboursements émis non rapprochés";
        this.totalRefund.amount = response.totalRefund;
        this.totalJssBankBalance.push(this.totalRefund);
        this.totalCheck.label = "Chèques émis non rapprochés";
        this.totalCheck.amount = Math.abs(response.totalCheck);
        this.totalJssBankBalance.push(this.totalCheck);
        this.totalDirectDebitTransfert.label = "Prélèvements émis non rapprochés";
        this.totalDirectDebitTransfert.amount = -Math.abs(response.totalDirectDebitTransfert);
        this.totalJssBankBalance.push(this.totalDirectDebitTransfert);
        this.finalBankBalance.label = "Solde bancaire";
        this.finalBankBalance.amount = +this.accountingRecordBalance.amount + +this.totalBankTransfert.amount + +this.totalRefund.amount + +this.totalCheck.amount - this.totalDirectDebitTransfert.amount;
        this.finalBankBalance.amount = this.finalBankBalance.amount;
        this.totalJssBankBalance.push(this.finalBankBalance);
        this.dataSource.data = this.totalJssBankBalance;

      });
    }
  }
}
