import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ChangeEvent, CKEditorModule } from '@ckeditor/ckeditor5-angular';
import { NgbModal, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { Alignment, Bold, ClassicEditor, Essentials, Font, GeneralHtmlSupport, Indent, IndentBlock, Italic, Link, List, Mention, Paragraph, PasteFromOffice, RemoveFormat, Underline, Undo } from 'ckeditor5';
import { combineLatest, of, tap } from 'rxjs';
import { PROVISION_SCREEN_TYPE_ANNOUNCEMENT, PROVISION_SCREEN_TYPE_DOMICILIATION, SERVICE_FIELD_TYPE_DATE, SERVICE_FIELD_TYPE_INTEGER, SERVICE_FIELD_TYPE_SELECT, SERVICE_FIELD_TYPE_TEXT, SERVICE_FIELD_TYPE_TEXTAREA } from '../../../../libs/Constants';
import { validateEmail, validateFrenchPhone, validateInternationalPhone } from '../../../../libs/CustomFormsValidatorsHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { Mail } from '../../../general/model/Mail';
import { AppService } from '../../../main/services/app.service';
import { ConstantService } from '../../../main/services/constant.service';
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
import { Provision } from '../../../my-account/model/Provision';
import { Service } from '../../../my-account/model/Service';
import { AssoServiceDocumentService } from '../../../my-account/services/asso.service.document.service';
import { CustomerOrderService } from '../../../my-account/services/customer.order.service';
import { QuotationService } from '../../../my-account/services/quotation.service';
import { ServiceService } from '../../../my-account/services/service.service';
import { Civility } from '../../../profile/model/Civility';
import { Department } from '../../../profile/model/Department';
import { Phone } from '../../../profile/model/Phone';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { Domiciliation } from '../../model/Domiciliation';
import { DomiciliationContractType } from '../../model/DomiciliationContractType';
import { IQuotation } from '../../model/IQuotation';
import { MailRedirectionType } from '../../model/MailRedirectionType';
import { NoticeType } from '../../model/NoticeType';
import { NoticeTypeFamily } from '../../model/NoticeTypeFamily';
import { ServiceFamily } from '../../model/ServiceFamily';
import { CityService } from '../../services/city.service';
import { CivilityService } from '../../services/civility.service';
import { DepartmentService } from '../../services/department.service';
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
    NgbNavModule
  ]
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

  announcementNoticeInit: string = "";

  noticeTypes: NoticeType[] | undefined;
  noticeTypeFamilies: NoticeTypeFamily[] | undefined;
  departments: Department[] | undefined;
  civilities: Civility[] | undefined;

  minDatePublication: Date = new Date();

  isDoNotGenerateAnnouncement: boolean = true;
  selectionRedaction: string[] = [this.CONFIER_ANNONCE_AU_JSS, "Je m'occupe de la publication de l'annonce légale"];
  selectedRedaction: string[][] = [];
  isSaving: boolean = false;
  isGoBack: boolean = false;

  checkedOnce = false;
  isBrowser = false;

  SERVICE_FIELD_TYPE_TEXT = SERVICE_FIELD_TYPE_TEXT;
  SERVICE_FIELD_TYPE_INTEGER = SERVICE_FIELD_TYPE_INTEGER;
  SERVICE_FIELD_TYPE_DATE = SERVICE_FIELD_TYPE_DATE;
  SERVICE_FIELD_TYPE_TEXTAREA = SERVICE_FIELD_TYPE_TEXTAREA;
  SERVICE_FIELD_TYPE_SELECT = SERVICE_FIELD_TYPE_SELECT;
  PROVISION_SCREEN_TYPE_DOMICILIATION = PROVISION_SCREEN_TYPE_DOMICILIATION;
  PROVISION_SCREEN_TYPE_ANNOUNCEMENT = PROVISION_SCREEN_TYPE_ANNOUNCEMENT;

  mailRedirectionTypeOther!: MailRedirectionType;
  domiciliationContractTypeRouteEmailAndMail!: DomiciliationContractType
  domiciliationContractTypeRouteMail!: DomiciliationContractType

  newMailDomiciliation: string = "";
  newMailLegalGardian: string = "";
  newPhoneLegalGardian: string = "";

  serviceFamilies: ServiceFamily[] = [];

  ckEditorHeader = ClassicEditor;

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
    private modalService: NgbModal
  ) { }


  informationForm!: FormGroup;

  parseInt = parseInt;

  async ngOnInit() {
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
              for (let provision of serv.provisions) {
                if (provision.provisionType.provisionScreenType.code == PROVISION_SCREEN_TYPE_ANNOUNCEMENT) {
                  provision.order = ++i;
                  if (!this.selectedRedaction[asso.services.indexOf(serv)]) {
                    this.selectedRedaction[asso.services.indexOf(serv)] = [];
                  }
                  this.selectedRedaction[asso.services.indexOf(serv)][serv.provisions.indexOf(provision)] = this.CONFIER_ANNONCE_AU_JSS;
                  if (!provision.announcement) {
                    provision.announcement = {} as Announcement;
                    provision.isRedactedByJss = true;
                  }
                } else if (provision.provisionType.provisionScreenType.code == PROVISION_SCREEN_TYPE_DOMICILIATION) {
                  if (!provision.domiciliation) {
                    provision.domiciliation = {} as Domiciliation;
                  }
                }
              }
            }
          }
        }
      }

      if (this.quotation.assoAffaireOrders[this.selectedAssoIndex].services && this.quotation.assoAffaireOrders[this.selectedAssoIndex].services.length > 0) {
        this.selectedServiceIndex = 0;
      }
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
    }
  } as any;

  onNoticeChange(event: ChangeEvent, provision: Provision) {
    if (provision && provision.announcement)
      provision.announcement.notice = event.editor.getData();
  }

  onIsCompleteChange(event: boolean, selectedAssoIndex: number, selectedServiceIndex: number, assoServiceDocumentIndex: number, assoServiceDocument: AssoServiceDocument) {
    if (event) {
      this.refreshAssoServiceDocument(selectedAssoIndex, selectedServiceIndex, assoServiceDocumentIndex, assoServiceDocument);
    }
  }

  onIsUploadDelete(event: boolean, selectedAssoIndex: number, selectedServiceIndex: number, assoServiceDocumentIndex: number, assoServiceDocument: AssoServiceDocument) {
    if (event) {
      this.refreshAssoServiceDocument(selectedAssoIndex, selectedServiceIndex, assoServiceDocumentIndex, assoServiceDocument);
    }
  }

  refreshAssoServiceDocument(selectedAssoIndex: number, selectedServiceIndex: number, assoServiceDocumentIndex: number, assoServiceDocument: AssoServiceDocument) {
    this.assoServiceDocumentService.getAssoServiceDocument(assoServiceDocument).subscribe(response => {
      if (this.quotation) {
        this.quotation.assoAffaireOrders[selectedAssoIndex].services[selectedServiceIndex].assoServiceDocuments[assoServiceDocumentIndex] = response;
      }
    });
  }

  canSaveQuotation() {
    if (this.quotation)
      for (let asso of this.quotation.assoAffaireOrders)
        if (!asso.services || asso.services.length == 0)
          return false;
    return true;
  }

  saveFieldsValue() {
    if (this.quotation && this.selectedAssoIndex != undefined && this.selectedServiceIndex != undefined) {
      if (this.currentUser) {
        this.isSaving = true;
        return this.serviceService.addOrUpdateService(this.quotation.assoAffaireOrders[this.selectedAssoIndex].services[this.selectedServiceIndex]).pipe(tap(response => {
          this.isSaving = false;
        }));

      } else {
        if (this.quotation.isQuotation) {
          this.quotationService.setCurrentDraftQuotation(this.quotation);
          return of({} as Service);
        } else {
          this.orderService.setCurrentDraftOrder(this.quotation);
          return of({} as Service);
        }
      }
    }
    return of({} as Service);
  }

  moveToService(newServiceIndex: number, newAssoIndex: number) {
    if (!this.quotation)
      return;

    // move backward
    if (newServiceIndex < 0) {
      newAssoIndex = newAssoIndex - 1;
    }

    if (newAssoIndex < 0) {
      this.saveFieldsValue();
      this.goBackQuotationModale(this.confirmBackModal);
      return;
    }

    // move forward
    if (newServiceIndex >= this.quotation.assoAffaireOrders[newAssoIndex].services.length) {
      newAssoIndex = newAssoIndex + 1;
      newServiceIndex = 0;
    }

    if (newAssoIndex >= this.quotation.assoAffaireOrders.length) {
      this.saveFieldsValue().subscribe(response => {
        let r = response;
        this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[3]);
        this.appService.openRoute(undefined, "quotation/checkout", undefined);
      });
      return;
    }

    this.saveFieldsValue().subscribe();
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
      backdrop: 'static',
    });

    this.goBackModalInstance.result.finally(() => {
      this.goBackModalInstance = undefined;
    });
  }

  confirmGoBack() {
    if (this.currentUser) {
      this.isGoBack = true;
      let promises = [];
      if (this.quotation && this.quotation.assoAffaireOrders)
        for (let asso of this.quotation.assoAffaireOrders)
          if (asso.services)
            for (let service of asso.services)
              promises.push(this.serviceService.deleteService(service));
      combineLatest(promises).subscribe(response => {
        this.isGoBack = false;
        this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[1]);
        this.appService.openRoute(undefined, "quotation/services-selection", undefined);
      })
    } else {
      if (this.quotation && this.quotation.assoAffaireOrders)
        for (let asso of this.quotation.assoAffaireOrders)
          asso.services = [];
      this.saveFieldsValue();
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

}
