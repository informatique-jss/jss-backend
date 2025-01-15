import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
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
  accountingDate: Date = new Date();
  bankBalanceForm = this.formBuilder.group({});

  constructor(private bankBalanceService: BankBalanceService,
    private constantService: ConstantService,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit() {
    this.refresh();
  }

  refresh() {
    if (this.accountingAccountJss.id) {
      this.accountingDate = new Date(this.accountingDate.setHours(12));
      this.totalJssBankBalance = [];
      combineLatest([
        this.bankBalanceService.getAccountingRecordBalanceByAccountingAccountId(this.accountingAccountJss.id, this.accountingDate),
        this.bankBalanceService.getBankTransfertTotal(this.accountingDate),
        this.bankBalanceService.getRefundTotal(this.accountingDate),
        this.bankBalanceService.getCheckTotal(this.accountingDate),
        this.bankBalanceService.getDirectDebitTransfertTotal(this.accountingDate)
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
