import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ConstantService } from '../../../../libs/constant.service';
import { CUSTOMER_ORDER_STATUS_BILLED } from '../../../../libs/Constants';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { CustomerOrder } from '../../model/CustomerOrder';
import { InvoiceLabelResult } from '../../model/InvoiceLabelResult';
import { MailComputeResult } from '../../model/MailComputeResult';
import { Payment } from '../../model/Payment';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';
import { CustomerOrderService } from '../../services/customer.order.service';
import { InvoiceLabelResultService } from '../../services/invoice.label.result.service';
import { MailComputeResultService } from '../../services/mail.compute.result.service';
import { PaymentService } from '../../services/payment.service';
import { getClassForCustomerOrderStatus, getCustomerOrderBillingMailList, getCustomerOrderStatusLabel, initTooltips } from '../orders/orders.component';

@Component({
  selector: 'app-order-details',
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.css']
})
export class OrderDetailsComponent implements OnInit {

  order: CustomerOrder | undefined;

  ordersAssoAffaireOrders: AssoAffaireOrder[] = [];
  orderInvoiceLabelResult: InvoiceLabelResult | undefined;
  orderMailComputeResult: MailComputeResult | undefined;
  digitalMailComputeResult: MailComputeResult | undefined;
  orderPhysicalMailComputeResult: InvoiceLabelResult | undefined;
  orderPayments: Payment[] | undefined;
  CUSTOMER_ORDER_STATUS_BILLED = CUSTOMER_ORDER_STATUS_BILLED;
  paymentTypeCb = this.constantService.getPaymentTypeCB();

  constructor(
    private constantService: ConstantService,
    private activatedRoute: ActivatedRoute,
    private customerOrderService: CustomerOrderService,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private invoiceLabelResultService: InvoiceLabelResultService,
    private mailComputeResultService: MailComputeResultService,
    private paymentService: PaymentService
  ) { }

  capitalizeName = capitalizeName;
  getCustomerOrderStatusLabel = getCustomerOrderStatusLabel;
  getClassForCustomerOrderStatus = getClassForCustomerOrderStatus;

  ngOnInit() {
    this.customerOrderService.getCustomerOrder(this.activatedRoute.snapshot.params['id']).subscribe(response => {
      this.order = response;
      this.loadOrderDetails();
    })
  }

  loadOrderDetails() {
    if (!this.order)
      return;

    this.assoAffaireOrderService.getAssoAffaireOrderForCustomerOrder(this.order).subscribe(response => {
      this.ordersAssoAffaireOrders = response;
      initTooltips();
    })
    this.invoiceLabelResultService.getInvoiceLabelComputeResultForCustomerOrder(this.order).subscribe(response => {
      this.orderInvoiceLabelResult = response;
    })
    this.mailComputeResultService.getMailComputeResultForBillingForCustomerOrder(this.order).subscribe(response => {
      this.orderMailComputeResult = response;
    })
    this.mailComputeResultService.getMailComputeResultForDigitalForCustomerOrder(this.order).subscribe(response => {
      this.digitalMailComputeResult = response;
    })
    this.invoiceLabelResultService.getPhysicalMailComputeResultForBillingForCustomerOrder(this.order).subscribe(response => {
      this.orderPhysicalMailComputeResult = response;
    })
    this.paymentService.getApplicablePaymentsForCustomerOrder(this.order).subscribe(response => {
      this.orderPayments = response;
      initTooltips();
    })
  }

  getCustomerOrderBillingMailList() {
    if (this.order && this.orderMailComputeResult)
      return getCustomerOrderBillingMailList(this.order, this.orderMailComputeResult);
    return null;
  }

  getCustomerOrderDigitalMailList() {
    if (this.order && this.digitalMailComputeResult)
      return getCustomerOrderBillingMailList(this.order, this.digitalMailComputeResult);
    return null;
  }

  changeAffaire() { }



}
