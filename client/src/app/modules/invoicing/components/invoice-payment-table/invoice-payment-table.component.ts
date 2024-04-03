import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { formatDate } from 'src/app/libs/FormatHelper';
import { instanceOfResponsable } from 'src/app/libs/TypeHelper';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { ITiers } from '../../../tiers/model/ITiers';
import { Payment } from '../../model/Payment';
import { PaymentDetailsDialogService } from '../../services/payment.details.dialog.service';
import { PaymentService } from '../../services/payment.service';
import { AssociatePaymentDialogComponent } from '../associate-payment-dialog/associate-payment-dialog.component';
import { getAmountPayed, getCustomerOrderForInvoice, getRemainingToPay } from '../invoice-tools';

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
    private paymentDetailsDialogService: PaymentDetailsDialogService,
    private habilitationsService: HabilitationsService
  ) { }

  ngOnInit() {
  }

  openPaymentDialog(payment: Payment) {
    this.paymentDetailsDialogService.displayPaymentDetailsDialog(payment);
  }

  movePaymentToWaitingAccount(payment: Payment) {
    this.paymentService.movePaymentToWaitingAccount(payment).subscribe((res) => {
      this.appService.openRoute(null, '/invoicing/view/' + this.invoice.id, null);
    });
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

  refundAppoint(payment: Payment) {
    if (this.invoice) {
      let customerOrder: ITiers = getCustomerOrderForInvoice(this.invoice);
      if (instanceOfResponsable(customerOrder))
        customerOrder = (customerOrder as Responsable).tiers;

      let affaireId = 1;
      if (this.invoice && this.invoice.customerOrder) {
        affaireId = this.invoice.customerOrder.assoAffaireOrders[0].affaire.id;
      }
      this.paymentService.refundPayment(payment, { entityId: customerOrder.id } as any, { entityId: affaireId } as any).subscribe(response => {
        this.appService.openRoute(null, '/invoicing/view/' + this.invoice.id, null);
      });
    }
  }

  canMovePaymentToWaitingAccount() {
    return this.habilitationsService.canMovePaymentToWaitingAccount();
  }



}
