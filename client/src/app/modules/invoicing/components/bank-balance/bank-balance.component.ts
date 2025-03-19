import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { combineLatest, map } from 'rxjs';
import { formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { BankBalancePaymentService } from 'src/app/modules/accounting/services/bank.balance.payment.service';
import { BankBalanceService } from 'src/app/modules/accounting/services/bank.balance.service';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AccountingAccount } from '../../../accounting/model/AccountingAccount';
import { Payment } from '../../model/Payment';

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
  displayedColumnsTotal: string[] = ['label', 'amount'];
  tableName: string = "Solde compte BNP JSS";
  accountingDate: Date = new Date();
  bankBalanceForm = this.formBuilder.group({});
  displayedColumns: SortTableColumn<any>[] = [];
  displayedColumnsPayment: SortTableColumn<Payment>[] = [];

  BANK_TRANSFERT: string = "BANK_TRANSFERT";
  DIRECT_DEBIT_TRANSFERT: string = "DIRECT_DEBIT_TRANSFERT";
  CHECK_EMITED: string = "CHECK_EMITED";
  REFUND: string = "REFUND";

  paymentList: Payment[] = [];

  constructor(private bankBalanceService: BankBalanceService,
    private constantService: ConstantService,
    private formBuilder: FormBuilder,
    private bankBalancePaymentService: BankBalancePaymentService
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Indicateur" } as SortTableColumn<any>);
    this.displayedColumns.push({ id: "amount", fieldName: "amount", label: "Montant (en €)", valueFonction: formatEurosForSortTable } as SortTableColumn<any>);

    this.displayedColumnsPayment = [];
    this.displayedColumnsPayment.push({ id: "id", fieldName: "id", label: "N° du paiement" } as SortTableColumn<Payment>);
    this.displayedColumnsPayment.push({ id: "originPaymentId", fieldName: "originPayment.id", label: "Paiement d'origine" } as SortTableColumn<Payment>);
    this.displayedColumnsPayment.push({ id: "paymentDate", fieldName: "paymentDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Payment>);
    this.displayedColumnsPayment.push({ id: "paymentAmount", fieldName: "paymentAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn<Payment>);
    this.displayedColumnsPayment.push({ id: "paymentTypeLabel", fieldName: "paymentType.label", label: "Type" } as SortTableColumn<Payment>);
    this.displayedColumnsPayment.push({ id: "label", fieldName: "label", label: "Libellé" } as SortTableColumn<Payment>);
    this.displayedColumnsPayment.push({ id: "invoice", fieldName: "invoice.id", label: "Facture associée", actionLinkFunction: this.getActionLink, actionIcon: "visibility", actionTooltip: "Voir la facture associée" } as SortTableColumn<Payment>);
    this.displayedColumnsPayment.push({ id: "comment", fieldName: "comment", label: "Commentaire" } as SortTableColumn<Payment>);

    this.refresh();
  }

  getActionLink(action: SortTableColumn<Payment>, element: Payment) {
    if (element && action.id == "invoice" && element.invoice && element.invoice.id)
      return ['/invoicing/view', element.invoice.id];
    return undefined;
  }

  refresh() {
    if (this.accountingAccountJss.id) {
      this.accountingDate = new Date(this.accountingDate.setHours(12));
      combineLatest([
        this.bankBalanceService.getAccountingRecordBalanceByAccountingAccountId(this.accountingAccountJss.id, this.accountingDate),
        this.bankBalanceService.getBankTransfertTotal(this.accountingDate),
        this.bankBalanceService.getRefundTotal(this.accountingDate),
        this.bankBalanceService.getCheckTotal(this.accountingDate),
        this.bankBalanceService.getDirectDebitTransfertTotal(this.accountingDate)
      ]).pipe(
        map(([accountingRecordBalance, totalBankTransfert, totalRefund, totalCheck, totalDirectDebitTransfert]) => ({ accountingRecordBalance, totalBankTransfert, totalRefund, totalCheck, totalDirectDebitTransfert })),
      ).subscribe(response => {
        this.totalJssBankBalance = [];
        this.accountingRecordBalance.label = "Solde du compte";
        this.accountingRecordBalance.amount = response.accountingRecordBalance;
        this.totalJssBankBalance.push(this.accountingRecordBalance);
        this.totalBankTransfert.id = this.BANK_TRANSFERT;
        this.totalBankTransfert.label = "Virements émis non rapprochés";
        this.totalBankTransfert.amount = response.totalBankTransfert;
        this.totalJssBankBalance.push(this.totalBankTransfert);
        this.totalRefund.id = this.REFUND;
        this.totalRefund.label = "Remboursements émis non rapprochés";
        this.totalRefund.amount = response.totalRefund;
        this.totalJssBankBalance.push(this.totalRefund);
        this.totalCheck.id = this.CHECK_EMITED;
        this.totalCheck.label = "Chèques émis non rapprochés";
        this.totalCheck.amount = response.totalCheck;
        this.totalJssBankBalance.push(this.totalCheck);
        this.totalDirectDebitTransfert.id = this.DIRECT_DEBIT_TRANSFERT;
        this.totalDirectDebitTransfert.label = "Prélèvements émis non rapprochés";
        this.totalDirectDebitTransfert.amount = response.totalDirectDebitTransfert;
        this.totalJssBankBalance.push(this.totalDirectDebitTransfert);
        this.finalBankBalance.label = "Solde bancaire";
        this.finalBankBalance.amount = +this.accountingRecordBalance.amount + this.totalBankTransfert.amount + this.totalRefund.amount + this.totalCheck.amount + this.totalDirectDebitTransfert.amount;
        this.finalBankBalance.amount = this.finalBankBalance.amount;
        this.totalJssBankBalance.push(this.finalBankBalance);
      });
    }
  }

  selectPayments(event: any) {
    console.log(event);

    if (event.id)
      if (event.id == this.REFUND) {
        this.bankBalancePaymentService.getRefundList(this.accountingDate).subscribe(response => this.paymentList = response);
      } else if (event.id == this.BANK_TRANSFERT) {
        this.bankBalancePaymentService.getBankTransfertList(this.accountingDate).subscribe(response => this.paymentList = response);
      } if (event.id == this.CHECK_EMITED) {
        this.bankBalancePaymentService.getCheckList(this.accountingDate).subscribe(response => this.paymentList = response);
      } if (event.id == this.DIRECT_DEBIT_TRANSFERT) {
        this.bankBalancePaymentService.getDirectDebitTransfertList(this.accountingDate).subscribe(response => this.paymentList = response);
      }
  }
}
