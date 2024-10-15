import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AppService } from '../../../../libs/app.service';
import { ConstantService } from '../../../../libs/constant.service';
import { ASSO_SERVICE_DOCUMENT_ENTITY_TYPE, CUSTOMER_ORDER_STATUS_BILLED } from '../../../../libs/Constants';
import { capitalizeName, getListMails, getListPhones } from '../../../../libs/FormatHelper';
import { Affaire } from '../../model/Affaire';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { Attachment } from '../../model/Attachment';
import { CustomerOrder } from '../../model/CustomerOrder';
import { InvoiceLabelResult } from '../../model/InvoiceLabelResult';
import { InvoicingSummary } from '../../model/InvoicingSummary';
import { MailComputeResult } from '../../model/MailComputeResult';
import { Payment } from '../../model/Payment';
import { Service } from '../../model/Service';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';
import { AttachmentService } from '../../services/attachment.service';
import { CustomerOrderService } from '../../services/customer.order.service';
import { InvoiceLabelResultService } from '../../services/invoice.label.result.service';
import { InvoicingSummaryService } from '../../services/invoicing.summary.service';
import { MailComputeResultService } from '../../services/mail.compute.result.service';
import { PaymentService } from '../../services/payment.service';
import { UploadAttachmentService } from '../../services/upload.attachment.service';
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
  invoiceSummary: InvoicingSummary | undefined;
  orderPayments: Payment[] | undefined;
  CUSTOMER_ORDER_STATUS_BILLED = CUSTOMER_ORDER_STATUS_BILLED;
  paymentTypeCb = this.constantService.getPaymentTypeCB();
  billingLabelTypeCodeAffaire = this.constantService.getBillingLabelTypeCodeAffaire();
  serviceProvisionAttachments: Attachment[][] = [];

  selectedAssoAffaireOrder: AssoAffaireOrder | undefined;
  ASSO_SERVICE_DOCUMENT_ENTITY_TYPE = ASSO_SERVICE_DOCUMENT_ENTITY_TYPE;

  currentSelectedAttachmentForDisable: Attachment | undefined;

  constructor(
    private constantService: ConstantService,
    private activatedRoute: ActivatedRoute,
    private customerOrderService: CustomerOrderService,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private invoiceLabelResultService: InvoiceLabelResultService,
    private mailComputeResultService: MailComputeResultService,
    private paymentService: PaymentService,
    private invoicingSummaryService: InvoicingSummaryService,
    private attachementService: AttachmentService,
    private uploadAttachmentService: UploadAttachmentService,
    private appService: AppService
  ) { }

  capitalizeName = capitalizeName;
  getCustomerOrderStatusLabel = getCustomerOrderStatusLabel;
  getClassForCustomerOrderStatus = getClassForCustomerOrderStatus;
  getListMails = getListMails;
  getListPhones = getListPhones;

  ngOnInit() {
    this.customerOrderService.getCustomerOrder(this.activatedRoute.snapshot.params['id']).subscribe(response => {
      this.order = response;
      this.loadOrderDetails();
    })
  }

  loadOrderDetails() {
    if (!this.order)
      return;

    this.assoAffaireOrderService.getAssoAffaireOrdersForCustomerOrder(this.order).subscribe(response => {
      this.ordersAssoAffaireOrders = response;
      if (this.ordersAssoAffaireOrders && this.ordersAssoAffaireOrders.length > 0) {
        this.changeAffaire(this.ordersAssoAffaireOrders[0]);
        if (this.ordersAssoAffaireOrders[0].services && this.ordersAssoAffaireOrders[0].services.length > 0)
          this.loadServiceDetails(this.ordersAssoAffaireOrders[0].services[0], false);
      }
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
    this.invoicingSummaryService.getInvoicingSummaryForCustomerOrder(this.order).subscribe(response => {
      this.invoiceSummary = response;
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

  changeAffaire(asso: AssoAffaireOrder) {
    this.selectedAssoAffaireOrder = asso;
    console.log(this.selectedAssoAffaireOrder);
  }

  loadServiceDetails(service: Service, forceLoad: boolean) {
    if (service) {
      if (!this.serviceProvisionAttachments[service.id] || forceLoad)
        this.attachementService.getAttachmentsForProvisionOfService(service).subscribe(response => {
          this.serviceProvisionAttachments[service.id] = response;
          initTooltips();
        })
    }
  }

  downloadAttachment(attachment: Attachment) {
    this.uploadAttachmentService.downloadAttachment(attachment);
  }

  disableAttachment(attachment: Attachment) {
    this.currentSelectedAttachmentForDisable = attachment;
  }

  confirmDisableAttachment() {
    if (this.currentSelectedAttachmentForDisable)
      this.uploadAttachmentService.disableAttachment(this.currentSelectedAttachmentForDisable).subscribe(response => {
        this.currentSelectedAttachmentForDisable = undefined;
        this.refreshCurrentAssoAffaireOrder();
      })
  }

  dismissDisableAttachment() {
    this.currentSelectedAttachmentForDisable = undefined;
  }

  refreshCurrentAssoAffaireOrder() {
    if (this.order)
      this.assoAffaireOrderService.getAssoAffaireOrdersForCustomerOrder(this.order).subscribe(response => {
        this.ordersAssoAffaireOrders = response;
        if (response && this.selectedAssoAffaireOrder)
          for (let asso of this.ordersAssoAffaireOrders)
            if (asso.id == this.selectedAssoAffaireOrder.id)
              this.changeAffaire(asso);
        initTooltips();
      })
  }

  editAffaireDetails(affaire: Affaire, event: any) {
    if (this.order)
      this.appService.openRoute(event, "account/affaire/edit/" + affaire.id + "/" + this.order.id, undefined);
  }

  editAddress(event: any) {
    if (this.order)
      this.appService.openRoute(event, "account/address/edit/" + this.order.id, undefined);
  }

}
