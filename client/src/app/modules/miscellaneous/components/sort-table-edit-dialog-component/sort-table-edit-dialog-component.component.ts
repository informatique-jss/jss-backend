import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Payment } from 'src/app/modules/invoicing/model/Payment';
import { PaymentService } from '../../../invoicing/services/payment.service';
import { PaymentSearchResult } from 'src/app/modules/invoicing/model/PaymentSearchResult';
import { PaymentWay } from 'src/app/modules/invoicing/model/PaymentWay';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { PaymentType } from '../../model/PaymentType';
import { BankTransfertSearchResult } from 'src/app/modules/invoicing/model/BankTransfertSearchResult';
import { BankTransfert } from 'src/app/modules/quotation/model/BankTransfert';
import { getLocaleDateFormat } from '@angular/common';
import { BankTransfertService } from '../../../quotation/services/bank.transfert.service';

@Component({
  selector: 'app-sort-table-edit-dialog-component',
  templateUrl: './sort-table-edit-dialog.component.html',
  styleUrls: ['./sort-table-edit-dialog.css']
})
export class SortTableEditDialogComponent {

  editedComment: string;
  payment: Payment | undefined;
  bankTransfert: BankTransfert | undefined;
  transfers: BankTransfertSearchResult[] | undefined;

  constructor(
    public dialogRef: MatDialogRef<SortTableEditDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private paymentService: PaymentService,
    private bankTransfertService: BankTransfertService,
  ) {

    this.editedComment = data.commentPayment;
  }

  mapToBackendBankTransfert(bankTransfertSearchResult: BankTransfertSearchResult): BankTransfert {
    const backendTransfert: BankTransfert = {
      id: bankTransfertSearchResult.id,
      label: '',
      transfertAmount: 0,
      transfertDateTime: new Date(),
      transfertIban: '',
      transfertBic: '',
      isAlreadyExported: false,
      debours: [],
      invoices: [],
      commentTransfert: this.editedComment
    };
    return backendTransfert;
  }

  mapToBackendPayment(paymentSearchResult: PaymentSearchResult): Payment {
    const backendPayment: Payment = {
      id: paymentSearchResult.id,
      bankId: '',
      paymentDate: paymentSearchResult.paymentDate,
      paymentAmount: paymentSearchResult.paymentAmount,
      paymentWay: {
        id: paymentSearchResult.paymentWayId,
        label: paymentSearchResult.paymentWayLabel
      } as PaymentWay,
      label: paymentSearchResult.paymentLabel,
      invoice: {
        id: paymentSearchResult.invoiceId
      } as Invoice,
      accountingRecords: [],
      customerOrder: undefined as unknown as CustomerOrder,
      isExternallyAssociated: paymentSearchResult.isExternallyAssociated,
      isCancelled: paymentSearchResult.isCancelled,
      paymentType: {
        label: paymentSearchResult.paymentTypeLabel
      } as PaymentType,
      commentPayment: paymentSearchResult.commentPayment,
    };
    return backendPayment;
  }

  onSaveClick() {
    if (this.editedComment && this.data.hasOwnProperty('commentPayment')) {
      this.data.commentPayment = this.editedComment;
      const backendPayment = this.mapToBackendPayment(this.data);
      this.paymentService.addOrUpdatePaymentComment(backendPayment).subscribe(response => {
        this.dialogRef.close(true);
      });
    }else if(this.editedComment && this.data.hasOwnProperty('commentTransfert')){
      this.data.commentTransfert = this.editedComment;
      const backendPayment = this.mapToBackendBankTransfert(this.data);
      this.bankTransfertService.addOrUpdateTransfertComment(backendPayment).subscribe(response => {
        this.dialogRef.close(true);
    });
    }}

  onCancelClick() {
    this.dialogRef.close();
  }

}
