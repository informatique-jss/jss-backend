import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from '../../../../libs/app.service';
import { ConstantService } from '../../../../libs/constant.service';
import { validateEmail, validateFrenchPhone, validateInternationalPhone, validateSiret } from '../../../../libs/CustomFormsValidatorsHelper';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { initTooltips } from '../../../my-account/components/orders/orders.component';
import { Document } from '../../../my-account/model/Document';
import { DocumentType } from '../../../my-account/model/DocumentType';
import { AffaireService } from '../../../my-account/services/affaire.service';
import { DocumentService } from '../../../my-account/services/document.service';
import { City } from '../../../profile/model/City';
import { Civility } from '../../../profile/model/Civility';
import { Country } from '../../../profile/model/Country';
import { Mail } from '../../../profile/model/Mail';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { ServiceTypeChosen } from '../../model/ServiceTypeChosen';
import { UserCustomerOrder } from '../../model/UserCustomerOrder';
import { CityService } from '../../services/city.service';
import { CivilityService } from '../../services/civility.service';
import { CountryService } from '../../services/country.service';
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
  @Output() onEditServiceChoosen = new EventEmitter<ServiceTypeChosen>();
  @Output() onAddServiceChoosen = new EventEmitter<Boolean>();
  @ViewChild('topAlert') topAlert: ElementRef | undefined;

  currentUser: Responsable | undefined;

  userCustomerOrder: UserCustomerOrder = { "customerIsIndividual": false } as UserCustomerOrder;

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
  siretSearched: string = "";

  defineCustomDocuments: boolean = false;
  validateCgv: boolean = false;
  isSavingOrder: boolean = false;
  isSavingDraft: boolean = false;
  checkedOnce: boolean = false;

  isMoralPerson: boolean = true;

  debounce: any;
  loadingCustomerSiret: boolean = false;

  countries: Country[] | undefined;
  foundCities: City[] | undefined;
  civilities: Civility[] | undefined;
  countryFrance: Country = this.constantService.getCountryFrance();

  inputMail: string = "";
  intervalId: any;
  isLinkSent: boolean = false;
  isSendingLink: boolean = false;

  constructor(
    private loginService: LoginService,
    private constantService: ConstantService,
    private documentService: DocumentService,
    private formBuilder: FormBuilder,
    private userCustomerOrderService: UserCustomerOrderService,
    private appService: AppService,
    private affaireService: AffaireService,
    private countryService: CountryService,
    private cityService: CityService,
    private civilityService: CivilityService
  ) { }

  capitalizeName = capitalizeName;
  validateEmail = validateEmail;
  validateFrenchPhone = validateFrenchPhone;
  validateInternationalPhone = validateInternationalPhone;
  documentForm = this.formBuilder.group({});

  ngOnInit() {
    initTooltips();
    this.loginService.getCurrentUser(false, true).subscribe(response => {
      if (!response) {
        this.intervalId = setInterval(() => this.checkUserConnected(), 2000);
        this.defineCustomDocuments = true;
        this.civilityService.getCivilities().subscribe(response => this.civilities = response);
        this.countryService.getCountries().subscribe(response => {
          setTimeout(() => window.scrollTo({
            top: 0,
            behavior: 'smooth',
          }), 100);
          this.countries = response;
          if (this.countries)
            for (let country of this.countries)
              if (country.id == this.countryFrance.id) {
                this.userCustomerOrder.customerCountry = country;
                break;
              }
        });

        this.userCustomerOrder.billingDocument = { isRecipientClient: true } as Document;
        this.userCustomerOrder.digitalDocument = { isRecipientClient: true } as Document;
        this.userCustomerOrder.paperDocument = { isRecipientClient: true } as Document;
        this.userCustomerOrder.billingDocument.billingLabelType = this.billingLabelTypeCustomer;

        this.computePrices();
      } else {
        this.logCurrentUser(response);
      }
    })
  }

  ngOnDestroy() {
    clearInterval(this.intervalId);
  }

  checkUserConnected() {
    this.loginService.getCurrentUser(true, true).subscribe(response => {
      if (response) {
        clearInterval(this.intervalId);
        this.logCurrentUser(response);
      }
    });
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

  logCurrentUser(response: Responsable) {
    this.currentUser = response;
    this.defineCustomDocuments = false;
    this.documentService.getDocumentForResponsable(this.currentUser.id).subscribe(response => {
      setTimeout(() => window.scrollTo({
        top: 0,
        behavior: 'smooth',
      }), 100);

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

  editService(service: ServiceTypeChosen) {
    this.onEditServiceChoosen.next(service);
  }

  addService() {
    this.onAddServiceChoosen.next(true);
  }

  computePrices() {
    if (this.userCustomerOrder) {
      this.userCustomerOrder.preTaxPrice = undefined;
      this.userCustomerOrder.vatPrice = undefined;
      this.userCustomerOrder.totalPrice = undefined;
      if (this.userCustomerOrder && this.userCustomerOrder.serviceTypes)
        for (let service of this.userCustomerOrder.serviceTypes) {
          service.preTaxPrice = undefined;
          service.discountedAmount = undefined;
        }

      if (this.serviceTypesSelected) {
        this.userCustomerOrder.serviceTypes = this.serviceTypesSelected;
        if (this.userCustomerOrder.serviceTypes) {
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
              console.log(this.userCustomerOrder);
            }
          })
        }
      }
    }
  }

  validateCustomerOrder() {
    this.userCustomerOrder.isCustomerOrder = true;
    this.userCustomerOrder.isDraft = false;
    this.isSavingOrder = true;
    this.saveOrder();
  }

  validateQuotation() {
    this.userCustomerOrder.isCustomerOrder = false;
    this.userCustomerOrder.isDraft = false;
    this.isSavingOrder = true;
    this.saveOrder();
  }

  validateCustomerOrderDraft() {
    this.userCustomerOrder.isCustomerOrder = true;
    this.userCustomerOrder.isDraft = true;
    this.isSavingDraft = true;
    this.saveOrder();
  }

  validateQuotationDraft() {
    this.userCustomerOrder.isCustomerOrder = false;
    this.userCustomerOrder.isDraft = true;
    this.isSavingDraft = true;
    this.saveOrder();
  }

  saveOrder() {
    if (!this.validateCgv) {
      this.appService.displayToast("La validation des CGV est obligatoire", true, "Validation", 10000);
      this.isSavingDraft = false;
      this.isSavingOrder = false;
      return;
    }

    if (!this.currentUser) {
      let hasError = false;
      if (this.isMoralPerson && !this.userCustomerOrder.customerDenomination)
        hasError = true;
      if (!this.userCustomerOrder.customerAddress)
        hasError = true;
      if (!this.userCustomerOrder.customerCity)
        hasError = true;
      if (!this.userCustomerOrder.customerCountry)
        hasError = true;
      if (!this.userCustomerOrder.responsableCivility)
        hasError = true;
      if (!this.userCustomerOrder.responsableFirstname)
        hasError = true;
      if (!this.userCustomerOrder.responsableLastname)
        hasError = true;
      if (!this.userCustomerOrder.responsableMail || !validateEmail(this.userCustomerOrder.responsableMail))
        hasError = true;
      if (this.userCustomerOrder.responsablePhone && !validateFrenchPhone(this.userCustomerOrder.responsablePhone) && !validateInternationalPhone(this.userCustomerOrder.responsablePhone))
        hasError = true;
      if (this.userCustomerOrder.customerCountry.id == this.countryFrance.id && !this.userCustomerOrder.customerPostalCode)
        hasError = true;

      if (hasError) {
        this.checkedOnce = true;
        this.isSavingDraft = false;
        this.isSavingOrder = false;
        if (this.topAlert)
          setTimeout(this.topAlert.nativeElement.scrollIntoView({ behavior: 'smooth', block: "center" }), 100);
        return;
      }
    }

    this.userCustomerOrder.customerIsIndividual = !this.isMoralPerson;
    this.userCustomerOrderService.saveOrder(this.userCustomerOrder).subscribe(response => {
      // If not logged, the creation of the order will create account and log user
      if (!this.currentUser)
        this.loginService.logUser(0, "");
      this.isSavingDraft = false;
      this.isSavingOrder = false;
      if (this.userCustomerOrder.isCustomerOrder)
        this.appService.openRoute(undefined, "account/orders/details/" + response.orderId, undefined);
      if (!this.userCustomerOrder.isCustomerOrder)
        this.appService.openRoute(undefined, "account/quotations/details/" + response.orderId, undefined);
    })
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


  searchSiret() {
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.effectiveSearchSiret();
    }, 500);
  }

  effectiveSearchSiret() {
    if (this.siretSearched && validateSiret(this.siretSearched)) {
      this.loadingCustomerSiret = true;
      this.affaireService.getAffaireBySiret(this.siretSearched).subscribe(response => {
        this.loadingCustomerSiret = false;
        if (response && response.length == 1) {
          let affaire = response[0];
          this.userCustomerOrder.customerSiret = affaire.siret;
          this.userCustomerOrder.customerCity = affaire.city;
          this.userCustomerOrder.customerDenomination = affaire.denomination;
          this.userCustomerOrder.customerPostalCode = affaire.postalCode;
          this.userCustomerOrder.customerAddress = affaire.address;
          this.userCustomerOrder.siret = affaire.siret;
          if (this.countries)
            for (let country of this.countries)
              if (country.id == affaire.country.id) {
                this.userCustomerOrder.customerCountry = country;
                break;
              }
        }
      })
    }
  }

  fetchCities() {
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.effectiveFetchCities();
    }, 500);
  }

  effectiveFetchCities() {
    this.foundCities = undefined;
    if (this.userCustomerOrder)
      if (this.userCustomerOrder.customerCountry && this.userCustomerOrder.customerCountry.id) {
        if (this.userCustomerOrder.customerCountry.id != this.countryFrance.id) {
          this.cityService.getCitiesByCountry(this.userCustomerOrder.customerCountry).subscribe(response => {
            this.foundCities = response;
          })
        } else if (this.userCustomerOrder.customerPostalCode && this.userCustomerOrder.customerPostalCode.length > 4) {
          this.cityService.getCitiesFilteredByCountryAndNameAndPostalCode(this.userCustomerOrder.customerCountry, this.userCustomerOrder.customerPostalCode).subscribe(response => {
            this.foundCities = response;
            if (this.foundCities && this.foundCities.length == 1)
              this.userCustomerOrder.customerCity = this.foundCities[0];
          })
        }
      }
  }

}
