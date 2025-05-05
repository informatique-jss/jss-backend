import { Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from '../../../../libs/app.service';
import { ConstantService } from '../../../../libs/constant.service';
import { validateEmail, validateFrenchPhone, validateInternationalPhone } from '../../../../libs/CustomFormsValidatorsHelper';
import { getDocument } from '../../../../libs/DocumentHelper';
import { copyObject } from '../../../../libs/GenericHelper';
import { BillingLabelType } from '../../../my-account/model/BillingLabelType';
import { Document } from '../../../my-account/model/Document';
import { CustomerOrderService } from '../../../my-account/services/customer.order.service';
import { DocumentService } from '../../../my-account/services/document.service';
import { QuotationService } from '../../../my-account/services/quotation.service';
import { Civility } from '../../../profile/model/Civility';
import { Country } from '../../../profile/model/Country';
import { Mail } from '../../../profile/model/Mail';
import { Responsable } from '../../../profile/model/Responsable';
import { Tiers } from '../../../profile/model/Tiers';
import { LoginService } from '../../../profile/services/login.service';
import { IQuotation } from '../../model/IQuotation';
import { CivilityService } from '../../services/civility.service';
import { CountryService } from '../../services/country.service';

@Component({
  selector: 'checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css'],
  standalone: false
})
export class CheckoutComponent implements OnInit {

  @ViewChild('orderButton') orderButton: ElementRef | undefined;

  quotation: IQuotation | undefined;
  currentUser: Responsable | undefined;
  totalPrice: number = 0;

  isMobile: boolean = false;
  isCartOpen: boolean = false;

  inputMail: string = "";
  isLinkSent: boolean = false;
  isSendingLink: boolean = false;
  intervalId: any;
  countries: Country[] | undefined;
  civilities: Civility[] | undefined;

  isIndividualTiers: boolean = false;

  checkedOnce: boolean = false;
  countryFrance: Country = this.constantService.getCountryFrance();

  validateEmail = validateEmail;
  validateFrenchPhone = validateFrenchPhone;
  validateInternationalPhone = validateInternationalPhone;

  documentTypeBilling = this.constantService.getDocumentTypeBilling();
  documentTypeDigital = this.constantService.getDocumentTypeDigital();
  documentTypePaper = this.constantService.getDocumentTypePaper();
  isExtRefMandatory: boolean = false;

  documentForm = this.formBuilder.group({});
  billingLabelTypeCodeAffaire: BillingLabelType = this.constantService.getBillingLabelTypeCodeAffaire();
  billingLabelTypeCustomer: BillingLabelType = this.constantService.getBillingLabelTypeCustomer();
  billingLabelTypeOther: BillingLabelType = this.constantService.getBillingLabelTypeOther();
  newMailBillingAffaire: string = "";
  newMailBillingClient: string = "";

  newMailDigitalAffaire: string = "";
  newMailDigitalClient: string = "";

  constructor(
    private loginService: LoginService,
    private quotationService: QuotationService,
    private orderService: CustomerOrderService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private constantService: ConstantService,
    private civilityService: CivilityService,
    private countryService: CountryService,
    private documentService: DocumentService,
  ) { }

  ngOnInit() {
    this.checkScreenSize();
    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
      this.initIQuotation();
    })
    this.loginService.getCurrentUser(false, true).subscribe(response => {
      if (!response) {
        this.intervalId = setInterval(() => this.checkUserConnected(), 2000);
      } else {
        this.logCurrentUser(response);
      }
    })


    this.initIQuotation();
  }

  initEmptyDocuments() {
    if (this.quotation) {
      let responsableForDocument = undefined;
      if (!this.quotation.responsable) {
        if (this.currentUser) {
          responsableForDocument = this.currentUser;
          this.quotation.responsable = this.currentUser;
        } else {
          responsableForDocument = this.constantService.getResponsableDummyCustomerFrance();
          this.quotation.responsable = { tiers: { isIndividual: !this.isIndividualTiers } as Tiers, mail: {} as Mail } as Responsable;
        }
      }

      if (responsableForDocument && (!this.quotation.documents || this.quotation.documents.length == 0)) {
        this.quotation.documents.push(copyObject(getDocument(this.constantService.getDocumentTypeBilling(), responsableForDocument)))
        this.quotation.documents.push(copyObject(getDocument(this.constantService.getDocumentTypeDigital(), responsableForDocument)))
        this.quotation.documents.push(copyObject(getDocument(this.constantService.getDocumentTypePaper(), responsableForDocument)))
      }
    }
  }

  toggleTiersIndividual() {
    if (this.quotation && this.quotation.responsable && this.quotation.responsable.tiers)
      this.quotation.responsable.tiers.isIndividual = !this.quotation.responsable.tiers.isIndividual;
  }

  initIQuotation() {
    if (this.currentUser) {
      if (this.quotationService.getCurrentDraftQuotationId()) {
        this.quotationService.getQuotation(parseInt(this.quotationService.getCurrentDraftQuotationId()!)).subscribe(response => {
          this.quotation = response;
          this.initEmptyDocuments();
        });
      } else if (this.orderService.getCurrentDraftOrderId()) {
        this.orderService.getCustomerOrder(parseInt(this.orderService.getCurrentDraftOrderId()!)).subscribe(response => {
          this.quotation = response;
          this.initEmptyDocuments();
        });
      }
    } else {
      if (this.quotationService.getCurrentDraftQuotation()) {
        this.quotation = this.quotationService.getCurrentDraftQuotation()!;
        this.initEmptyDocuments();
        this.quotationService.setCurrentDraftQuotation(this.quotation);
      } else if (this.orderService.getCurrentDraftOrder()) {
        this.quotation = this.orderService.getCurrentDraftOrder()!;
        this.initEmptyDocuments();
        this.orderService.setCurrentDraftOrder(this.quotation);
      }
    }
  }

  sendConnectionLink() {
    if (validateEmail(this.inputMail)) {
      this.isSendingLink = true;
      this.loginService.sendConnectionLink(this.inputMail).subscribe(response => {
        this.isLinkSent = true;
        this.isSendingLink = false;
      })
    } else {
      this.appService.displayToast("L'adresse saisie n'est pas une adresse mail valide", true, "Erreur", 5000);
    }
  }

  computePrices() {
    //TODO
  }

  checkUserConnected() {
    this.loginService.getCurrentUser(true, true).subscribe(response => {
      if (response) {
        clearInterval(this.intervalId);
        this.logCurrentUser(response);
      }
    });
  }

  logCurrentUser(response: Responsable) {
    this.currentUser = response;
    if (this.quotation) {
      this.quotation.responsable = undefined;
      this.quotation.documents = [];
      this.initEmptyDocuments();
    }
  }

  // TODO non relu
  deleteMail(mail: Mail, document: Document, isAffaire: boolean) {
    if (document)
      if (isAffaire)
        document.mailsAffaire.splice(document.mailsAffaire.indexOf(mail), 1);
      else
        document.mailsClient.splice(document.mailsClient.indexOf(mail), 1);
  }

  // TODO non relu
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

  // TODO non relu
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

  makeOrder() {
    if (this.currentUser)
      if (this.quotation)
        this.quotationService.setCurrentDraftQuotation(this.quotation);
  }

  checkScreenSize() {
    this.isMobile = window.innerWidth < 992;
  }

  closeCartPanel() {
    this.isCartOpen = false;
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.checkScreenSize();
  }

  @HostListener('document:click', ['$event'])
  closePanelOnClickOutside(event: MouseEvent) {
    const panel = document.getElementById('cart-panel');
    const button = document.getElementById('cart-button');

    if (this.isCartOpen && panel && !panel.contains(event.target as Node)) {
      this.isCartOpen = false;
    } else if (!this.isCartOpen && button && button.contains(event.target as Node)) {
      this.isCartOpen = true;
      if (this.isCartOpen) {
        setTimeout(() => {
          if (this.orderButton)
            this.orderButton?.nativeElement.scrollIntoView({ behavior: 'smooth', block: 'end' });
        }, 100);
      }
    }
  }

  ngOnDestroy() {
  }

}
