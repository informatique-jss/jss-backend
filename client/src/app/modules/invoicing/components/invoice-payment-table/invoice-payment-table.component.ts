import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { formatDate } from 'src/app/libs/FormatHelper';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { AppService } from 'src/app/services/app.service';
import { Payment } from '../../model/Payment';
import { PaymentService } from '../../services/payment.service';
import { AssociatePaymentDialogComponent } from '../associate-payment-dialog/associate-payment-dialog.component';
import { getAmountPayed, getRemainingToPay } from '../invoice-tools';

@Component({
  selector: 'invoice-payment-table',
  templateUrl: './invoice-payment-table.component.html',
  styleUrls: ['./invoice-payment-table.component.css']
})
export class InvoicePaymentTableComponent implements OnInit {

  @Input() invoice: Invoice = {} as Invoice;

  formatDate = formatDate;
  getAmountRemaining = getRemainingToPay;
  getAmountPayed = getAmountPayed;

  constructor(
    public associateDepositDialog: MatDialog,
    public appService: AppService,
    public associatePaymentDialog: MatDialog,
    private paymentService: PaymentService,
  ) { }

  ngOnInit() {
  }

  movePayment(payment: Payment) {
    this.paymentService.getPaymentById(payment.id).subscribe(element => {
      let dialogPaymentDialogRef = this.associatePaymentDialog.open(AssociatePaymentDialogComponent, {
        width: '100%'
      });
      dialogPaymentDialogRef.componentInstance.payment = element;
      dialogPaymentDialogRef.componentInstance.doNotInitializeAsso = true;
      dialogPaymentDialogRef.afterClosed().subscribe(response => {
        this.appService.openRoute(null, '/invoicing/view/' + this.invoice.id, null);
      });
    })
  }


}
