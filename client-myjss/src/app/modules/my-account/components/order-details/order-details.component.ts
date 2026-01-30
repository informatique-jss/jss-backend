import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NgbAccordionModule, NgbDropdownModule, NgbNavModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../../../../environments/environment';
import { compareWithId } from '../../../../libs/CompareHelper';
import { ASSO_SERVICE_DOCUMENT_ENTITY_TYPE, CUSTOMER_ORDER_STATUS_ABANDONED, CUSTOMER_ORDER_STATUS_BILLED, CUSTOMER_ORDER_STATUS_OPEN, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT, INVOICING_PAYMENT_LIMIT_REFUND_EUROS, SERVICE_FIELD_TYPE_DATE, SERVICE_FIELD_TYPE_INTEGER, SERVICE_FIELD_TYPE_SELECT, SERVICE_FIELD_TYPE_TEXT, SERVICE_FIELD_TYPE_TEXTAREA } from '../../../../libs/Constants';
import { capitalizeName, getListMails, getListPhones } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { TrustHtmlPipe } from '../../../../libs/TrustHtmlPipe';
import { AppService } from '../../../main/services/app.service';
import { ConstantService } from '../../../main/services/constant.service';
import { GtmService } from '../../../main/services/gtm.service';
import { FileUploadPayload, PageInfo } from '../../../main/services/GtmPayload';
import { AvatarComponent } from '../../../miscellaneous/components/avatar/avatar.component';
import { SingleUploadComponent } from '../../../miscellaneous/components/forms/single-upload/single-upload.component';
import { Employee } from '../../../profile/model/Employee';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { AssoServiceDocument } from '../../model/AssoServiceDocument';
import { Attachment } from '../../model/Attachment';
import { BillingLabelType } from '../../model/BillingLabelType';
import { CustomerOrder } from '../../model/CustomerOrder';
import { CustomerOrderComment } from '../../model/CustomerOrderComment';
import { DocumentType } from '../../model/DocumentType';
import { InvoiceLabelResult } from '../../model/InvoiceLabelResult';
import { InvoicingSummary } from '../../model/InvoicingSummary';
import { MailComputeResult } from '../../model/MailComputeResult';
import { Payment } from '../../model/Payment';
import { PaymentType } from '../../model/PaymentType';
import { Quotation } from '../../model/Quotation';
import { Service } from '../../model/Service';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';
import { AssoServiceDocumentService } from '../../services/asso.service.document.service';
import { AttachmentService } from '../../services/attachment.service';
import { CustomerOrderCommentService } from '../../services/customer.order.comment.service';
import { CustomerOrderService } from '../../services/customer.order.service';
import { InvoiceLabelResultService } from '../../services/invoice.label.result.service';
import { InvoicingSummaryService } from '../../services/invoicing.summary.service';
import { MailComputeResultService } from '../../services/mail.compute.result.service';
import { PaymentService } from '../../services/payment.service';
import { QuotationService } from '../../services/quotation.service';
import { ServiceService } from '../../services/service.service';
import { UploadAttachmentService } from '../../services/upload.attachment.service';
import { getClassForCustomerOrderStatus, getCustomerOrderBillingMailList, getCustomerOrderStatusLabel, getLastMissingAttachmentQueryDateLabel } from '../orders/orders.component';

@Component({
  selector: 'app-order-details',
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, AvatarComponent, TrustHtmlPipe, SingleUploadComponent, NgbTooltipModule, NgbDropdownModule, NgbAccordionModule, NgbNavModule]
})
export class OrderDetailsComponent implements OnInit {

  order: CustomerOrder | undefined;

  ordersAssoAffaireOrders: AssoAffaireOrder[] = [];
  orderInvoiceLabelResult: InvoiceLabelResult | undefined;
  orderMailComputeResult: MailComputeResult | undefined;
  digitalMailComputeResult: MailComputeResult | undefined;
  comments: CustomerOrderComment[] = [];
  orderPhysicalMailComputeResult: InvoiceLabelResult | undefined;
  invoiceSummary: InvoicingSummary | undefined;
  orderPayments: Payment[] | undefined;
  CUSTOMER_ORDER_STATUS_BILLED = CUSTOMER_ORDER_STATUS_BILLED;
  CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT = CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT;
  CUSTOMER_ORDER_STATUS_ABANDONED = CUSTOMER_ORDER_STATUS_ABANDONED;
  paymentTypeCb!: PaymentType;
  billingLabelTypeCodeAffaire!: BillingLabelType;
  serviceProvisionAttachments: Attachment[][] = [];
  currentUser: Responsable | undefined;
  associatedQuotation: Quotation | undefined;

  documentTypeBilling!: DocumentType;
  selectedAssoAffaireOrder: AssoAffaireOrder | undefined;
  ASSO_SERVICE_DOCUMENT_ENTITY_TYPE = ASSO_SERVICE_DOCUMENT_ENTITY_TYPE;

  currentSelectedAttachmentForDisable: Attachment | undefined;

  newComment: CustomerOrderComment = { comment: '', customerOrder: {} } as CustomerOrderComment;

  displayPayButton: boolean = false;
  orderDetailsForm!: FormGroup;
  selectedService: Service | undefined;
  jssEmployee: Employee = { firstname: 'Journal', lastname: 'Spécial des Sociétés', title: '' } as Employee;
  currentDate = new Date();

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
    private appService: AppService,
    private customerOrderCommentService: CustomerOrderCommentService,
    private loginService: LoginService,
    private formBuilder: FormBuilder,
    private serviceService: ServiceService,
    private quotationService: QuotationService,
    private gtmService: GtmService,
    private assoServiceDocumentService: AssoServiceDocumentService
  ) { }

  capitalizeName = capitalizeName;
  getCustomerOrderStatusLabel = getCustomerOrderStatusLabel;
  getClassForCustomerOrderStatus = getClassForCustomerOrderStatus;
  getListMails = getListMails;
  getListPhones = getListPhones;
  getLastMissingAttachmentQueryDateLabel = getLastMissingAttachmentQueryDateLabel;
  SERVICE_FIELD_TYPE_INTEGER = SERVICE_FIELD_TYPE_INTEGER;
  SERVICE_FIELD_TYPE_TEXT = SERVICE_FIELD_TYPE_TEXT;
  SERVICE_FIELD_TYPE_TEXTAREA = SERVICE_FIELD_TYPE_TEXTAREA;
  SERVICE_FIELD_TYPE_DATE = SERVICE_FIELD_TYPE_DATE;
  SERVICE_FIELD_TYPE_SELECT = SERVICE_FIELD_TYPE_SELECT;
  CUSTOMER_ORDER_STATUS_OPEN = CUSTOMER_ORDER_STATUS_OPEN;
  frontendJssUrl = environment.frontendJssUrl;

  ngOnInit() {
    this.orderDetailsForm = this.formBuilder.group({});

    this.paymentTypeCb = this.constantService.getPaymentTypeCB();
    this.billingLabelTypeCodeAffaire = this.constantService.getBillingLabelTypeCodeAffaire();
    this.documentTypeBilling = this.constantService.getDocumentTypeBilling();

    this.customerOrderCommentService.comments.subscribe((res: CustomerOrderComment[]) => {
      this.comments = res;
      this.sortComments();
    });

    this.refreshOrder();
  }

  refreshOrder() {
    this.customerOrderService.getCustomerOrder(this.activatedRoute.snapshot.params['id']).subscribe(response => {
      this.order = response;
      this.appService.hideLoadingSpinner();
      this.loadOrderDetails();
      if (this.order) {
        this.customerOrderCommentService.setWatchedOrder(this.order.id);
      }
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
    })
    this.invoiceLabelResultService.getInvoiceLabelComputeResultForCustomerOrder(this.order.id).subscribe(response => {
      this.orderInvoiceLabelResult = response;
    })
    this.mailComputeResultService.getMailComputeResultForBillingForCustomerOrder(this.order.id).subscribe(response => {
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
    })
    this.invoicingSummaryService.getInvoicingSummaryForCustomerOrder(this.order.id).subscribe(response => {
      this.invoiceSummary = response;
      if (this.invoiceSummary.remainingToPay && Math.abs(this.invoiceSummary.remainingToPay) > INVOICING_PAYMENT_LIMIT_REFUND_EUROS && this.order!.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_OPEN)
        this.displayPayButton = true;
      if (this.invoiceSummary.remainingToPay && this.order?.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_OPEN && this.order!.servicesList == this.constantService.getServiceTypeUniqueArticleBuy().label)
        this.displayPayButton = true;
      if (this.invoiceSummary.remainingToPay && this.order?.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_OPEN && this.order!.servicesList == this.constantService.getServiceTypeKioskNewspaperBuy().label)
        this.displayPayButton = true;
    })
    this.refreshCustomerOrderComments();
    this.loginService.getCurrentUser().subscribe(response => this.currentUser = response);
    this.quotationService.getQuotationForCustomerOrder(this.order.id).subscribe(response => {
      if (response && response.id)
        this.associatedQuotation = response;
    })
  }

  getCustomerOrderBillingMailList() {
    if (this.order && this.orderMailComputeResult)
      return getCustomerOrderBillingMailList(this.orderMailComputeResult);
    return null;
  }

  getCustomerOrderDigitalMailList() {
    if (this.order && this.digitalMailComputeResult)
      return getCustomerOrderBillingMailList(this.digitalMailComputeResult);
    return null;
  }

  changeAffaire(asso: AssoAffaireOrder) {
    this.selectedAssoAffaireOrder = asso;
  }

  loadServiceDetails(service: Service, forceLoad: boolean) {
    if (service) {
      this.selectedService = service;
      if (!this.serviceProvisionAttachments[service.id] || forceLoad)
        this.attachementService.getAttachmentsForProvisionOfService(service).subscribe(response => {
          this.serviceProvisionAttachments[service.id] = response;
        })
    }
  }

  getPurchaseOrderAttachment() {
    if (this.order && this.order.id)
      this.attachementService.getPurchaseOrderAttachment(this.order.id).subscribe(response => {
        if (response)
          this.downloadAttachment(response);
      });
    else
      this.appService.displayToast("Aucun bon de commande trouvé", false, "Erreur", 15000);
  }

  downloadAttachment(attachment: Attachment) {
    this.uploadAttachmentService.downloadAttachment(attachment);
  }

  disableAttachment(attachment: Attachment) {
    this.currentSelectedAttachmentForDisable = attachment;
  }

  dismissDisableAttachment() {
    this.currentSelectedAttachmentForDisable = undefined;
  }

  trackUploadFile(assoServiceDocument: AssoServiceDocument) {
    if (this.order)
      this.gtmService.trackFileUpload(
        {
          business: {
            type: 'order',
            order_id: this.order.id,
            documentType: assoServiceDocument.typeDocument.label
          },
          page: {
            type: 'my-account',
            name: 'order-details'
          } as PageInfo
        } as FileUploadPayload
      );
  }

  refreshCurrentAssoAffaireOrder() {
    if (this.order)
      this.assoAffaireOrderService.getAssoAffaireOrdersForCustomerOrder(this.order).subscribe(response => {
        this.ordersAssoAffaireOrders = response;
        if (response && this.selectedAssoAffaireOrder)
          for (let asso of this.ordersAssoAffaireOrders)
            if (asso.id == this.selectedAssoAffaireOrder.id)
              this.changeAffaire(asso);
      })
  }

  updateAssoServiceDocument(assoServiceDocument: AssoServiceDocument) {
    if (assoServiceDocument)
      this.trackUploadFile(assoServiceDocument);

    this.assoServiceDocumentService.getAssoServiceDocument(assoServiceDocument).subscribe(response => {
      if (response && this.selectedService) {
        const index = this.selectedService.assoServiceDocuments.findIndex(asso => asso.id === response.id);
        if (index !== -1) {
          this.selectedService.assoServiceDocuments.splice(index, 1, response);
        }
      }
    });
  }

  downloadInvoice() {
    if (this.order)
      this.customerOrderService.downloadInvoice(this.order);
  }

  refreshCustomerOrderComments() {
    if (this.order)
      this.customerOrderCommentService.getCustomerOrderCommentsForCustomer(this.order.id).subscribe(response => {
        this.comments = response;
      })
  }

  addCustomerOrderComment() {
    if (this.newComment.comment.trim().length > 0)
      if (this.newComment && this.newComment.comment.replace(/<(?:.|\n)*?>/gm, ' ').length > 0) {
        if (this.newComment.id == undefined) {
          if (this.order)
            this.newComment.customerOrder.id = this.order.id;
          this.newComment.isFromChat = true;
          this.newComment.isReadByCustomer = true;
        }
        this.customerOrderCommentService.addOrUpdateCustomerOrderComment(this.newComment).subscribe(response => {
          if (response) {
            this.comments.push(response);
            this.scrollToLastMessage();
          }
          this.newComment.comment = '';
        })
      }
  }

  sortComments() {
    if (this.comments && this.currentUser)
      this.comments.sort((b: CustomerOrderComment, a: CustomerOrderComment) => new Date(b.createdDateTime).getTime() - new Date(a.createdDateTime).getTime());
  }

  private scrollToLastMessage(behavior: ScrollBehavior = 'smooth'): void {
    setTimeout(() => {
      const el = document.getElementById('send-message');
      if (el) {
        el.scrollIntoView({ behavior: behavior, block: 'end' });
      }
    }, 100); // Timeout so the DOM is well up to date
  }


  editCustomerOrderComment(comment: CustomerOrderComment) {
    this.newComment = comment;
    this.newComment.comment = this.newComment.comment.replace(/<[^>]+>/g, '');
  }

  saveFieldsValue(service: Service) {
    this.serviceService.addOrUpdateService(service).subscribe(response => {
      this.appService.displayToast("Vos informations complémentaires ont bien été enregistrées", false, "Succès", 15000);
      this.refreshCurrentAssoAffaireOrder();
    })
  }

  displayMyDoc(service: Service) {
    return service.assoServiceDocuments && service.assoServiceDocuments.find(a => a.isMandatory);
  }

  displayMyInformation(service: Service) {
    return service.assoServiceFieldTypes && service.assoServiceFieldTypes.find(a => a.isMandatory);
  }

  cancelDraft(event: any) {
    if (this.order && this.order.id) {
      this.appService.showLoadingSpinner();
      this.customerOrderService.cancelCustomerOrder(this.order.id).subscribe(response => {
        this.refreshOrder();
      });
    }
  }

  ngOnDestroy() {
    this.customerOrderCommentService.setWatchedOrder(null);
  }

  compareWithId = compareWithId;
}
