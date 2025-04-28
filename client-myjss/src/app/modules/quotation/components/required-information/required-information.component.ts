import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ChangeEvent } from '@ckeditor/ckeditor5-angular';
import { Alignment, Bold, ClassicEditor, Essentials, Font, GeneralHtmlSupport, Indent, IndentBlock, Italic, Link, List, Mention, Paragraph, PasteFromOffice, RemoveFormat, Underline, Undo } from 'ckeditor5';
import { AppService } from '../../../../libs/app.service';
import { PROVISION_SCREEN_TYPE_ANNOUNCEMENT, PROVISION_SCREEN_TYPE_DOMICILIATION, SERVICE_FIELD_TYPE_DATE, SERVICE_FIELD_TYPE_INTEGER, SERVICE_FIELD_TYPE_SELECT, SERVICE_FIELD_TYPE_TEXT, SERVICE_FIELD_TYPE_TEXTAREA } from '../../../../libs/Constants';
import { Affaire } from '../../../my-account/model/Affaire';
import { AssoServiceDocument } from '../../../my-account/model/AssoServiceDocument';
import { Service } from '../../../my-account/model/Service';
import { AssoServiceDocumentService } from '../../../my-account/services/asso.service.document.service';
import { CustomerOrderService } from '../../../my-account/services/customer.order.service';
import { QuotationService } from '../../../my-account/services/quotation.service';
import { ServiceService } from '../../../my-account/services/service.service';
import { Civility } from '../../../profile/model/Civility';
import { Department } from '../../../profile/model/Department';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { IQuotation } from '../../model/IQuotation';
import { NoticeType } from '../../model/NoticeType';
import { NoticeTypeFamily } from '../../model/NoticeTypeFamily';
import { ServiceFamily } from '../../model/ServiceFamily';
import { CivilityService } from '../../services/civility.service';
import { DepartmentService } from '../../services/department.service';
import { NoticeTypeFamilyService } from '../../services/notice.type.family.service';
import { NoticeTypeService } from '../../services/notice.type.service';

@Component({
  selector: 'required-information',
  templateUrl: './required-information.component.html',
  styleUrls: ['./required-information.component.css'],
  standalone: false
})
export class RequiredInformationComponent implements OnInit {


  selectedAssoIndex: number | null = null;
  selectedServiceIndex: number | null = null;
  selectedService: Service | null = null;
  currentUser: Responsable | undefined;
  quotation: IQuotation | undefined;

  affaire: Affaire = { isIndividual: false } as Affaire;

  announcementPublicationDate: Date = new Date();
  announcementRedactedByJss: Boolean = true;
  announcementProofReading: Boolean | undefined;
  announcementNoticeFamily: NoticeTypeFamily | undefined;
  announcementNoticeType: NoticeType | undefined;
  announcementDepartment: Department | undefined;
  announcementNotice: string | undefined;
  announcementNoticeInit: string = "";

  noticeTypes: NoticeType[] | undefined;
  noticeTypeFamilies: NoticeTypeFamily[] | undefined;
  departments: Department[] | undefined;
  civilities: Civility[] | undefined;

  minDatePublication: Date = new Date();

  checkedOnce = false;

  SERVICE_FIELD_TYPE_TEXT = SERVICE_FIELD_TYPE_TEXT;
  SERVICE_FIELD_TYPE_INTEGER = SERVICE_FIELD_TYPE_INTEGER;
  SERVICE_FIELD_TYPE_DATE = SERVICE_FIELD_TYPE_DATE;
  SERVICE_FIELD_TYPE_TEXTAREA = SERVICE_FIELD_TYPE_TEXTAREA;
  SERVICE_FIELD_TYPE_SELECT = SERVICE_FIELD_TYPE_SELECT;
  PROVISION_SCREEN_TYPE_DOMICILIATION = PROVISION_SCREEN_TYPE_DOMICILIATION;
  PROVISION_SCREEN_TYPE_ANNOUNCEMENT = PROVISION_SCREEN_TYPE_ANNOUNCEMENT;
  serviceFamilies: ServiceFamily[] = [];

  ckEditorHeader = ClassicEditor;

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
  ) { }

  informationForm = this.formBuilder.group({});

  ngOnInit() {
    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
      this.initIQuotation();
    })
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
      // Init order of provisions for front-end
      for (let asso of this.quotation.assoAffaireOrders) {
        if (asso.services && asso.services.length > 0) {
          for (let serv of asso.services) {
            if (serv.provisions && serv.provisions.length > 0) {
              let i = 0;
              for (let provision of serv.provisions) {
                if (provision.provisionType.provisionScreenType.code == PROVISION_SCREEN_TYPE_ANNOUNCEMENT)
                  provision.order = ++i;
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
        if (this.departments && this.affaire && this.affaire.city && this.affaire.city.department)
          for (let department of this.departments)
            if (department.id == this.affaire.city.department.id) {
              this.announcementDepartment = department;
            }
      });
  }


  selectCard(assoIndex: number, event: Event): void {
    // Do not propagate clic if it is on pill
    if ((event.target as HTMLElement).closest('.pill')) {
      return;
    }
    this.selectedAssoIndex = assoIndex;
  }

  selectServiceIndex(selectedService: number, selectedAssoIndex: number) {
    this.selectedServiceIndex = selectedService;
    this.selectedAssoIndex = selectedAssoIndex;
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

  onNoticeChange(event: ChangeEvent) {
    this.announcementNotice = event.editor.getData();
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

  saveQuotation() {
    if (this.quotation) {
      if (!this.currentUser) {
        if (this.quotation.isQuotation) {
          this.quotationService.setCurrentDraftQuotation(this.quotation);
        } else {
          this.orderService.setCurrentDraftOrder(this.quotation);
        }
      }
      this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[3]);
      this.appService.openRoute(undefined, "quotation", undefined);
    }
  }

  saveFieldsValue() {
    if (this.quotation && this.selectedAssoIndex != undefined && this.selectedServiceIndex != undefined) {
      this.serviceService.addOrUpdateService(this.quotation.assoAffaireOrders[this.selectedAssoIndex].services[this.selectedServiceIndex]).subscribe(response => {
        if (this.quotation!.assoAffaireOrders[this.selectedAssoIndex!].services[this.selectedServiceIndex! + 1])
          this.selectedServiceIndex!++;

        else if (this.quotation!.assoAffaireOrders[this.selectedAssoIndex! + 1])
          this.selectedAssoIndex!++;
      });

    } else if (this.quotation) {
      this.selectedAssoIndex = 0;
      this.selectedServiceIndex = 0;
    }
  }
}
