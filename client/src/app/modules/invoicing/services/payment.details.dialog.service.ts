import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { PaymentDetailsDialogComponent } from '../components/payment-details-dialog/payment-details-dialog.component';
import { Payment } from '../model/Payment';

@Injectable({
  providedIn: 'root'
})
export class PaymentDetailsDialogService {

  constructor(
    public paymentDetailsDialog: MatDialog,
  ) {
  }

  displayPaymentDetailsDialog(payment: Payment) {
    let paymentDetailsDialogRef = this.paymentDetailsDialog.open(PaymentDetailsDialogComponent, {
      minWidth: 'calc(100vw - 90px)',
      minHeight: 'calc(100vh - 90px)',
    });

    paymentDetailsDialogRef.componentInstance.payment = payment;

  }

}
