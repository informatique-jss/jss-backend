import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbAccordionModule, NgbDropdownModule, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { validateEmail, validateFrenchPhone, validateInternationalPhone } from '../../../../libs/CustomFormsValidatorsHelper';
import { getDocument } from '../../../../libs/DocumentHelper';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { copyObject } from '../../../../libs/GenericHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { Mail } from '../../../general/model/Mail';
import { AppService } from '../../../main/services/app.service';
import { ConstantService } from '../../../main/services/constant.service';
import { AutocompleteCityComponent } from '../../../miscellaneous/components/forms/autocomplete-city/autocomplete-city.component';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { GenericTextareaComponent } from '../../../miscellaneous/components/forms/generic-textarea/generic-textarea.component';
import { GenericToggleComponent } from '../../../miscellaneous/components/forms/generic-toggle/generic-toggle.component';
import { SelectBillingLabelTypeComponent } from '../../../miscellaneous/components/forms/select-billing-label-type/select-billing-label-type.component';
import { SelectCivilityComponent } from '../../../miscellaneous/components/forms/select-civility/select-civility.component';
import { SelectCountryComponent } from '../../../miscellaneous/components/forms/select-country/select-country.component';
import { BillingLabelType } from '../../../my-account/model/BillingLabelType';
import { CustomerOrder } from '../../../my-account/model/CustomerOrder';
import { Document } from '../../../my-account/model/Document';
import { DocumentType } from '../../../my-account/model/DocumentType';
import { Quotation } from '../../../my-account/model/Quotation';
import { ServiceType } from '../../../my-account/model/ServiceType';
import { CustomerOrderService } from '../../../my-account/services/customer.order.service';
import { DocumentService } from '../../../my-account/services/document.service';
import { QuotationService } from '../../../my-account/services/quotation.service';
import { ServiceService } from '../../../my-account/services/service.service';
import { Phone } from '../../../profile/model/Phone';
import { Responsable } from '../../../profile/model/Responsable';
import { Tiers } from '../../../profile/model/Tiers';
import { LoginService } from '../../../profile/services/login.service';
import { UserScopeService } from '../../../profile/services/user.scope.service';
import { IQuotation } from '../../model/IQuotation';
import { CityService } from '../../services/city.service';

@Component({
  selector: 'checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS,
    GenericInputComponent,
    GenericToggleComponent,
    AutocompleteCityComponent,
    GenericTextareaComponent,
    SelectCountryComponent,
    SelectCivilityComponent,
    SelectBillingLabelTypeComponent,
    NgbDropdownModule,
    NgbNavModule,
    NgbAccordionModule
  ]
})
export class CheckoutComponent implements OnInit {

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

  isNotIndividualTiers: boolean = true;
  isComputingPrice: boolean = false;

  documentTypeBilling!: DocumentType;
  documentTypeDigital!: DocumentType;
  documentTypePaper!: DocumentType;
  billingLabelTypeOther!: BillingLabelType;
  isExtRefMandatory: boolean = false;

  documentForm!: FormGroup;

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

  mailToConfirm: string | undefined;
  userScope: Responsable[] | undefined;

  subscriptionType: string | undefined;
  isPriceReductionForSubscription: boolean = false;
  idArticle: number | undefined;


  capitalizeName = capitalizeName;

  serviceTypeAnnualSubscription!: ServiceType;
  serviceTypeEnterpriseAnnualSubscription!: ServiceType;
  serviceTypeMonthlySubscription!: ServiceType;
  serviceTypeUniqueArticleBuy!: ServiceType;
  serviceTypeKioskNewspaperBuy!: ServiceType;

  constructor(
    private loginService: LoginService,
    private quotationService: QuotationService,
    private orderService: CustomerOrderService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private constantService: ConstantService,
    private serviceService: ServiceService,
    private userScopeService: UserScopeService,
    private documentService: DocumentService,
    private cityService: CityService
  ) { }

  async ngOnInit() {
    this.serviceTypeAnnualSubscription = this.constantService.getServiceTypeAnnualSubscription();
    this.serviceTypeEnterpriseAnnualSubscription = this.constantService.getServiceTypeEnterpriseAnnualSubscription();
    this.serviceTypeMonthlySubscription = this.constantService.getServiceTypeMonthlySubscription();
    this.serviceTypeUniqueArticleBuy = this.constantService.getServiceTypeUniqueArticleBuy();
    this.serviceTypeKioskNewspaperBuy = this.constantService.getServiceTypeKioskNewspaperBuy();

    this.documentForm = this.formBuilder.group({});

    this.documentTypeBilling = this.constantService.getDocumentTypeBilling();
    this.documentTypeDigital = this.constantService.getDocumentTypeDigital();
    this.documentTypePaper = this.constantService.getDocumentTypePaper();
    this.billingLabelTypeOther = this.constantService.getBillingLabelTypeOther();

    await this.loginService.getCurrentUser().subscribe(response => {
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

    if (!this.currentUser)
      this.initIQuotation();

    this.userScopeService.getUserScope().subscribe(response => {
      this.userScope = [];
      if (response)
        for (let scope of response)
          this.userScope.push(scope.responsableViewed);
    })
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

  onValidateOrder(isDraft: boolean) {
    this.documentForm.markAllAsTouched();
    if (this.isOrderPossible())
      this.saveOrder(isDraft);
  }

  saveOrder(isDraft: boolean) {
    if (!this.quotation)
      return;

    this.appService.showLoadingSpinner();
    if (!this.currentUser) {
      if (this.quotation) {
        this.quotationService.setCurrentDraftQuotation(this.quotation);
        if (this.quotation.isQuotation)
          this.quotationService.saveFinalQuotation(this.quotation as Quotation, !isDraft).subscribe(response => {
            if (response && response.id) {
              this.cleanStorageData();
              this.appService.openRoute(undefined, "account/quotations/details/" + response.id, undefined);
            }
          });
        else
          this.orderService.saveFinalOrder(this.quotation as CustomerOrder, !isDraft).subscribe(response => {
            if (response && response.id) {
              this.cleanStorageData();
              this.appService.openRoute(undefined, "account/orders/details/" + response.id, undefined);
            }
          });
        this.appService.hideLoadingSpinner();
      }
    } else {
      if (this.quotation.isQuotation)
        this.quotationService.saveQuotation(this.quotation, !isDraft).subscribe(response => {
          if (response && response.id) {
            this.cleanStorageData();
            this.appService.hideLoadingSpinner();
            this.appService.openRoute(undefined, "account/quotations/details/" + response.id, undefined);
          }
        })
      else
        this.orderService.saveOrder(this.quotation, !isDraft).subscribe(response => {
          if (response && response.id) {
            this.cleanStorageData();
            this.appService.hideLoadingSpinner();
            this.appService.openRoute(undefined, "account/orders/details/" + response.id, undefined);
          }
        })
    }
  }

  cleanStorageData() {
    this.quotationService.cleanStorageData();
  }


  isOrderPossible() {
    console.log(this.documentForm);
    if (this.documentForm.invalid) {
      this.appService.displayToast("Il manque des informations obligatoires pour pouvoir valider " + (this.quotation!.isQuotation ? "le devis" : "la commande"), true, "Validation de commande impossible", 5000);
      return false;
    }
    if (!this.currentUser && this.quotation!.responsable!.mail.mail != this.mailToConfirm) {
      this.appService.displayToast("Les deux e-mails renseignés ne sont pas identiques !", true, "Validation de commande impossible", 5000);
      return false;
    }
    if (!this.acceptDocs || !this.acceptTerms) {
      this.appService.displayToast("Vous devez accepter les conditions ci-dessus pour pouvoir passer commande", true, "Validation de commande impossible", 5000);
      return false;
    }
    return true;
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
        this.completeBillingDocument();

      }
    }
  }

  completeBillingDocument() {
    if (this.quotation && this.quotation.documents) {
      let billingDocument = getDocument(this.constantService.getDocumentTypeBilling(), this.quotation);
      if (billingDocument && billingDocument.billingLabelType.id == this.constantService.getBillingLabelTypeOther().id)
        if (!billingDocument.billingLabelCountry)
          billingDocument.billingLabelCountry = this.constantService.getResponsableDummyCustomerFrance().tiers.country;
      if (!billingDocument.billingLabelCity)
        billingDocument.billingLabelCity = this.constantService.getResponsableDummyCustomerFrance().tiers.city;

    }
  }

  changeCurrentUser(user: Responsable) {
    this.currentUser = user;
    if (this.currentUser)
      this.documentService.getDocumentForResponsable(this.currentUser.id).subscribe(response => {
        if (this.quotation) {
          this.quotation.documents = [];
          this.quotation.responsable = user;
          for (let doc of response) {
            if (doc.documentType.id == this.constantService.getDocumentTypeBilling().id
              || doc.documentType.id == this.constantService.getDocumentTypeDigital().id
              || doc.documentType.id == this.constantService.getDocumentTypePaper().id
            )
              this.quotation.documents.push(doc);
          }
          this.sortDocuments(this.quotation.documents);
          if (!this.currentUser)
            this.quotationService.setCurrentDraftQuotation(this.quotation);
          else
            this.quotationService.saveQuotation(this.quotation, false).subscribe();
        }
      })
  }

  initEmptyDocuments() {
    if (this.quotation) {
      if (this.quotation.assoAffaireOrders && this.quotation.assoAffaireOrders[0].affaire) {
        if (!this.quotation.assoAffaireOrders[0].affaire.mails)
          this.quotation.assoAffaireOrders[0].affaire.mails = [];
        if (this.quotation.assoAffaireOrders[0].affaire.mails.length == 0)
          this.quotation.assoAffaireOrders[0].affaire.mails.push({} as Mail);
      }

      if (!this.quotation.responsable)
        return;

      if (!this.quotation.documents || this.quotation.documents.length == 0)
        this.quotation.documents = [];
      let responsableForDocument = this.quotation.responsable && this.quotation.responsable.id ? this.quotation.responsable : this.constantService.getResponsableDummyCustomerFrance();

      if (responsableForDocument && responsableForDocument.tiers && (!this.quotation.documents || this.quotation.documents.length == 0)) {
        let billingDocument = copyObject(getDocument(this.constantService.getDocumentTypeBilling(), responsableForDocument));
        if (!billingDocument.billingLabelCountry)
          billingDocument.billingLabelCountry = this.constantService.getCountryFrance();
        if (!billingDocument.billingLabelCity)
          billingDocument.billingLabelCity = this.constantService.getResponsableDummyCustomerFrance().tiers.city;
        this.quotation.documents.push(billingDocument)
        this.quotation.documents.push(copyObject(getDocument(this.constantService.getDocumentTypeDigital(), responsableForDocument)))
        this.quotation.documents.push(copyObject(getDocument(this.constantService.getDocumentTypePaper(), responsableForDocument)))

        this.sortDocuments(this.quotation.documents);
      }
    }
  }

  sortDocuments(documents: Document[]) {
    const defaultOrder = [this.constantService.getDocumentTypeBilling().code, this.constantService.getDocumentTypeDigital().code, this.constantService.getDocumentTypePaper().code];

    documents.sort((a, b) => {
      return defaultOrder.indexOf(a.documentType.code) - defaultOrder.indexOf(b.documentType.code);
    });
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
              this.quotation!.assoAffaireOrders = response.assoAffaireOrders;
              this.finalizePricingAnswer();
            });
          } else if (this.orderService.getCurrentDraftOrderId()) {
            this.orderService.getCustomerOrder(parseInt(this.orderService.getCurrentDraftOrderId()!)).subscribe(response => {
              this.quotation!.assoAffaireOrders = response.assoAffaireOrders;
              this.finalizePricingAnswer();
            });
          }
        } else {
          this.finalizePricingAnswer();
        }
      } else {
        if (this.quotation.assoAffaireOrders && this.quotation.assoAffaireOrders[0].services && this.quotation.assoAffaireOrders[0].services[0].provisions)
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
      this.isComputingPrice = true;
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
    if (this.quotation && document.documentType.code == this.documentTypeBilling.code) {
      this.completeBillingDocument();
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
        this.serviceService.deleteService(this.quotation.assoAffaireOrders[assoIndex].services[serviceIndex]).subscribe(response => {
          this.prepareForPricingAndCompute();
        })
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
    if (this.quotation && !this.quotation.id) {
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

  deleteMailTiers(mail: Mail, tiers: Tiers) {
    if (tiers)
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
      this.quotation.responsable.tiers.isIndividual = !this.isNotIndividualTiers;
  }

  findCityForResponsable() {
    if (this.quotation && this.quotation.responsable && this.quotation.responsable.postalCode) {
      this.cityService.getCitiesByPostalCode(this.quotation.responsable.postalCode).subscribe(response => {
        if (response && response.length == 1)
          this.quotation!.responsable!.city = response[0];
      })
    }
  }

  findCityForTiers() {
    if (this.quotation && this.quotation.responsable && this.quotation.responsable.tiers && this.quotation.responsable.tiers.postalCode) {
      this.cityService.getCitiesByPostalCode(this.quotation.responsable.tiers.postalCode).subscribe(response => {
        if (response && response.length == 1)
          this.quotation!.responsable!.tiers.city = response[0];
      })
    }
  }

  findCityForDocument(document: Document) {
    if (document && document.billingPostalCode) {
      this.cityService.getCitiesByPostalCode(document.billingPostalCode).subscribe(response => {
        if (response && response.length == 1)
          document.billingLabelCity = response[0];
      })
    }
  }

  isServiceJssSubscription(serviceType: ServiceType): boolean {
    return serviceType.id === this.serviceTypeAnnualSubscription.id
      || serviceType.id === this.serviceTypeMonthlySubscription.id
      || serviceType.id === this.serviceTypeUniqueArticleBuy.id
      //TODO : create constant
      // || serviceType.id === this.serviceTypeEnterpriseAnnualSubscription.id
      || serviceType.id === this.serviceTypeUniqueArticleBuy.id;
  }

  goBackQuotation() {
    this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[2]);
    this.appService.openRoute(undefined, "quotation/required-information", undefined);
  }
}
