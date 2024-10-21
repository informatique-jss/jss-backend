import { Component, OnInit } from '@angular/core';
import { BankBalanceService } from 'src/app/modules/accounting/services/bank.balance.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AccountingAccount } from '../../../accounting/model/AccountingAccount';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'bank-balance',
  templateUrl: './bank-balance.component.html',
  styleUrls: ['./bank-balance.component.css']
})
export class BankBalanceComponent implements OnInit {
  accountingAccountJss: AccountingAccount = this.constantService.getAccountingAccountBankJss();
  totalBalance = {} as any;
  totalBankTransfert = {} as any;
  totalRefund = {} as any;
  totalCheck = {} as any;
  totalDirectDebitTransfert = {} as any;
  finalBankBalance = {} as any;
  totalJssBankBalance = [] as Array<any>;
  dataSource = new MatTableDataSource<any>;
  displayedColumnsTotal: string[] = ['label', 'amount'];

  constructor(private bankBalanceService: BankBalanceService,
    private constantService: ConstantService
  ) { }

  ngOnInit() {
    if (this.accountingAccountJss.id) {
      this.bankBalanceService.getAccountingRecordBalanceByAccountingAccountId(this.accountingAccountJss.id).subscribe(response => {
        this.totalBalance.label = "Solde du compte";
        this.totalBalance.amount = response.toFixed(2);

        this.bankBalanceService.getBankTransfertTotal().subscribe(response => {
          this.totalBankTransfert.label = "Virements émis non rapprochés";
          this.totalBankTransfert.amount = response.toFixed(2);

          this.bankBalanceService.getRefundTotal().subscribe(response => {
            this.totalRefund.label = "Remboursements émis non rapprochés";
            this.totalRefund.amount = response.toFixed(2);

            this.bankBalanceService.getCheckTotal().subscribe(response => {
              this.totalCheck.label = "Chèques émis non rapprochés";
              this.totalCheck.amount = response.toFixed(2);

              this.bankBalanceService.getDirectDebitTransfertTotal().subscribe(response => {
                this.totalDirectDebitTransfert.label = "Prélèvements émis non rapprochés";
                this.totalDirectDebitTransfert.amount = response.toFixed(2);
                this.finalBankBalance.label = "Solde bancaire";
                this.finalBankBalance.amount = +this.totalBalance.amount + +this.totalBankTransfert.amount + +this.totalRefund.amount + +this.totalCheck.amount - +this.totalDirectDebitTransfert.amount;
                this.finalBankBalance.amount = this.finalBankBalance.amount.toFixed(2);
                this.totalJssBankBalance.push(this.totalBalance);
                this.totalJssBankBalance.push(this.totalDirectDebitTransfert);
                this.totalJssBankBalance.push(this.totalRefund);
                this.totalJssBankBalance.push(this.totalBankTransfert);
                this.totalJssBankBalance.push(this.totalCheck);
                this.totalJssBankBalance.push(this.finalBankBalance);
                this.dataSource.data = this.totalJssBankBalance;
              });
            });
          });
        });
      });
    }
  }
}
