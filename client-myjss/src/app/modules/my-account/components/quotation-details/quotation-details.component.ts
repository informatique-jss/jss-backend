import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NgbAccordionModule, NgbDropdownModule, NgbModal, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { ASSO_SERVICE_DOCUMENT_ENTITY_TYPE, INVOICING_PAYMENT_LIMIT_REFUND_EUROS, QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_OPEN, QUOTATION_STATUS_REFUSED_BY_CUSTOMER, QUOTATION_STATUS_SENT_TO_CUSTOMER, QUOTATION_STATUS_VALIDATED_BY_CUSTOMER, SERVICE_FIELD_TYPE_DATE, SERVICE_FIELD_TYPE_INTEGER, SERVICE_FIELD_TYPE_SELECT, SERVICE_FIELD_TYPE_TEXT, SERVICE_FIELD_TYPE_TEXTAREA } from '../../../../libs/Constants';
import { capitalizeName, getListMails, getListPhones } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { ConstantService } from '../../../main/services/constant.service';
import { SingleUploadComponent } from '../../../miscellaneous/components/forms/single-upload/single-upload.component';
import { Affaire } from '../../model/Affaire';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { Attachment } from '../../model/Attachment';
import { BillingLabelType } from '../../model/BillingLabelType';
import { CustomerOrder } from '../../model/CustomerOrder';
import { InvoiceLabelResult } from '../../model/InvoiceLabelResult';
import { InvoicingSummary } from '../../model/InvoicingSummary';
import { MailComputeResult } from '../../model/MailComputeResult';
import { Quotation } from '../../model/Quotation';
import { Service } from '../../model/Service';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';
import { AttachmentService } from '../../services/attachment.service';
import { CustomerOrderService } from '../../services/customer.order.service';
import { InvoiceLabelResultService } from '../../services/invoice.label.result.service';
import { InvoicingSummaryService } from '../../services/invoicing.summary.service';
import { MailComputeResultService } from '../../services/mail.compute.result.service';
import { QuotationService } from '../../services/quotation.service';
import { ServiceService } from '../../services/service.service';
import { UploadAttachmentService } from '../../services/upload.attachment.service';
import { getCustomerOrderBillingMailList } from '../orders/orders.component';
import { getClassForQuotationStatus, getQuotationStatusLabel } from '../quotations/quotations.component';

@Component({
  selector: 'app-quotation-details',
  templateUrl: './quotation-details.component.html',
  styleUrls: ['./quotation-details.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, SingleUploadComponent, NgbDropdownModule, NgbAccordionModule, NgbNavModule]
})
export class QuotationDetailsComponent implements OnInit {

  @ViewChild('validatedQuotationModal') validatedQuotationModal!: TemplateRef<any>;
  validatedQuotationModalInstance: any | undefined;

  @ViewChild('cancelQuotationModal') cancelQuotationModal!: TemplateRef<any>;
  cancelQuotationModalInstance: any | undefined;

  quotation: Quotation | undefined;

  quotationAssoAffaireOrders: AssoAffaireOrder[] = [];
  quotationInvoiceLabelResult: InvoiceLabelResult | undefined;
  quotationMailComputeResult: MailComputeResult | undefined;
  quotationDigitalMailComputeResult: MailComputeResult | undefined;
  quotationPhysicalMailComputeResult: InvoiceLabelResult | undefined;
  invoiceSummary: InvoicingSummary | undefined;
  billingLabelTypeCodeAffaire!: BillingLabelType;
  associatedCustomerOrder: CustomerOrder | undefined;
  quotationAttachments: Attachment[][] = [];

  selectedAssoAffaireOrder: AssoAffaireOrder | undefined;
  ASSO_SERVICE_DOCUMENT_ENTITY_TYPE = ASSO_SERVICE_DOCUMENT_ENTITY_TYPE;

  currentSelectedAttachmentForDisable: Attachment | undefined;

  displayPayButton: boolean = false;
  quotationDetailsForm!: FormGroup;

  dislayAlreadyFilledAttachment = false;
  canEditQuotation: boolean = false;

  constructor(
    private constantService: ConstantService,
    private activatedRoute: ActivatedRoute,
    private quotationService: QuotationService,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private invoiceLabelResultService: InvoiceLabelResultService,
    private mailComputeResultService: MailComputeResultService,
    private invoicingSummaryService: InvoicingSummaryService,
    private uploadAttachmentService: UploadAttachmentService,
    private attachementService: AttachmentService,
    private appService: AppService,
    private formBuilder: FormBuilder,
    private customerOrderService: CustomerOrderService,
    private serviceService: ServiceService,
    private modalService: NgbModal,
  ) { }

  capitalizeName = capitalizeName;
  getQuotationStatusLabel = getQuotationStatusLabel;
  getClassForQuotationStatus = getClassForQuotationStatus;
  getListMails = getListMails;
  getListPhones = getListPhones;
  SERVICE_FIELD_TYPE_INTEGER = SERVICE_FIELD_TYPE_INTEGER;
  SERVICE_FIELD_TYPE_TEXT = SERVICE_FIELD_TYPE_TEXT;
  SERVICE_FIELD_TYPE_TEXTAREA = SERVICE_FIELD_TYPE_TEXTAREA;
  SERVICE_FIELD_TYPE_DATE = SERVICE_FIELD_TYPE_DATE;
  SERVICE_FIELD_TYPE_SELECT = SERVICE_FIELD_TYPE_SELECT;
  QUOTATION_STATUS_VALIDATED_BY_CUSTOMER = QUOTATION_STATUS_VALIDATED_BY_CUSTOMER;
  QUOTATION_STATUS_OPEN = QUOTATION_STATUS_OPEN;

  ngOnInit() {
    this.billingLabelTypeCodeAffaire = this.constantService.getBillingLabelTypeCodeAffaire();
    this.quotationDetailsForm = this.formBuilder.group({});

    this.refreshQuotation();
  }

  refreshQuotation() {
    this.dislayAlreadyFilledAttachment = false;
    this.quotationService.getQuotation(this.activatedRoute.snapshot.params['id']).subscribe(response => {
      this.quotation = response;
      this.canEditQuotation = this.quotation.quotationStatus.code != QUOTATION_STATUS_VALIDATED_BY_CUSTOMER
        && this.quotation.quotationStatus.code != QUOTATION_STATUS_ABANDONED && this.quotation.quotationStatus.code != QUOTATION_STATUS_REFUSED_BY_CUSTOMER;
      this.appService.hideLoadingSpinner();
      this.loadQuotationDetails();
    })
  }

  loadQuotationDetails() {
    if (!this.quotation)
      return;

    this.assoAffaireOrderService.getAssoAffaireOrdersForQuotation(this.quotation).subscribe(response => {
      this.quotationAssoAffaireOrders = response;
      if (this.quotationAssoAffaireOrders && this.quotationAssoAffaireOrders.length > 0) {
        this.changeAffaire(this.quotationAssoAffaireOrders[0]);
      }
    })
    this.invoiceLabelResultService.getInvoiceLabelComputeResultForQuotation(this.quotation.id).subscribe(response => {
      this.quotationInvoiceLabelResult = response;
    })
    this.mailComputeResultService.getMailComputeResultForBillingForQuotation(this.quotation.id).subscribe(response => {
      this.quotationMailComputeResult = response;
    })
    this.mailComputeResultService.getMailComputeResultForDigitalForQuotation(this.quotation).subscribe(response => {
      this.quotationDigitalMailComputeResult = response;
    })
    this.invoiceLabelResultService.getPhysicalMailComputeResultForBillingForQuotation(this.quotation).subscribe(response => {
      this.quotationPhysicalMailComputeResult = response;
    })
    this.invoicingSummaryService.getInvoicingSummaryForQuotation(this.quotation.id).subscribe(response => {
      this.invoiceSummary = response;
      if (this.invoiceSummary.remainingToPay && Math.abs(this.invoiceSummary.remainingToPay) > INVOICING_PAYMENT_LIMIT_REFUND_EUROS
        && this.quotation && this.canEditQuotation)
        this.displayPayButton = this.quotation.quotationStatus.code == QUOTATION_STATUS_SENT_TO_CUSTOMER;
    })
    this.customerOrderService.getCustomerOrderForQuotation(this.quotation.id).subscribe(response => {
      if (response && response.id) {
        this.associatedCustomerOrder = response;
        if (this.associatedCustomerOrder)
          this.openValidatedQuotationModal();
      }
    })
  }

  getQuotationBillingMailList() {
    if (this.quotation && this.quotationMailComputeResult)
      return getCustomerOrderBillingMailList(this.quotationMailComputeResult);
    return null;
  }

  getQuotationDigitalMailList() {
    if (this.quotation && this.quotationDigitalMailComputeResult)
      return getCustomerOrderBillingMailList(this.quotationDigitalMailComputeResult);
    return null;
  }

  changeAffaire(asso: AssoAffaireOrder) {
    this.selectedAssoAffaireOrder = asso;
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
    if (this.quotation)
      this.assoAffaireOrderService.getAssoAffaireOrdersForQuotation(this.quotation).subscribe(response => {
        this.quotationAssoAffaireOrders = response;
        if (response && this.selectedAssoAffaireOrder)
          for (let asso of this.quotationAssoAffaireOrders)
            if (asso.id == this.selectedAssoAffaireOrder.id)
              this.changeAffaire(asso);
      })
  }

  toggleDislayAlreadyFilledAttachment() {
    this.dislayAlreadyFilledAttachment = !this.dislayAlreadyFilledAttachment;
  }

  editAffaireDetails(affaire: Affaire, event: any) {
    if (this.quotation)
      this.appService.openRoute(event, "account/affaire/edit/quotation/" + affaire.id + "/" + this.quotation.id, undefined);
  }

  editAddress(event: any) {
    if (this.quotation)
      this.appService.openRoute(event, "account/quotation/address/edit/" + this.quotation.id, undefined);
  }

  payQuotation(event: any) {
    if (this.quotation)
      this.appService.openRoute(event, "account/quotation/pay/" + this.quotation.id, undefined);
  }

  openOrderDetails(event: any, order: CustomerOrder) {
    this.appService.openRoute(event, "account/orders/details/" + order.id, undefined);
  }

  saveFieldsValue(service: Service) {
    this.serviceService.addOrUpdateService(service).subscribe(response => {
      this.appService.displayToast("Vos informations complémentaires ont bien été enrengistrées", false, "Succès", 15000);
      this.refreshCurrentAssoAffaireOrder();
    })
  }

  displayMyDoc(service: Service) {
    return service.assoServiceDocuments && service.assoServiceDocuments.find(a => a.isMandatory);
  }

  displayMyInformation(service: Service) {
    return service.assoServiceFieldTypes && service.assoServiceFieldTypes.find(a => a.isMandatory);
  }

  loadServiceDetails(service: Service, forceLoad: boolean) {
    if (service) {
      if (!this.quotationAttachments[service.id] || forceLoad)
        this.attachementService.getAttachmentsForProvisionOfService(service).subscribe(response => {
          this.quotationAttachments[service.id] = response;
        })
    }
  }

  resumeDraft(event: any) {
    if (this.quotation && this.quotation.id)
      this.appService.openRoute(event, "quotation/resume/quotation/" + this.quotation.id, undefined);
  }

  finalCancelDraft(event: any) {
    if (this.quotation && this.quotation.id) {
      this.appService.showLoadingSpinner();
      this.quotationService.cancelQuotation(this.quotation.id).subscribe(response => {
        this.refreshQuotation();
      });
    }
  }

  cancelDraft() {
    if (this.cancelQuotationModalInstance) {
      return;
    }

    this.cancelQuotationModalInstance = this.modalService.open(this.cancelQuotationModal, {
    });

    this.cancelQuotationModalInstance.result.finally(() => {
      this.cancelQuotationModalInstance = undefined;
    });
  }

  openValidatedQuotationModal() {
    if (this.validatedQuotationModalInstance) {
      return;
    }

    this.validatedQuotationModalInstance = this.modalService.open(this.validatedQuotationModal, {
    });

    this.validatedQuotationModalInstance.result.finally(() => {
      this.validatedQuotationModalInstance = undefined;
    });
  }
}
