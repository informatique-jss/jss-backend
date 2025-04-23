import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ASSO_SERVICE_DOCUMENT_ENTITY_TYPE, PROVISION_SCREEN_TYPE_ANNOUNCEMENT, PROVISION_SCREEN_TYPE_DOMICILIATION, SERVICE_FIELD_TYPE_DATE, SERVICE_FIELD_TYPE_INTEGER, SERVICE_FIELD_TYPE_SELECT, SERVICE_FIELD_TYPE_TEXT, SERVICE_FIELD_TYPE_TEXTAREA } from '../../../../libs/Constants';
import { Attachment } from '../../../my-account/model/Attachment';
import { Service } from '../../../my-account/model/Service';
import { CustomerOrderService } from '../../../my-account/services/customer.order.service';
import { QuotationService } from '../../../my-account/services/quotation.service';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { IQuotation } from '../../model/IQuotation';
import { ServiceFamily } from '../../model/ServiceFamily';
import { ServiceTypeService } from '../../services/service.type.service';

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

  SERVICE_FIELD_TYPE_TEXT = SERVICE_FIELD_TYPE_TEXT;
  SERVICE_FIELD_TYPE_INTEGER = SERVICE_FIELD_TYPE_INTEGER;
  SERVICE_FIELD_TYPE_DATE = SERVICE_FIELD_TYPE_DATE;
  SERVICE_FIELD_TYPE_TEXTAREA = SERVICE_FIELD_TYPE_TEXTAREA;
  SERVICE_FIELD_TYPE_SELECT = SERVICE_FIELD_TYPE_SELECT;
  PROVISION_SCREEN_TYPE_DOMICILIATION = PROVISION_SCREEN_TYPE_DOMICILIATION;
  PROVISION_SCREEN_TYPE_ANNOUNCEMENT = PROVISION_SCREEN_TYPE_ANNOUNCEMENT;
  ASSO_SERVICE_DOCUMENT_ENTITY_TYPE = ASSO_SERVICE_DOCUMENT_ENTITY_TYPE;
  serviceFamilies: ServiceFamily[] = [];


  uploadedFiles: Attachment[] = [];
  downloadingPercentage: number = 100;
  totalFileWeight: number = 120;
  isDownloading: boolean = this.downloadingPercentage != 100 ? true : false;
  isError: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private loginService: LoginService,
    private quotationService: QuotationService,
    private orderService: CustomerOrderService,
    private serviceTypeService: ServiceTypeService,
  ) { }

  informationForm = this.formBuilder.group({});

  ngOnInit() {
    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
      this.initIQuotation();
    })
    this.initIQuotation();
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
      if (this.quotation.assoAffaireOrders[this.selectedAssoIndex].services && this.quotation.assoAffaireOrders[this.selectedAssoIndex].services.length > 0) {
        this.selectedServiceIndex = 0;
      }
    }
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

  refreshCurrentAssoAffaireOrder() {
    // TODO voir avec Alex qu'est-ce que ça fait et comment l'uiliser
    // if (this.quotation)
    //   this.quotationService.getAsso(this.quotation).subscribe(response => {
    //     this.ordersAssoAffaireOrders = response;
    //     if (response && this.selectedAssoAffaireOrder)
    //       for (let asso of this.ordersAssoAffaireOrders)
    //         if (asso.id == this.selectedAssoAffaireOrder.id)
    //           this.changeAffaire(asso);
    //     initTooltips();
    //   })
  }

  deleteFile() {
    //TODO
    throw new Error('Method not implemented.');
  }
}
