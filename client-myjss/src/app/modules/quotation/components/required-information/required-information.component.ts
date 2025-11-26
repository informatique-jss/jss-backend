import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ChangeEvent, CKEditorModule } from '@ckeditor/ckeditor5-angular';
import { NgbModal, NgbNavChangeEvent, NgbNavModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Alignment, Bold, ClassicEditor, Essentials, Font, GeneralHtmlSupport, Indent, IndentBlock, Italic, Link, List, Mention, Paragraph, PasteFromOffice, RemoveFormat, Underline, Undo } from 'ckeditor5';
import { combineLatest, Observable, of, Subscription } from 'rxjs';
import { PROVISION_SCREEN_TYPE_ANNOUNCEMENT, PROVISION_SCREEN_TYPE_DOMICILIATION, SERVICE_FIELD_TYPE_DATE, SERVICE_FIELD_TYPE_INTEGER, SERVICE_FIELD_TYPE_SELECT, SERVICE_FIELD_TYPE_TEXT, SERVICE_FIELD_TYPE_TEXTAREA } from '../../../../libs/Constants';
import { validateEmail, validateFrenchPhone, validateInternationalPhone } from '../../../../libs/CustomFormsValidatorsHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { Mail } from '../../../general/model/Mail';
import { AppService } from '../../../main/services/app.service';
import { ConstantService } from '../../../main/services/constant.service';
import { GtmService } from '../../../main/services/gtm.service';
import { FileUploadPayload, PageInfo } from '../../../main/services/GtmPayload';
import { AutocompleteCityComponent } from '../../../miscellaneous/components/forms/autocomplete-city/autocomplete-city.component';
import { AutocompleteLegalFormComponent } from '../../../miscellaneous/components/forms/autocomplete-legal-form/autocomplete-legal-form.component';
import { GenericDatePickerComponent } from '../../../miscellaneous/components/forms/generic-date-picker/generic-datetime-picker.component';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { GenericTextareaComponent } from '../../../miscellaneous/components/forms/generic-textarea/generic-textarea.component';
import { GenericToggleComponent } from '../../../miscellaneous/components/forms/generic-toggle/generic-toggle.component';
import { SelectBuildingDomiciliationComponent } from '../../../miscellaneous/components/forms/select-building-domiciliation/select-building-domiciliation.component';
import { SelectCivilityComponent } from '../../../miscellaneous/components/forms/select-civility/select-civility.component';
import { SelectContractTypeComponent } from '../../../miscellaneous/components/forms/select-contract-type/select-contract-type.component';
import { SelectCountryComponent } from '../../../miscellaneous/components/forms/select-country/select-country.component';
import { SelectDepartmentComponent } from '../../../miscellaneous/components/forms/select-department/select-department.component';
import { SelectLanguageComponent } from '../../../miscellaneous/components/forms/select-language/select-language.component';
import { SelectMailRedirectionComponent } from '../../../miscellaneous/components/forms/select-mail-redirection/select-mail-redirection.component';
import { SelectMultipleNoticeTypeComponent } from '../../../miscellaneous/components/forms/select-multiple-notice-type/select-multiple-notice-type.component';
import { SelectNoticeTypeFamilyComponent } from '../../../miscellaneous/components/forms/select-notice-type-family/select-notice-type-family.component';
import { SelectStringComponent } from '../../../miscellaneous/components/forms/select-string/select-string.component';
import { SelectValueServiceFieldTypeComponent } from '../../../miscellaneous/components/forms/select-value-service-field-type/select-value-service-field-type.component';
import { Affaire } from '../../../my-account/model/Affaire';
import { Announcement } from '../../../my-account/model/Announcement';
import { AssoServiceDocument } from '../../../my-account/model/AssoServiceDocument';
import { AssoServiceFieldType } from '../../../my-account/model/AssoServiceFieldType';
import { Provision } from '../../../my-account/model/Provision';
import { ProvisionType } from '../../../my-account/model/ProvisionType';
import { Service } from '../../../my-account/model/Service';
import { ServiceType } from '../../../my-account/model/ServiceType';
import { AssoServiceDocumentService } from '../../../my-account/services/asso.service.document.service';
import { CustomerOrderService } from '../../../my-account/services/customer.order.service';
import { QuotationService } from '../../../my-account/services/quotation.service';
import { ServiceService } from '../../../my-account/services/service.service';
import { Civility } from '../../../profile/model/Civility';
import { Department } from '../../../profile/model/Department';
import { Phone } from '../../../profile/model/Phone';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { AnnouncementNoticeTemplate } from '../../model/AnnouncementNoticeTemplate';
import { Domiciliation } from '../../model/Domiciliation';
import { DomiciliationContractType } from '../../model/DomiciliationContractType';
import { IQuotation } from '../../model/IQuotation';
import { MailRedirectionType } from '../../model/MailRedirectionType';
import { NoticeTemplateDescription } from '../../model/NoticeTemplateDescription';
import { NoticeType } from '../../model/NoticeType';
import { NoticeTypeFamily } from '../../model/NoticeTypeFamily';
import { ServiceFamily } from '../../model/ServiceFamily';
import { CityService } from '../../services/city.service';
import { CivilityService } from '../../services/civility.service';
import { DepartmentService } from '../../services/department.service';
import { NoticeTemplateService } from '../../services/notice.template.service';
import { NoticeTypeFamilyService } from '../../services/notice.type.family.service';
import { NoticeTypeService } from '../../services/notice.type.service';
import { QuotationFileUploaderComponent } from '../quotation-file-uploader/quotation-file-uploader.component';

@Component({
  selector: 'required-information',
  templateUrl: './required-information.component.html',
  styleUrls: ['./required-information.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS,
    AutocompleteCityComponent,
    AutocompleteLegalFormComponent,
    GenericInputComponent,
    GenericTextareaComponent,
    GenericDatePickerComponent,
    GenericToggleComponent,
    QuotationFileUploaderComponent,
    SelectStringComponent,
    SelectValueServiceFieldTypeComponent,
    SelectDepartmentComponent,
    SelectNoticeTypeFamilyComponent,
    SelectMultipleNoticeTypeComponent,
    SelectContractTypeComponent,
    SelectLanguageComponent,
    SelectMailRedirectionComponent,
    SelectBuildingDomiciliationComponent,
    SelectCountryComponent,
    SelectCivilityComponent,
    CKEditorModule,
    NgbNavModule,
    NgbTooltipModule]
})
export class RequiredInformationComponent implements OnInit {

  @ViewChild('confirmBackModal') confirmBackModal!: TemplateRef<any>;

  CONFIER_ANNONCE_AU_JSS: string = "Confier l'annonce légale au JSS";

  selectedAssoIndex: number | null = 0;
  selectedServiceIndex: number | null = 0;
  selectedService: Service | null = null;
  currentUser: Responsable | undefined;
  quotation: IQuotation | undefined;

  affaire: Affaire = { isIndividual: false } as Affaire;

  noticeTypes: NoticeType[] | undefined;
  noticeTypeFamilies: NoticeTypeFamily[] | undefined;
  departments: Department[] | undefined;
  civilities: Civility[] | undefined;

  minDatePublication: Date = new Date();

  isDoNotGenerateAnnouncement: boolean = true;
  selectionRedaction: string[] = [this.CONFIER_ANNONCE_AU_JSS, "Je m'occupe de la publication de l'annonce légale"];
  selectedRedaction: string[][] = [];

  checkedOnce = false;
  isBrowser = false;

  activeId = 4;
  isOnlyAnnouncement = true;

  SERVICE_FIELD_TYPE_TEXT = SERVICE_FIELD_TYPE_TEXT;
  SERVICE_FIELD_TYPE_INTEGER = SERVICE_FIELD_TYPE_INTEGER;
  SERVICE_FIELD_TYPE_DATE = SERVICE_FIELD_TYPE_DATE;
  SERVICE_FIELD_TYPE_TEXTAREA = SERVICE_FIELD_TYPE_TEXTAREA;
  SERVICE_FIELD_TYPE_SELECT = SERVICE_FIELD_TYPE_SELECT;
  PROVISION_SCREEN_TYPE_DOMICILIATION = PROVISION_SCREEN_TYPE_DOMICILIATION;
  PROVISION_SCREEN_TYPE_ANNOUNCEMENT = PROVISION_SCREEN_TYPE_ANNOUNCEMENT;

  provisionTypeRbe!: ProvisionType;

  mailRedirectionTypeOther!: MailRedirectionType;
  domiciliationContractTypeRouteEmailAndMail!: DomiciliationContractType
  domiciliationContractTypeRouteMail!: DomiciliationContractType

  newMailDomiciliation: string = "";
  newMailLegalGardian: string = "";
  newPhoneLegalGardian: string = "";

  serviceFamilies: ServiceFamily[] = [];

  ckEditorHeader = ClassicEditor;

  noticeTemplateDescriptionSubscription: Subscription = new Subscription;
  noticeTemplateDescription: NoticeTemplateDescription | undefined;

  goBackModalInstance: any | undefined;

  currentTab: string = 'documents';

  constructor(
    private formBuilder: FormBuilder,
    private appService: AppService,
    private loginService: LoginService,
    private quotationService: QuotationService,
    private assoServiceDocumentService: AssoServiceDocumentService,
    private orderService: CustomerOrderService,
    private serviceService: ServiceService,
    private noticeTypeFamilyService: NoticeTypeFamilyService,
    private noticeTypeService: NoticeTypeService,
    private departmentService: DepartmentService,
    private civilityService: CivilityService,
    private constantService: ConstantService,
    private cityService: CityService,
    private noticeTemplateService: NoticeTemplateService,
    private modalService: NgbModal,
    private gtmService: GtmService
  ) {
  }

  informationForm!: FormGroup;

  parseInt = parseInt;

  async ngOnInit() {
    this.provisionTypeRbe = this.constantService.getProvisionTypeRbe();

    this.noticeTemplateDescriptionSubscription = this.noticeTemplateService.noticeTemplateDescriptionObservable.subscribe(item => {
      if (item && this.quotation && this.selectedAssoIndex != undefined && this.selectedServiceIndex != undefined && item.announcementOrder != undefined
        && this.quotation.assoAffaireOrders[this.selectedAssoIndex].services[this.selectedServiceIndex]
        && this.quotation.assoAffaireOrders[this.selectedAssoIndex].services[this.selectedServiceIndex].provisions[item.announcementOrder].announcement) {
        this.quotation.assoAffaireOrders[this.selectedAssoIndex].services[this.selectedServiceIndex].provisions[item.announcementOrder].announcement!.notice = item.displayText;
        this.noticeTemplateDescription = item;
      }
    });
    this.mailRedirectionTypeOther = this.constantService.getMailRedirectionTypeOther();
    this.domiciliationContractTypeRouteEmailAndMail = this.constantService.getDomiciliationContractTypeRouteEmailAndMail();
    this.domiciliationContractTypeRouteMail = this.constantService.getDomiciliationContractTypeRouteMail();

    this.informationForm = this.formBuilder.group({});

    await this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
      this.initIQuotation();
    })

    if (!this.currentUser)
      this.initIQuotation();
    this.fetchAnnouncementReferentials();
    this.noticeTemplateDescription = this.noticeTemplateService.getNoticeTemplateDescription();
    if (!this.noticeTemplateDescription) {
      this.noticeTemplateDescription = { service: undefined, isShowNoticeTemplate: false, displayText: "", isUsingTemplate: false, assoAffaireOrder: undefined } as any as NoticeTemplateDescription;
      this.setAssoAffaireOrderToNoticeTemplateDescription();
    }
  }

  initIQuotation() {
    if (this.currentUser) {
      if (this.quotationService.getCurrentDraftQuotationId()) {
        this.quotationService.getQuotation(parseInt(this.quotationService.getCurrentDraftQuotationId()!)).subscribe(response => {
          this.quotation = response;
          this.initIndexesAndServiceType();
        });
      } else if (this.orderService.getCurrentDraftOrderId()) {
        this.orderService.getCustomerOrder(parseInt(this.orderService.getCurrentDraftOrderId()!)).subscribe(response => {
          this.quotation = response;
          this.initIndexesAndServiceType();
        });
      }
    } else {
      if (this.quotationService.getCurrentDraftQuotation()) {
        this.quotation = this.quotationService.getCurrentDraftQuotation()!;
        this.initIndexesAndServiceType();
      } else if (this.orderService.getCurrentDraftOrder()) {
        this.quotation = this.orderService.getCurrentDraftOrder()!;
        this.initIndexesAndServiceType();
      }
    }
  }


  initIndexesAndServiceType() {
    if (this.quotation && this.quotation.assoAffaireOrders && this.quotation.assoAffaireOrders.length > 0) {
      this.selectedAssoIndex = 0;
      // Init order of provisions for multiple announcements in front-end and annouvement and domiciliation
      for (let asso of this.quotation.assoAffaireOrders) {
        if (asso.services && asso.services.length > 0) {
          for (let serv of asso.services) {
            if (serv.provisions && serv.provisions.length > 0) {
              let i = 0;
              let index = 0;
              for (let provision of serv.provisions) {
                if (provision.provisionType.provisionScreenType.code == PROVISION_SCREEN_TYPE_ANNOUNCEMENT) {
                  this.activeId = parseInt('1' + index);
                  provision.order = ++i;
                  if (!this.selectedRedaction[asso.services.indexOf(serv)]) {
                    this.selectedRedaction[asso.services.indexOf(serv)] = [];
                  }
                  this.selectedRedaction[asso.services.indexOf(serv)][serv.provisions.indexOf(provision)] = this.CONFIER_ANNONCE_AU_JSS;
                  if (!provision.announcement) {
                    provision.announcement = {} as Announcement;
                    provision.isRedactedByJss = true;
                  }
                } else {
                  this.isOnlyAnnouncement = false;
                }
                if (provision.provisionType.provisionScreenType.code == PROVISION_SCREEN_TYPE_DOMICILIATION) {
                  if (!provision.domiciliation) {
                    this.activeId = 2;
                    provision.domiciliation = {} as Domiciliation;
                  }
                }
                index++;
              }
            }

            if (serv.assoServiceDocuments) {
              serv.assoServiceDocuments.sort((a, b) => (b.isMandatory ? 1 : 0) - (a.isMandatory ? 1 : 0))
            }

            if (serv.assoServiceFieldTypes) {
              serv.assoServiceFieldTypes.sort((a, b) => (b.isMandatory ? 1 : 0) - (a.isMandatory ? 1 : 0))
            }
          }
        }
      }

      this.setAssoAffaireOrderToNoticeTemplateDescription();
      this.changeProvisionNoticeTemplateDescription({ nextId: this.activeId } as NgbNavChangeEvent);

      if (this.quotation.assoAffaireOrders[this.selectedAssoIndex].services && this.quotation.assoAffaireOrders[this.selectedAssoIndex].services.length > 0) {
        this.selectedServiceIndex = 0;
      }
      this.emitServiceChange();
    }
  }

  fetchAnnouncementReferentials() {
    if (!this.civilities)
      this.civilityService.getCivilities().subscribe(reponse => this.civilities = reponse);
    if (!this.noticeTypeFamilies)
      this.noticeTypeFamilyService.getNoticeTypeFamilies().subscribe(response => this.noticeTypeFamilies = response.sort((a: NoticeTypeFamily, b: NoticeTypeFamily) => a.label.localeCompare(b.label)));
    if (!this.noticeTypes)
      this.noticeTypeService.getNoticeTypes().subscribe(response => this.noticeTypes = response.sort((a: NoticeType, b: NoticeType) => a.label.localeCompare(b.label)));
    if (!this.departments)
      this.departmentService.getDepartments().subscribe(response => {
        this.departments = response.sort((a: Department, b: Department) => a.code.localeCompare(b.code));
      });
  }

  // Sets AssoAffaireOrder on NoticeTemplateDescription and push the update to the service
  setAssoAffaireOrderToNoticeTemplateDescription() {
    if (!this.noticeTemplateDescription)
      this.noticeTemplateDescription = this.noticeTemplateService.getNoticeTemplateDescription();

    if (this.quotation && this.quotation.assoAffaireOrders && this.selectedAssoIndex != undefined && this.quotation.assoAffaireOrders[this.selectedAssoIndex]) {
      this.noticeTemplateDescription!.assoAffaireOrder = this.quotation.assoAffaireOrders[this.selectedAssoIndex];
    }
    this.noticeTemplateService.changeNoticeTemplateDescription(this.noticeTemplateDescription!);
  }

  selectCard(assoIndex: number, event: Event): void {
    // Do not propagate clic if it is on pill
    if ((event.target as HTMLElement).closest('.pill')) {
      return;
    }
    this.moveToService(0, assoIndex);
  }

  config = {
    toolbar: ['undo', 'redo', '|', 'fontFamily', 'fontSize', 'bold', 'italic', 'underline', 'fontColor', 'fontBackgroundColor', '|',
      'alignment:left', 'alignment:right', 'alignment:center', 'alignment:justify', '|', 'link', 'bulletedList', 'numberedList', 'outdent', 'indent', 'removeformat'
    ],
    plugins: [
      Bold, Essentials, Italic, Mention, Paragraph, Undo, Font, Alignment, Link, List, Indent, IndentBlock, RemoveFormat, GeneralHtmlSupport, Underline, PasteFromOffice],
    htmlSupport: {
      allow: [
        {
          name: /.*/,
          attributes: true,
          classes: true,
          styles: true
        }
      ]
    },
    language: {
      ui: 'fr',
      content: 'fr'
    }
  } as any;


  onEditorReady(editor: any, provision: Provision) {
    if (!provision || !provision.announcement)
      return;

    const initialValue = provision.announcement.notice?.length > 0 ? provision.announcement.notice : "";

    editor.setData(initialValue);
  }

  onNoticeChange(event: ChangeEvent, provision: Provision) {
    if (provision && provision.announcement)
      provision.announcement.notice = event.editor.getData();
  }

  onIsCompleteChange(event: boolean, selectedAssoIndex: number, selectedServiceIndex: number, assoServiceDocumentIndex: number, assoServiceDocument: AssoServiceDocument) {
    if (event) {
      this.trackUploadFile(assoServiceDocument);
      this.refreshAssoServiceDocument(selectedAssoIndex, selectedServiceIndex, assoServiceDocumentIndex, assoServiceDocument);
    }
  }

  trackUploadFile(assoServiceDocument: AssoServiceDocument) {
    if (this.quotation)
      this.gtmService.trackFileUpload(
        {
          business: {
            type: this.quotation.isQuotation ? 'quotation' : 'order',
            order_id: this.quotation.id, documentType: assoServiceDocument.typeDocument.label
          },
          page: {
            type: 'quotation',
            name: 'required-information'
          } as PageInfo
        } as FileUploadPayload
      );
  }

  onIsUploadDelete(event: boolean, selectedAssoIndex: number, selectedServiceIndex: number, assoServiceDocumentIndex: number, assoServiceDocument: AssoServiceDocument) {
    if (event) {
      this.refreshAssoServiceDocument(selectedAssoIndex, selectedServiceIndex, assoServiceDocumentIndex, assoServiceDocument);
    }
  }

  refreshAssoServiceDocument(selectedAssoIndex: number, selectedServiceIndex: number, assoServiceDocumentIndex: number, assoServiceDocument: AssoServiceDocument) {
    this.assoServiceDocumentService.getAssoServiceDocument(assoServiceDocument).subscribe(response => {
      if (this.quotation && response) {
        this.quotation.assoAffaireOrders[selectedAssoIndex].services[selectedServiceIndex].assoServiceDocuments[assoServiceDocumentIndex] = response;
      }
    });
  }

  canSaveQuotation() {
    if (this.quotation && this.quotation.assoAffaireOrders)
      for (let asso of this.quotation.assoAffaireOrders)
        if (!asso.services || asso.services.length == 0)
          return false;
    return true;
  }

  saveFieldsValue() {
    if (this.quotation && this.selectedAssoIndex != undefined && this.selectedServiceIndex != undefined) {
      if (this.informationForm) {
        this.informationForm.markAllAsTouched();
        if (!this.informationForm.valid) {
          this.appService.displayToast("Veuillez remplir les champs obligatoires", true, "Champs obligatoires", 5000);
          return of(false);
        }

        if (this.quotation.assoAffaireOrders[this.selectedAssoIndex].services[this.selectedServiceIndex])
          for (let provision of this.quotation.assoAffaireOrders[this.selectedAssoIndex].services[this.selectedServiceIndex].provisions)
            if (provision && provision.announcement && !provision.isRedactedByJss && (!this.noticeTemplateDescription || !this.noticeTemplateDescription.isUsingTemplate) && (!provision.announcement || !provision.announcement.notice || provision.announcement.notice.length == 0)) {
              this.appService.displayToast("Veuillez remplir le texte de l'annonce légale", true, "Champs obligatoires", 5000);
              return of(false);
            }
      }

      if (this.noticeTemplateDescription && this.noticeTemplateDescription.announcementOrder && this.quotation.assoAffaireOrders[this.selectedAssoIndex].services[this.selectedServiceIndex] && this.quotation.assoAffaireOrders[this.selectedAssoIndex].services[this.selectedServiceIndex].provisions[this.noticeTemplateDescription.announcementOrder])
        this.quotation.assoAffaireOrders[this.selectedAssoIndex].services[this.selectedServiceIndex].provisions[this.noticeTemplateDescription.announcementOrder].announcement!.notice = this.noticeTemplateDescription.displayText;

      if (this.currentUser) {
        this.appService.showLoadingSpinner();
        return this.serviceService.addOrUpdateService(this.quotation.assoAffaireOrders[this.selectedAssoIndex].services[this.selectedServiceIndex]) as any as Observable<boolean>;
      } else {
        if (this.quotation.isQuotation) {
          this.quotationService.setCurrentDraftQuotation(this.quotation);
          return of(true);
        } else {
          this.orderService.setCurrentDraftOrder(this.quotation);
          return of(true);
        }
      }
    }
    return of(true);
  }

  // TODO : connet to back to save the list of modifiedBeneficialOwners when Pierre has finished the back end
  moveToService(newServiceIndex: number, newAssoIndex: number) {
    if (!this.quotation)
      return;

    // move backward
    if (newServiceIndex < 0) {
      newAssoIndex = newAssoIndex - 1;
    }

    if (newAssoIndex < 0) {
      this.goBackQuotationModale(this.confirmBackModal);
      return;
    }

    // move forward
    if (newServiceIndex >= this.quotation.assoAffaireOrders[newAssoIndex].services.length) {
      newAssoIndex = newAssoIndex + 1;
      newServiceIndex = 0;
    }

    this.emitServiceChange();

    if (newAssoIndex >= this.quotation.assoAffaireOrders.length) {
      this.saveFieldsValue().subscribe(response => {
        this.appService.hideLoadingSpinner();
        if (!response)
          return;
        this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[3]);
        if (this.noticeTemplateDescription) {
          this.noticeTemplateDescription.isShowNoticeTemplate = false;
          this.noticeTemplateDescription.assoAffaireOrder = this.quotation!.assoAffaireOrders[newAssoIndex];
          this.noticeTemplateService.changeNoticeTemplateDescription(this.noticeTemplateDescription);
        }
        this.appService.openRoute(undefined, "quotation/checkout", undefined);
      });
      return;
    }

    this.saveFieldsValue().subscribe(response => {
      this.appService.hideLoadingSpinner();
      if (!response)
        return;
    });
    this.selectedAssoIndex = null;
    this.selectedServiceIndex = null;

    setTimeout(() => {
      this.selectedAssoIndex = newAssoIndex;
      this.selectedServiceIndex = newServiceIndex;
    }, 0);
  }

  goBackQuotationModale(content: TemplateRef<any>) {
    if (this.goBackModalInstance) {
      return;
    }

    this.goBackModalInstance = this.modalService.open(content, {
    });

    this.goBackModalInstance.result.finally(() => {
      this.goBackModalInstance = undefined;
    });
  }

  confirmGoBack() {
    if (this.currentUser) {
      this.appService.showLoadingSpinner();
      let promises = [];
      if (this.quotation && this.quotation.assoAffaireOrders)
        for (let asso of this.quotation.assoAffaireOrders)
          if (asso.services)
            for (let service of asso.services)
              promises.push(this.serviceService.deleteService(service));
      combineLatest(promises).subscribe(response => {
        this.appService.hideLoadingSpinner();
        this.noticeTemplateService.clearNoticeTemplateDescription();
        this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[1]);
        this.appService.openRoute(undefined, "quotation/services-selection", undefined);
      })
    } else {
      if (this.quotation && this.quotation.assoAffaireOrders)
        for (let asso of this.quotation.assoAffaireOrders)
          asso.services = [];
      this.saveFieldsValue().subscribe(response => {
        this.appService.hideLoadingSpinner();
      });
      this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[1]);
      this.appService.openRoute(undefined, "quotation/services-selection", undefined);
    }

  }

  changeDoNotGenerateAnnouncement(selection: string, provision: Provision) {
    provision.isDoNotGenerateAnnouncement = true;
    if (selection != this.CONFIER_ANNONCE_AU_JSS) {
      this.isDoNotGenerateAnnouncement = false;
      provision.isDoNotGenerateAnnouncement = false;
    } else {
      this.isDoNotGenerateAnnouncement = true;
      provision.isDoNotGenerateAnnouncement = true;
    }
  }

  // ------------ Domiciliation form methods ---------------------
  mustDecribeAdress(domiciliation: Domiciliation): boolean {
    return domiciliation != null && domiciliation.domiciliationContractType &&
      (domiciliation.domiciliationContractType.id == this.domiciliationContractTypeRouteEmailAndMail.id
        || domiciliation.domiciliationContractType.id == this.domiciliationContractTypeRouteMail.id)
      && domiciliation.mailRedirectionType && domiciliation.mailRedirectionType.id == this.mailRedirectionTypeOther.id;
  }

  findCityForDomiciliation(domiciliation: Domiciliation) {
    if (domiciliation && domiciliation.postalCode) {
      this.cityService.getCitiesByPostalCode(domiciliation.postalCode).subscribe(response => {
        if (response && response.length == 1)
          domiciliation.city = response[0];
      })
    }
  }

  findCityForActivityDomiciliation(domiciliation: Domiciliation) {
    if (domiciliation && domiciliation.activityPostalCode) {
      this.cityService.getCitiesByPostalCode(domiciliation.activityPostalCode).subscribe(response => {
        if (response && response.length == 1)
          domiciliation.activityCity = response[0];
      })
    }
  }

  findCityForLegalGardianDomiciliation(domiciliation: Domiciliation) {
    if (domiciliation && domiciliation.legalGardianPostalCode) {
      this.cityService.getCitiesByPostalCode(domiciliation.legalGardianPostalCode).subscribe(response => {
        if (response && response.length == 1)
          domiciliation.legalGardianCity = response[0];
      })
    }
  }

  findCityForAccountingDomiciliation(domiciliation: Domiciliation) {
    if (domiciliation && domiciliation.accountingDocumentsConservationPostalCode) {
      this.cityService.getCitiesByPostalCode(domiciliation.accountingDocumentsConservationPostalCode).subscribe(response => {
        if (response && response.length == 1)
          domiciliation.accountingDocumentsConservationCity = response[0];
      })
    }
  }

  addLegalGardianMail(domiciliation: Domiciliation) {
    if (domiciliation)
      if (this.newMailLegalGardian && (validateEmail(this.newMailLegalGardian))) {
        let mail = {} as Mail;
        mail.mail = this.newMailLegalGardian;
        if (!domiciliation.mails)
          domiciliation.legalGardianMails = [];
        domiciliation.legalGardianMails.push(mail);
        this.newMailLegalGardian = "";
      }
  }

  deleteLegalGardianMail(mail: Mail, domiciliation: Domiciliation) {
    if (domiciliation)
      domiciliation.legalGardianMails.splice(domiciliation.legalGardianMails.indexOf(mail), 1);
  }

  addMailDomiciliation(domiciliation: Domiciliation) {
    if (domiciliation)
      if (this.newMailDomiciliation && (validateEmail(this.newMailDomiciliation))) {
        let mail = {} as Mail;
        mail.mail = this.newMailDomiciliation;
        if (!domiciliation.mails)
          domiciliation.mails = [];
        domiciliation.mails.push(mail);
        this.newMailDomiciliation = "";
      }
  }

  deleteMailDomiciliation(mail: Mail, domiciliation: Domiciliation) {
    if (domiciliation)
      domiciliation.mails.splice(domiciliation.mails.indexOf(mail), 1);
  }

  mustDecribeAdresse(domiciliation: Domiciliation): boolean {
    return domiciliation != null && domiciliation.domiciliationContractType &&
      (domiciliation.domiciliationContractType.id == this.domiciliationContractTypeRouteEmailAndMail.id
        || domiciliation.domiciliationContractType.id == this.domiciliationContractTypeRouteMail.id)
      && domiciliation.mailRedirectionType && domiciliation.mailRedirectionType.id == this.mailRedirectionTypeOther.id;
  }

  getCurrentDate(): Date {
    return new Date();
  }

  addDomiciliationPhone(domiciliation: Domiciliation) {
    if (domiciliation)
      if (this.newPhoneLegalGardian && (validateFrenchPhone(this.newPhoneLegalGardian) || validateInternationalPhone(this.newPhoneLegalGardian))) {
        let phone = {} as Phone;
        phone.phoneNumber = this.newPhoneLegalGardian;
        if (!domiciliation.legalGardianPhones)
          domiciliation.legalGardianPhones = [];
        domiciliation.legalGardianPhones.push(phone);
        this.newPhoneLegalGardian = "";
      }
  }

  deletePhoneForDomiciliation(phone: Phone, domiciliation: Domiciliation) {
    if (domiciliation)
      domiciliation.legalGardianPhones.splice(domiciliation.legalGardianPhones.indexOf(phone), 1);
  }

  hasMandatoryDocuments(assos: AssoServiceDocument[]) {
    if (assos)
      for (let asso of assos)
        if (asso.isMandatory)
          return true;
    return false;
  }

  hasNonMandatoryDocuments(assos: AssoServiceDocument[]) {
    if (assos)
      for (let asso of assos)
        if (!asso.isMandatory)
          return true;
    return false;
  }

  private emitServiceChange() {
    if (this.quotation && this.selectedAssoIndex != undefined && this.selectedServiceIndex != undefined && this.noticeTemplateDescription) {
      this.noticeTemplateDescription.service = this.quotation.assoAffaireOrders[this.selectedAssoIndex].services[this.selectedServiceIndex];
      this.setAssoAffaireOrderToNoticeTemplateDescription();
    }
  }

  changeIsShowNoticeTemplate(event: Boolean, isRedactedByJss: boolean, selectedTemplate: AnnouncementNoticeTemplate | undefined, service: Service | undefined) {
    if (this.noticeTemplateDescription) {
      if (isRedactedByJss) {
        this.noticeTemplateDescription.isShowNoticeTemplate = false;
        this.noticeTemplateService.changeNoticeTemplateDescription(this.noticeTemplateDescription);
        return;
      }

      if (service) {
        this.noticeTemplateService.changeNoticeTemplateDescription(this.noticeTemplateDescription);
        setTimeout(() => {
          this.updateTemplate(event as boolean, selectedTemplate, service);
          this.scrollToNoticeTemplateSection();
        }, 0);
      } else {
        this.noticeTemplateDescription.isShowNoticeTemplate = false;
        this.noticeTemplateService.changeNoticeTemplateDescription(this.noticeTemplateDescription);
      }

    }
  }

  updateTemplate(isShowNoticeTemplate: boolean, selectedTemplate: AnnouncementNoticeTemplate | undefined, service: Service | undefined) {
    if (service && this.noticeTemplateDescription) {
      if (selectedTemplate || (isShowNoticeTemplate && this.getPossibleTemplates(service) && this.getPossibleTemplates(service)!.length > 0)) {
        this.noticeTemplateDescription.selectedTemplate = undefined;
        this.noticeTemplateService.changeNoticeTemplateDescription(this.noticeTemplateDescription);
        setTimeout(() => {
          if (this.getPossibleTemplates(service) && this.getPossibleTemplates(service)!.length == 1) {
            this.noticeTemplateDescription!.selectedTemplate = this.getPossibleTemplates(service)![0];
            this.noticeTemplateDescription!.service = service;
          } else if (selectedTemplate) {
            this.noticeTemplateDescription!.selectedTemplate = selectedTemplate;
            this.noticeTemplateDescription!.service = service;
          } else
            return;
          this.noticeTemplateDescription!.isShowNoticeTemplate = true;
          this.noticeTemplateService.changeNoticeTemplateDescription(this.noticeTemplateDescription!);
          if (event)
            this.scrollToNoticeTemplateSection();
        }, 0);
        return;
      }
      this.noticeTemplateDescription.isShowNoticeTemplate = true;
      this.noticeTemplateService.changeNoticeTemplateDescription(this.noticeTemplateDescription);
      if (event)
        this.scrollToNoticeTemplateSection();
    }
  }

  private scrollToNoticeTemplateSection(): void {
    setTimeout(() => {
      const el = document.getElementById(`option-form-end`);
      if (el) {
        el.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    }, 0); // Timeout so the DOM is well up to date
  }

  isToggleIsUsingTemplateDisabled(provision: Provision, service: Service): boolean {
    if (!provision.isRedactedByJss && this.getPossibleTemplates(service) && this.noticeTemplateDescription)
      return false;
    else
      return true;
  }

  getPossibleTemplates(service: Service): AnnouncementNoticeTemplate[] | undefined {
    if (service) {
      if (service && service.serviceTypes)
        for (let st of service.serviceTypes)
          if (st.assoServiceProvisionTypes)
            for (let asso of st.assoServiceProvisionTypes)
              if (asso.announcementNoticeTemplates && asso.announcementNoticeTemplates.length > 0)
                return asso.announcementNoticeTemplates;
    }
    return undefined;
  }

  changeProvisionNoticeTemplateDescription(ngbEvent: NgbNavChangeEvent) {
    let destId = ngbEvent.nextId as number;
    let originId = ngbEvent.activeId as number;

    // if id is > 10 and first char begins with 1 then its an announcement tab
    if (this.noticeTemplateDescription)
      if (destId >= 10 && destId < 20) {
        this.noticeTemplateDescription.announcementOrder = this.parseInt(destId.toString().substring(1));
        this.noticeTemplateService.changeNoticeTemplateDescription(this.noticeTemplateDescription);
      } else if (originId >= 10 && destId < 20) {
        this.noticeTemplateDescription.announcementOrder = this.parseInt(originId.toString().substring(1));
        this.noticeTemplateService.changeNoticeTemplateDescription(this.noticeTemplateDescription);
      }
    if (destId < 10) {
      this.changeIsShowNoticeTemplate(false, true, undefined, undefined);
    }
  }

  isDisplayDependantField(assoField: AssoServiceFieldType) {
    if (this.quotation && this.selectedAssoIndex != undefined && this.selectedServiceIndex != undefined) {
      let service = this.quotation.assoAffaireOrders[this.selectedAssoIndex].services[this.selectedServiceIndex];
      if (service && service.serviceTypes) {
        for (let serviceType of service.serviceTypes) {
          if (serviceType.assoServiceTypeFieldTypes) {
            for (let assoServiceTypeFieldType of serviceType.assoServiceTypeFieldTypes) {
              if (assoServiceTypeFieldType.serviceFieldType.id == assoField.serviceFieldType.id) {
                if (!assoServiceTypeFieldType.serviceFieldTypeDependancy || !assoServiceTypeFieldType.serviceFieldTypeDependancyValue)
                  return true;

                let valueToCheck = assoServiceTypeFieldType.serviceFieldTypeDependancyValue;

                // browse all field to check
                for (let assoFieldToCheck of service.assoServiceFieldTypes) {
                  if (assoFieldToCheck.serviceFieldType.id == assoServiceTypeFieldType.serviceFieldTypeDependancy.id) {
                    if (assoFieldToCheck.integerValue + "" == valueToCheck
                      || assoFieldToCheck.selectValue && assoFieldToCheck.selectValue.value == valueToCheck
                      || assoFieldToCheck.stringValue == valueToCheck
                      || assoFieldToCheck.textAreaValue == valueToCheck)
                      return true;
                    return false;
                  }
                }
              }
            }
          }
        }
      }
    }
    return true;
  }

  isDisplayFieldForServiceType(assoServiceFieldType: AssoServiceFieldType, currentServiceType: ServiceType, isLastIndex: boolean) {
    let alreadyFoundIds = [];
    if (this.quotation && this.selectedAssoIndex != undefined && this.selectedServiceIndex != undefined && this.quotation.assoAffaireOrders[this.selectedAssoIndex].services[this.selectedServiceIndex].serviceTypes) {
      for (let serviceType of this.quotation.assoAffaireOrders[this.selectedAssoIndex].services[this.selectedServiceIndex].serviceTypes) {
        if (serviceType.assoServiceTypeFieldTypes) {
          for (let serviceTypeAssoFieldType of serviceType.assoServiceTypeFieldTypes) {
            if (alreadyFoundIds.indexOf(assoServiceFieldType.serviceFieldType.id) >= 0)
              return false;
            alreadyFoundIds.push(serviceTypeAssoFieldType.serviceFieldType.id);
            if (serviceTypeAssoFieldType.serviceFieldType.id == assoServiceFieldType.serviceFieldType.id)
              if (serviceType.id == currentServiceType.id) {
                return true;
              }
          }
        }
      }
    }
    if (isLastIndex && alreadyFoundIds.indexOf(assoServiceFieldType.serviceFieldType.id) < 0)
      return true;
    return false;
  }

}
