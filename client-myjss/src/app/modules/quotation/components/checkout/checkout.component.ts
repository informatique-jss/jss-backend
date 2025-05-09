import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Subscription } from 'rxjs';
import { AppService } from '../../../../libs/app.service';
import { ConstantService } from '../../../../libs/constant.service';
import { validateEmail, validateFrenchPhone, validateInternationalPhone } from '../../../../libs/CustomFormsValidatorsHelper';
import { getDocument } from '../../../../libs/DocumentHelper';
import { copyObject } from '../../../../libs/GenericHelper';
import { CustomerOrder } from '../../../my-account/model/CustomerOrder';
import { Document } from '../../../my-account/model/Document';
import { Quotation } from '../../../my-account/model/Quotation';
import { CustomerOrderService } from '../../../my-account/services/customer.order.service';
import { QuotationService } from '../../../my-account/services/quotation.service';
import { ServiceService } from '../../../my-account/services/service.service';
import { City } from '../../../profile/model/City';
import { Mail } from '../../../profile/model/Mail';
import { Phone } from '../../../profile/model/Phone';
import { Responsable } from '../../../profile/model/Responsable';
import { Tiers } from '../../../profile/model/Tiers';
import { LoginService } from '../../../profile/services/login.service';
import { IQuotation } from '../../model/IQuotation';

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
  totalPriceWithoutVat: number = 0;
  totalVatPrice: number = 0;

  isMobile: boolean = false;
  isCartOpen: boolean = false;

  inputMail: string = "";
  isLinkSent: boolean = false;
  isSendingLink: boolean = false;
  intervalId: any;

  isIndividualTiers: boolean = false;
  isComputingPrice: boolean = false;

  documentTypeBilling = this.constantService.getDocumentTypeBilling();
  documentTypeDigital = this.constantService.getDocumentTypeDigital();
  documentTypePaper = this.constantService.getDocumentTypePaper();
  billingLabelTypeOther = this.constantService.getBillingLabelTypeOther();
  isExtRefMandatory: boolean = false;

  documentForm = this.formBuilder.group({
  });

  newMailBillingAffaire: string = "";
  newMailBillingClient: string = "";

  newMailDigitalAffaire: string = "";
  newMailDigitalClient: string = "";

  newPhoneTiers: string = "";
  newMailTiers: string = "";
  newPhoneResponsable: string = "";

  acceptDocs: boolean = false;
  acceptTerms: boolean = false;

  quotationPriceObservableRef: Subscription | undefined;

  constructor(
    private loginService: LoginService,
    private quotationService: QuotationService,
    private orderService: CustomerOrderService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private constantService: ConstantService,
    private serviceService: ServiceService
  ) { }

  ngOnInit() {
    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
      this.initIQuotation();
    })
    this.loginService.getCurrentUser(false, true).subscribe(response => {
      if (!response) {
        this.intervalId = setInterval(() => this.checkUserConnected(), 2000000);
      } else {
        this.logCurrentUser(response);
      }
    })
    this.initIQuotation();
  }

  ngOnDestroy() {
    if (this.quotationPriceObservableRef)
      this.quotationPriceObservableRef.unsubscribe();
  }

  /**
   * Init and save quotation
   */

  initIQuotation() {
    if (this.currentUser) {
      if (this.quotationService.getCurrentDraftQuotationId()) {
        this.quotationService.getQuotation(parseInt(this.quotationService.getCurrentDraftQuotationId()!)).subscribe(response => {
          this.quotation = response;
          this.prepareForPricingAndCompute(true);
        });
      } else if (this.orderService.getCurrentDraftOrderId()) {
        this.orderService.getCustomerOrder(parseInt(this.orderService.getCurrentDraftOrderId()!)).subscribe(response => {
          this.quotation = response;
          this.prepareForPricingAndCompute(true);
        });
      }
    } else {
      if (this.quotationService.getCurrentDraftQuotation()) {
        this.quotation = this.quotationService.getCurrentDraftQuotation()!;
      } else if (this.orderService.getCurrentDraftOrder()) {
        this.quotation = this.orderService.getCurrentDraftOrder()!;
      }
      this.prepareForPricingAndCompute();
    }
  }

  makeOrder() {
    this.saveOrder();
    if (!this.acceptDocs || !this.acceptTerms) {
      this.appService.displayToast("Vous devez accepter les conditions ci-dessus pour pouvoir passer commande", true, "Validation de commande impossible", 5000);
      return;
    }
  }

  saveOrder() {
    if (!this.currentUser)
      if (this.quotation)
        this.quotationService.setCurrentDraftQuotation(this.quotation);
  }

  saveDraftQuotation() {
    if (this.quotation) {
      if (!this.currentUser)
        if (this.quotation.isQuotation)
          this.quotationService.setCurrentDraftQuotation(this.quotation);
        else
          this.orderService.setCurrentDraftOrder(this.quotation);
    }
  }

  /**
   * Price events management
   */

  prepareForPricingAndCompute(isFromInit = false) {
    this.isComputingPrice = true;
    this.populateEmptyResponsable();
    this.initEmptyDocuments();
    this.computeQuotationPrice(isFromInit);
  }

  finalizePricingAnswer() {
    this.computeTotalPrices();
    this.saveDraftQuotation();
    this.isComputingPrice = false;
  }

  populateEmptyResponsable() {
    if (this.quotation) {
      if (!this.quotation.responsable)
        this.quotation.responsable = { tiers: {} as Tiers, mail: {} as Mail } as Responsable;

      if (this.quotation.responsable) {
        if (!this.quotation.responsable.country) {
          this.quotation.responsable.country = this.constantService.getResponsableDummyCustomerFrance().country;
        }
        if (this.quotation.responsable.tiers) {
          if (!this.quotation.responsable.tiers.country) {
            this.quotation.responsable.tiers.country = this.constantService.getResponsableDummyCustomerFrance().tiers.country;
          }
          if (!this.quotation.responsable.tiers.city) {
            this.quotation.responsable.tiers.city = this.constantService.getResponsableDummyCustomerFrance().tiers.city;
          }
        }
      }
    }
  }

  initEmptyDocuments() {
    if (this.quotation) {
      if (!this.quotation.responsable)
        return;

      if (!this.quotation.documents || this.quotation.documents.length == 0)
        this.quotation.documents = [];
      let responsableForDocument = this.quotation.responsable && this.quotation.responsable.id ? this.quotation.responsable : this.constantService.getResponsableDummyCustomerFrance();

      if (responsableForDocument && (!this.quotation.documents || this.quotation.documents.length == 0)) {
        this.quotation.documents.push(copyObject(getDocument(this.constantService.getDocumentTypeBilling(), responsableForDocument)))
        this.quotation.documents.push(copyObject(getDocument(this.constantService.getDocumentTypeDigital(), responsableForDocument)))
        this.quotation.documents.push(copyObject(getDocument(this.constantService.getDocumentTypePaper(), responsableForDocument)))
      }

      //TODO delete after test
      for (let doc of this.quotation.documents) {
        if (doc.documentType.id == this.constantService.getDocumentTypeBilling().id
          && doc.billingLabelType.id == this.constantService.getBillingLabelTypeOther().id) {
          doc.billingPostalCode = "76230";
          doc.billingLabelCity = { id: 106072 } as City;
        }
      }
    }
  }

  computeQuotationPrice(isFromInit: boolean) {
    if (this.quotationPriceObservableRef) {
      this.quotationPriceObservableRef.unsubscribe();
      this.quotationPriceObservableRef = undefined;
    }

    if (this.quotation) {
      if (this.currentUser) {
        if (!isFromInit) {
          if (this.quotationService.getCurrentDraftQuotationId()) {
            this.quotationService.getQuotation(parseInt(this.quotationService.getCurrentDraftQuotationId()!)).subscribe(response => {
              this.quotation = response;
              this.finalizePricingAnswer();
            });
          } else if (this.orderService.getCurrentDraftOrderId()) {
            this.orderService.getCustomerOrder(parseInt(this.orderService.getCurrentDraftOrderId()!)).subscribe(response => {
              this.quotation = response;
              this.finalizePricingAnswer();
            });
          }
        } else {
          this.finalizePricingAnswer();
        }
      } else {
        if (this.quotation.isQuotation) {
          this.quotationPriceObservableRef = this.quotationService.completePricingOfQuotation(
            this.quotation as Quotation, this.quotation.assoAffaireOrders[0].services[0].provisions[0].isEmergency).subscribe(res => {
              this.quotation!.assoAffaireOrders = res.assoAffaireOrders;
              this.finalizePricingAnswer();
            });
        } else {
          this.quotationPriceObservableRef = this.orderService.completePricingOfOrder(
            this.quotation as CustomerOrder, this.quotation.assoAffaireOrders[0].services[0].provisions[0].isEmergency).subscribe(res => {
              this.quotation!.assoAffaireOrders = res.assoAffaireOrders;
              this.finalizePricingAnswer();
            });
        }
      }
    }
  }

  computeTotalPrices() {
    if (this.quotation) {
      this.totalPriceWithoutVat = 0;
      this.totalVatPrice = 0;
      this.totalPrice = 0;
      for (let asso of this.quotation.assoAffaireOrders)
        for (let service of asso.services) {
          this.totalPriceWithoutVat += service.servicePreTaxPrice;
          this.totalVatPrice += service.serviceVatPrice;
          this.totalPrice += service.serviceTotalPrice;
        }
    }
  }

  setEmergencyOption() {
    if (this.quotation) {
      if (this.currentUser) {
        if (this.quotation.isQuotation) {
          this.quotationService.setEmergencyOnQuotation(this.quotation.id, this.quotation.assoAffaireOrders[0].services[0].provisions[0].isEmergency).subscribe(res => {
            this.prepareForPricingAndCompute();
          });
        } else {
          this.orderService.setEmergencyOnOrder(this.quotation.id, this.quotation.assoAffaireOrders[0].services[0].provisions[0].isEmergency).subscribe(res => {
            this.prepareForPricingAndCompute();
          });
        }
      } else {
        this.saveDraftQuotation();
        this.prepareForPricingAndCompute();
      }
    }
  }

  setDocumentValue(document: Document) {
    if (this.quotation && document.documentType === this.documentTypeBilling) {
      if (this.currentUser) {
        if (this.quotation.isQuotation) {
          this.quotationService.setDocumentOnQuotation(this.quotation.id, document).subscribe(res => {
            this.prepareForPricingAndCompute();
          });
        } else {
          this.orderService.setDocumentOnOrder(this.quotation.id, document).subscribe(res => {
            this.prepareForPricingAndCompute();
          });
        }
      } else {
        this.saveDraftQuotation();
        this.prepareForPricingAndCompute();
      }
    }
  }

  applyCoupon() {
    this.appService.displayToast("Le code de réduction utilisé n'existe pas ou a déjà été utilisé", true, "Code de réduction invalide", 5000);
  }

  deleteService(serviceIndex: number, assoIndex: number) {
    if (this.quotation) {
      if (this.currentUser) {
        //TO implement
        //  this.serviceService.deleteService(this.quotation.assoAffaireOrders[assoIndex].services[serviceIndex].id).suscribe(response=>{
        //  this.prepareForPricingAndCompute();
        //})
      } else {
        this.quotation.assoAffaireOrders[assoIndex].services.splice(serviceIndex, 1);
        this.saveDraftQuotation();
        this.prepareForPricingAndCompute();
      }
    }
  }

  /**
   * Log user management
   */

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

  /**
   * Forms function
   */

  deleteMail(mail: Mail, document: Document, isAffaire: boolean) {
    if (document)
      if (isAffaire)
        document.mailsAffaire.splice(document.mailsAffaire.indexOf(mail), 1);
      else
        document.mailsClient.splice(document.mailsClient.indexOf(mail), 1);
  }

  deleteMailTiers(mail: Mail, tiers: Tiers, isAffaire: boolean) {
    if (tiers)
      if (isAffaire)
        tiers.mails.splice(tiers.mails.indexOf(mail), 1);
  }

  deletePhone(phone: Phone, responsable: Responsable, isAffaire: boolean) {
    if (responsable)
      if (!isAffaire)
        responsable.phones.splice(responsable.phones.indexOf(phone), 1);
      else
        responsable.tiers.phones.splice(responsable.tiers.phones.indexOf(phone), 1);
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

  addPhone(responsable: Responsable, isAffaire: boolean) {
    if (responsable)
      if (isAffaire) {
        if (this.newPhoneTiers && (validateFrenchPhone(this.newPhoneTiers) || validateInternationalPhone(this.newPhoneTiers))) {
          let phone = {} as Phone;
          phone.phoneNumber = this.newPhoneTiers;
          if (!responsable.tiers.phones)
            responsable.tiers.phones = [];
          responsable.tiers.phones.push(phone);
          this.newPhoneTiers = "";
        }
      } else {
        if (this.newPhoneResponsable && (validateFrenchPhone(this.newPhoneResponsable) || validateInternationalPhone(this.newPhoneResponsable))) {
          let phone = {} as Phone;
          phone.phoneNumber = this.newPhoneResponsable;
          if (!responsable.phones)
            responsable.phones = [];
          responsable.phones.push(phone);
          this.newPhoneResponsable = "";
        }
      }
  }

  addMailTiers(tiers: Tiers) {
    if (tiers)
      if (this.newMailTiers && (validateEmail(this.newMailTiers))) {
        let mail = {} as Mail;
        mail.mail = this.newMailTiers;
        if (!tiers.mails)
          tiers.mails = [];
        tiers.mails.push(mail);
        this.newMailTiers = "";
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

  toggleTiersIndividual() {
    if (this.quotation && this.quotation.responsable && this.quotation.responsable.tiers)
      this.quotation.responsable.tiers.isIndividual = !this.quotation.responsable.tiers.isIndividual;
  }
}
