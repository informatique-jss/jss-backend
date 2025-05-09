import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Subscription } from 'rxjs';
import { AppService } from '../../../../libs/app.service';
import { ConstantService } from '../../../../libs/constant.service';
import { validateEmail, validateFrenchPhone, validateInternationalPhone } from '../../../../libs/CustomFormsValidatorsHelper';
import { getDocument } from '../../../../libs/DocumentHelper';
import { copyObject } from '../../../../libs/GenericHelper';
import { BillingLabelType } from '../../../my-account/model/BillingLabelType';
import { CustomerOrder } from '../../../my-account/model/CustomerOrder';
import { Document } from '../../../my-account/model/Document';
import { Quotation } from '../../../my-account/model/Quotation';
import { CustomerOrderService } from '../../../my-account/services/customer.order.service';
import { DocumentService } from '../../../my-account/services/document.service';
import { QuotationService } from '../../../my-account/services/quotation.service';
import { City } from '../../../profile/model/City';
import { Civility } from '../../../profile/model/Civility';
import { Country } from '../../../profile/model/Country';
import { Mail } from '../../../profile/model/Mail';
import { Phone } from '../../../profile/model/Phone';
import { Responsable } from '../../../profile/model/Responsable';
import { Tiers } from '../../../profile/model/Tiers';
import { LoginService } from '../../../profile/services/login.service';
import { IQuotation } from '../../model/IQuotation';
import { CityService } from '../../services/city.service';
import { CivilityService } from '../../services/civility.service';
import { CountryService } from '../../services/country.service';

@Component({
  selector: 'checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css'],
  standalone: false
})
export class CheckoutComponent implements OnInit, AfterViewInit {


  @ViewChild('orderButton') orderButton: ElementRef | undefined;

  quotation: IQuotation | undefined;
  currentUser: Responsable | undefined;
  totalPrice: number = 0;
  isComputingPrice: boolean = false;
  totalPriceWithoutVat: number = 0;
  totalVatPrice: number = 0;

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
  postalCode: string = "06210";
  city: City | undefined;

  validateEmail = validateEmail;
  validateFrenchPhone = validateFrenchPhone;
  validateInternationalPhone = validateInternationalPhone;

  documentTypeBilling = this.constantService.getDocumentTypeBilling();
  documentTypeDigital = this.constantService.getDocumentTypeDigital();
  documentTypePaper = this.constantService.getDocumentTypePaper();
  isExtRefMandatory: boolean = false;

  documentForm = this.formBuilder.group({
  });


  billingLabelTypeCodeAffaire: BillingLabelType = this.constantService.getBillingLabelTypeCodeAffaire();
  billingLabelTypeCustomer: BillingLabelType = this.constantService.getBillingLabelTypeCustomer();
  billingLabelTypeOther: BillingLabelType = this.constantService.getBillingLabelTypeOther();
  newMailBillingAffaire: string = "";
  newMailBillingClient: string = "";

  newMailDigitalAffaire: string = "";
  newMailDigitalClient: string = "";

  newPhoneTiers: string = "";
  newMailTiers: string = "";
  newPhoneResponsable: string = "";

  acceptDocs: boolean = false;
  acceptTerms: boolean = false;

  isFirstViewRendered: boolean = false;

  quotationPriceObservableRef: Subscription | undefined;

  constructor(
    private loginService: LoginService,
    private quotationService: QuotationService,
    private orderService: CustomerOrderService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private constantService: ConstantService,
    private civilityService: CivilityService,
    private countryService: CountryService,
    private cityService: CityService,
    private documentService: DocumentService,
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

  ngAfterViewInit(): void {
    this.isFirstViewRendered = true;
    this.computeQuotationPrice();
  }


  ngOnDestroy() {
    if (this.quotationPriceObservableRef)
      this.quotationPriceObservableRef.unsubscribe();

  }

  initEmptyDocuments() {
    if (this.quotation) {
      if (!this.quotation.documents || this.quotation.documents.length == 0)
        this.quotation.documents = [];
      let responsableForDocument = undefined;
      if (!this.quotation.responsable) {
        if (this.currentUser) {
          responsableForDocument = this.currentUser;
          this.quotation.responsable = this.currentUser;
        } else {
          responsableForDocument = this.constantService.getResponsableDummyCustomerFrance();
          this.quotation.responsable = { tiers: { isIndividual: !this.isIndividualTiers } as Tiers, mail: {} as Mail } as Responsable;
          // TODO : delete after test
          this.city = { id: 114155 } as City;
          if (this.quotation && this.quotation.responsable) {
            this.quotation.responsable.tiers.postalCode = this.postalCode;
            this.quotation.responsable.tiers.city = this.city;

            this.quotation.responsable.postalCode = this.postalCode;
            this.quotation.responsable.city = this.city;

            for (let doc of this.quotation.documents) {
              if (doc.billingLabelType.id === this.billingLabelTypeOther.id) {
                doc.billingPostalCode = this.postalCode;
                doc.billingLabelCity = this.city!;
              }
            }
          }
        }
      } else {
        responsableForDocument = this.quotation.responsable;
        // TODO : delete after test
        this.city = { id: 114155 } as City;
        if (this.quotation && this.quotation.responsable) {
          this.quotation.responsable.tiers.postalCode = this.postalCode;
          this.quotation.responsable.tiers.city = this.city;

          this.quotation.responsable.postalCode = this.postalCode;
          this.quotation.responsable.city = this.city;

          for (let doc of this.quotation.documents) {
            if (doc.documentType.id == this.constantService.getDocumentTypeBilling().id && doc.billingLabelType.id == this.billingLabelTypeOther.id) {
              doc.billingPostalCode = this.postalCode;
              doc.billingLabelCity = this.city!;
            }
          }
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

  toggleIsRecipientAffaire(docIndex: number) {
    if (this.quotation)
      this.quotation.documents[docIndex].isRecipientAffaire = !this.quotation.documents[docIndex].isRecipientAffaire;
  }

  toggleIsRecipientClient(docIndex: number) {
    if (this.quotation)
      this.quotation.documents[docIndex].isRecipientClient = !this.quotation.documents[docIndex].isRecipientClient;
  }

  toggleAddToAffaireMailList(docIndex: number) {
    if (this.quotation)
      this.quotation.documents[docIndex].addToAffaireMailList = !this.quotation.documents[docIndex].addToAffaireMailList;
  }

  toggleAddToClientMailList(docIndex: number) {
    if (this.quotation)
      this.quotation.documents[docIndex].addToClientMailList = !this.quotation.documents[docIndex].addToClientMailList;
  }

  initIQuotation() {
    if (this.currentUser) {
      if (this.quotationService.getCurrentDraftQuotationId()) {
        this.quotationService.getQuotation(parseInt(this.quotationService.getCurrentDraftQuotationId()!)).subscribe(response => {
          this.quotation = response;
          this.computeTotalPrices();
          this.initEmptyDocuments();
        });
      } else if (this.orderService.getCurrentDraftOrderId()) {
        this.orderService.getCustomerOrder(parseInt(this.orderService.getCurrentDraftOrderId()!)).subscribe(response => {
          this.quotation = response;
          this.computeTotalPrices();
          this.initEmptyDocuments();
        });
      }
    } else {
      if (this.quotationService.getCurrentDraftQuotation()) {
        this.quotation = this.quotationService.getCurrentDraftQuotation()!;
        this.initEmptyDocuments();
        this.computeTotalPrices();
      } else if (this.orderService.getCurrentDraftOrder()) {
        this.quotation = this.orderService.getCurrentDraftOrder()!;
        this.initEmptyDocuments();
        this.computeTotalPrices();
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

  computeEmergencyPrice() {
    if (this.quotation && this.isFirstViewRendered) {
      this.isComputingPrice = true;
      if (this.currentUser) {
        if (this.quotation.isQuotation) {
          this.quotationService.setEmergencyOnQuotation(this.quotation.id, this.quotation.assoAffaireOrders[0].services[0].provisions[0].isEmergency).subscribe(res => {
            if (res)
              this.quotationService.getQuotation(this.quotation!.id).subscribe(res => this.quotation = res);
            this.computeTotalPrices();
            this.isComputingPrice = false;
          });
        } else {
          this.orderService.setEmergencyOnOrder(this.quotation.id, this.quotation.assoAffaireOrders[0].services[0].provisions[0].isEmergency).subscribe(res => {
            if (res)
              this.orderService.getCustomerOrder(this.quotation!.id).subscribe(res => this.quotation = res);
            this.computeTotalPrices();
            this.isComputingPrice = false;
          });
        }
      } else {
        if (this.quotation.responsable && this.quotation.responsable.tiers &&
          (!this.quotation.responsable.country || !this.quotation.responsable.tiers.country || !this.quotation.responsable.tiers.city)) {
          this.populateEmptyResponsable();
        }
        this.computeQuotationPrice();
      }
    }
  }

  computeDocumentPrice(document: Document) {
    if (this.quotation && this.isFirstViewRendered) {
      this.isComputingPrice = true;
      if (this.currentUser) {
        if (this.quotation.isQuotation) {
          this.quotationService.setDocumentOnQuotation(this.quotation.id, document).subscribe(res => {
            if (res && document.documentType === this.documentTypeBilling) {
              this.quotationService.getQuotation(this.quotation!.id).subscribe(res => this.quotation = res);
              this.computeTotalPrices();
            }
            this.isComputingPrice = false;
          });
        } else {
          this.orderService.setDocumentOnOrder(this.quotation.id, document).subscribe(res => {
            if (res && document.documentType === this.documentTypeBilling) {
              this.orderService.getCustomerOrder(this.quotation!.id).subscribe(res => this.quotation = res);
              this.computeTotalPrices();
            }
            this.isComputingPrice = false;
          });
        }
      } else {
        if (this.quotation.responsable && this.quotation.responsable.tiers &&
          (!this.quotation.responsable.country || !this.quotation.responsable.tiers.country || !this.quotation.responsable.tiers.city)) {
          this.populateEmptyResponsable();
        }
        this.computeQuotationPrice();
      }
    }
  }

  computeQuotationPrice() {
    if (this.quotation) {
      this.isComputingPrice = true;
      if (this.quotation.isQuotation) {
        this.quotationPriceObservableRef = this.quotationService.completePricingOfQuotation(
          this.quotation as Quotation, this.quotation.assoAffaireOrders[0].services[0].provisions[0].isEmergency).subscribe(res => {
            this.updateNotConnectedUserQuotationAndPrices(res)
          });
      } else {
        this.quotationPriceObservableRef = this.orderService.completePricingOfOrder(
          this.quotation as CustomerOrder, this.quotation.assoAffaireOrders[0].services[0].provisions[0].isEmergency).subscribe(res => {
            this.updateNotConnectedUserQuotationAndPrices(res)
          });
      }
    }
  }

  updateNotConnectedUserQuotationAndPrices(res: IQuotation) {
    this.quotation!.assoAffaireOrders = res.assoAffaireOrders;
    this.computeTotalPrices();
    if (!this.currentUser)
      this.saveDraftQuotation();
    this.isComputingPrice = false;
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

  applyCoupon() {
    this.appService.displayToast("Le code de réduction utilisé n'existe pas ou a déjà été utilisé", true, "Code de réduction invalide", 5000);
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

  deleteService(serviceIndex: number, assoIndex: number) {
    if (this.quotation) {
      this.quotation.assoAffaireOrders[assoIndex].services.splice(serviceIndex, 1);
      this.computeTotalPrices();
      this.saveDraftQuotation();
    }
  }

  populateEmptyResponsable() {
    if (this.quotation && this.quotation.responsable) {
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