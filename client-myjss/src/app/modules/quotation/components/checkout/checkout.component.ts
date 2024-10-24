import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from '../../../../libs/app.service';
import { ConstantService } from '../../../../libs/constant.service';
import { validateEmail } from '../../../../libs/CustomFormsValidatorsHelper';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { initTooltips } from '../../../my-account/components/orders/orders.component';
import { Document } from '../../../my-account/model/Document';
import { DocumentType } from '../../../my-account/model/DocumentType';
import { DocumentService } from '../../../my-account/services/document.service';
import { Mail } from '../../../profile/model/Mail';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { ServiceTypeChosen } from '../../model/ServiceTypeChosen';
import { UserCustomerOrder } from '../../model/UserCustomerOrder';
import { UserCustomerOrderService } from '../../services/user.customer.service';
import { TYPE_CHOSEN_ORDER, TYPE_CHOSEN_QUOTATION } from '../choose-type/choose-type.component';

@Component({
  selector: 'checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {

  @Input() serviceTypesSelected: ServiceTypeChosen[] | undefined;
  @Input() typeChosen: string | undefined;
  @Output() onRemoveServiceChoosen = new EventEmitter<ServiceTypeChosen>();

  currentUser: Responsable | undefined;

  userCustomerOrder: UserCustomerOrder = {} as UserCustomerOrder;

  documentTypeBilling = this.constantService.getDocumentTypeBilling();
  documentTypeDigital = this.constantService.getDocumentTypeDigital();
  documentTypePaper = this.constantService.getDocumentTypePaper();
  billingLabelTypeAffaire = this.constantService.getBillingLabelTypeCodeAffaire();
  billingLabelTypeCustomer = this.constantService.getBillingLabelTypeCustomer();
  billingLabelTypeOther = this.constantService.getBillingLabelTypeOther();

  newMailBillingAffaire: string = "";
  newMailBillingClient: string = "";

  newMailDigitalAffaire: string = "";
  newMailDigitalClient: string = "";

  defineCustomDocuments: boolean = false;
  validateCgv: boolean = false;

  constructor(
    private loginService: LoginService,
    private constantService: ConstantService,
    private documentService: DocumentService,
    private formBuilder: FormBuilder,
    private userCustomerOrderService: UserCustomerOrderService,
    private appService: AppService,
  ) { }

  capitalizeName = capitalizeName;
  documentForm = this.formBuilder.group({});

  ngOnInit() {
    initTooltips();
    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
      this.documentService.getDocumentForResponsable(this.currentUser.id).subscribe(response => {
        this.userCustomerOrder.billingDocument = this.getDocument(this.documentTypeBilling, response);

        if (this.userCustomerOrder.billingDocument.billingLabelType.id == this.billingLabelTypeAffaire.id)
          this.userCustomerOrder.billingDocument.billingLabelType = this.billingLabelTypeAffaire;
        if (this.userCustomerOrder.billingDocument.billingLabelType.id == this.billingLabelTypeCustomer.id)
          this.userCustomerOrder.billingDocument.billingLabelType = this.billingLabelTypeCustomer;
        if (this.userCustomerOrder.billingDocument.billingLabelType.id == this.billingLabelTypeOther.id)
          this.userCustomerOrder.billingDocument.billingLabelType = this.billingLabelTypeOther;

        this.userCustomerOrder.digitalDocument = this.getDocument(this.documentTypeDigital, response);
        this.userCustomerOrder.paperDocument = this.getDocument(this.documentTypePaper, response);

        this.computePrices();
      })
    })
  }

  isAnOrder() {
    return this.typeChosen && this.typeChosen == TYPE_CHOSEN_ORDER;
  }

  isAQuotation() {
    return this.typeChosen && this.typeChosen == TYPE_CHOSEN_QUOTATION;
  }

  removeService(service: ServiceTypeChosen) {
    this.onRemoveServiceChoosen.next(service);
  }

  computePrices() {
    if (this.userCustomerOrder)
      if (this.serviceTypesSelected) {
        this.userCustomerOrder.serviceTypes = this.serviceTypesSelected;
        if (this.userCustomerOrder.serviceTypes) {
          let i = 0;
          for (let service of this.userCustomerOrder.serviceTypes) {
            service.temporaryId = i;
            i++;
          }
          this.userCustomerOrderService.completePricingOfUserCustomerOrder(this.userCustomerOrder).subscribe(response => {
            if (response) {
              this.userCustomerOrder.preTaxPrice = response.preTaxPrice;
              this.userCustomerOrder.totalPrice = response.totalPrice;
              this.userCustomerOrder.vatPrice = response.vatPrice;
              if (this.userCustomerOrder.serviceTypes && response.serviceTypes)
                for (let serviceClient of this.userCustomerOrder.serviceTypes) {
                  for (let serviceServer of response.serviceTypes) {
                    if (serviceClient.temporaryId == serviceServer.temporaryId) {
                      serviceClient.discountedAmount = serviceServer.discountedAmount;
                      serviceClient.preTaxPrice = serviceServer.preTaxPrice;
                    }
                  }
                }
            }
          })
        }
      } else {
        this.userCustomerOrder.preTaxPrice = undefined;
        this.userCustomerOrder.vatPrice = undefined;
        this.userCustomerOrder.totalPrice = undefined;
      }
  }

  validateCustomerOrder() {
    this.userCustomerOrder.isCustomerOrder = true;
    this.userCustomerOrder.isDraft = false;
    this.saveOrder();
  }

  validateQuotation() {
    this.userCustomerOrder.isCustomerOrder = false;
    this.userCustomerOrder.isDraft = false;
    this.saveOrder();
  }

  validateCustomerOrderDraft() {
    this.userCustomerOrder.isCustomerOrder = true;
    this.userCustomerOrder.isDraft = true;
    this.saveOrder();
  }

  validateQuotationDraft() {
    this.userCustomerOrder.isCustomerOrder = false;
    this.userCustomerOrder.isDraft = true;
    this.saveOrder();
  }

  saveOrder() {
    if (!this.validateCgv) {
      this.appService.displayToast("La validation des CGV est obligatoire", true, "Validation", 10000);
      return;
    }
  }

  getDocument(documentType: DocumentType, documents: Document[]) {
    // Document currently exists
    if (documents && documents.length > 0) {
      for (let document of documents)
        if (document.documentType.id == documentType.id) {
          return document;
        }
    }
    return { isRecipientClient: true, isRecipientAffaire: false, addToAffaireMailList: false, addToClientMailList: false } as Document;
  }

  deleteMail(mail: Mail, document: Document, isAffaire: boolean) {
    if (document)
      if (isAffaire)
        document.mailsAffaire.splice(document.mailsAffaire.indexOf(mail), 1);
      else
        document.mailsClient.splice(document.mailsClient.indexOf(mail), 1);
  }

  addMail(document: Document, isAffaire: boolean) {
    if (document)
      if (isAffaire && this.newMailBillingAffaire && validateEmail(this.newMailBillingAffaire)) {
        let mail = {} as Mail;
        mail.mail = this.newMailBillingAffaire;
        if (!document.mailsAffaire)
          document.mailsAffaire = [];
        document.mailsAffaire.push(mail);
        this.newMailBillingAffaire = "";
      } else if (this.newMailBillingClient && validateEmail(this.newMailBillingClient)) {
        let mail = {} as Mail;
        mail.mail = this.newMailBillingClient;
        if (!document.mailsClient)
          document.mailsClient = [];
        document.mailsClient.push(mail);
        this.newMailBillingClient = "";
      }
  }

  addMailDigital(document: Document, isAffaire: boolean) {
    if (document)
      if (isAffaire && this.newMailDigitalAffaire && validateEmail(this.newMailDigitalAffaire)) {
        let mail = {} as Mail;
        mail.mail = this.newMailDigitalAffaire;
        if (!document.mailsAffaire)
          document.mailsAffaire = [];
        document.mailsAffaire.push(mail);
        this.newMailDigitalAffaire = "";
      } else if (this.newMailDigitalClient && validateEmail(this.newMailDigitalClient)) {
        let mail = {} as Mail;
        mail.mail = this.newMailDigitalClient;
        if (!document.mailsClient)
          document.mailsClient = [];
        document.mailsClient.push(mail);
        this.newMailDigitalClient = "";
      }
  }
}
