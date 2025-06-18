import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { combineLatest } from 'rxjs';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { ServiceType } from '../../../my-account/model/ServiceType';
import { CustomerOrderService } from '../../../my-account/services/customer.order.service';
import { QuotationService } from '../../../my-account/services/quotation.service';
import { ServiceService } from '../../../my-account/services/service.service';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { IQuotation } from '../../model/IQuotation';
import { ServiceFamily } from '../../model/ServiceFamily';
import { ServiceFamilyService } from '../../services/service.family.service';

@Component({
  selector: 'services-selection',
  templateUrl: './services-selection.component.html',
  styleUrls: ['./services-selection.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent, NgbNavModule]
})
export class ServicesSelectionComponent implements OnInit {

  serviceFamilies: ServiceFamily[] = [];
  selectedServiceFamily: ServiceFamily | undefined;
  selectedServiceTypes: ServiceType[][] = [];
  selectedAssoIndex: number | null = null;
  searchtext: string = '';
  quotation: IQuotation | undefined;
  currentUser: Responsable | undefined;
  applyToAllAffaires: boolean = false;

  constructor(
    private formBuilder: FormBuilder,
    private serviceFamilyService: ServiceFamilyService,
    private quotationService: QuotationService,
    private orderService: CustomerOrderService,
    private loginService: LoginService,
    private appService: AppService,
    private serviceService: ServiceService
  ) { }

  servicesForm!: FormGroup;


  async ngOnInit() {
    this.servicesForm = this.formBuilder.group({});
    await this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
      this.initIQuotation();
    })
    if (!this.currentUser)
      this.initIQuotation();
  }

  initIQuotation() {
    if (this.currentUser) {
      if (this.quotationService.getCurrentDraftQuotationId()) {
        this.quotationService.getQuotation(parseInt(this.quotationService.getCurrentDraftQuotationId()!)).subscribe(response => {
          this.quotation = response;
          this.refreshServices();
        });
      } else if (this.orderService.getCurrentDraftOrderId()) {
        this.orderService.getCustomerOrder(parseInt(this.orderService.getCurrentDraftOrderId()!)).subscribe(response => {
          this.quotation = response
          this.refreshServices();
        });
      }
    } else {
      if (this.quotationService.getCurrentDraftQuotation()) {
        this.quotation = this.quotationService.getCurrentDraftQuotation()!;
        this.refreshServices();
      } else if (this.orderService.getCurrentDraftOrder()) {
        this.quotation = this.orderService.getCurrentDraftOrder()!;
        this.refreshServices();
      }
    }
  }

  refreshServices() {
    this.selectedAssoIndex = 0;
    if (this.quotation && this.quotation.serviceFamilyGroup)
      this.serviceFamilyService.getServiceFamiliesForFamilyGroup(this.quotation.serviceFamilyGroup.id).subscribe(response => {
        this.serviceFamilies = response.filter(s => s.myJssOrder != null && s.myJssOrder != undefined).sort((a: ServiceFamily, b: ServiceFamily) => a.myJssOrder - b.myJssOrder);
        for (let family of this.serviceFamilies)
          if (family.services)
            family.services.sort((a, b) => a.label.localeCompare(b.label));
        this.selectedServiceFamily = this.serviceFamilies[0];
      });

    if (this.quotation && this.quotation.assoAffaireOrders) {
      for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++) {
        if (this.selectedServiceTypes[i] == null)
          this.selectedServiceTypes[i] = [];
      }
    }
  }

  selectCard(affaireId: number) {
    this.selectedAssoIndex = affaireId;
  }

  selecteServiceFamily(serviceFamily: ServiceFamily) {
    this.selectedServiceFamily = serviceFamily;
  }

  addServiceToCurrentAffaire(service: ServiceType) {
    if (this.applyToAllAffaires) {
      if (this.quotation && this.quotation.assoAffaireOrders) {
        for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++) {
          this.selectedServiceTypes[i].push(service);
        }
      }
    } else {
      if (this.selectedAssoIndex != null && this.selectedAssoIndex >= 0) {
        if (!this.selectedServiceTypes[this.selectedAssoIndex])
          this.selectedServiceTypes[this.selectedAssoIndex] = [];
        this.selectedServiceTypes[this.selectedAssoIndex].push(service);
      }
    }
  }

  removeServiceFromCurrentAffaire(service: ServiceType) {
    if (this.applyToAllAffaires) {
      if (this.quotation && this.quotation.assoAffaireOrders) {
        for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++) {
          if (this.selectedServiceTypes[i] && this.selectedServiceTypes[i].indexOf(service) >= 0)
            this.selectedServiceTypes[i].splice(this.selectedServiceTypes[i].indexOf(service), 1);
        }
      }
    } else {
      if (this.selectedAssoIndex != null && this.selectedAssoIndex >= 0) {
        if (this.selectedServiceTypes[this.selectedAssoIndex] && this.selectedServiceTypes[this.selectedAssoIndex].indexOf(service) >= 0)
          this.selectedServiceTypes[this.selectedAssoIndex].splice(this.selectedServiceTypes[this.selectedAssoIndex].indexOf(service), 1);
      }
    }
  }

  getServiceIndexInCurrentAffaire(service: ServiceType): number {
    if (this.quotation && this.selectedAssoIndex != null && this.selectedServiceTypes.length > 0)
      if (this.selectedServiceTypes[this.selectedAssoIndex] != null)
        return this.selectedServiceTypes[this.selectedAssoIndex].indexOf(service);
    return -1;
  }

  canSaveQuotation() {
    if (this.quotation && this.quotation.assoAffaireOrders)
      for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++)
        if (!this.selectedServiceTypes[i] || this.selectedServiceTypes[i].length < 1)
          return false;
    return true;
  }

  saveQuotation() {
    if (this.quotation) {
      this.appService.showLoadingSpinner();
      if (!this.currentUser) {
        let promises = [];
        for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++) {
          promises.push(this.serviceService.getServiceForServiceType(this.selectedServiceTypes[i], this.quotation.assoAffaireOrders[i].affaire.city));
        }
        combineLatest(promises).subscribe(response => {
          for (let i = 0; i < this.quotation!.assoAffaireOrders.length; i++) {
            this.quotation!.assoAffaireOrders[i].services = response[i];
          }

          if (this.quotation!.isQuotation) {
            this.quotationService.setCurrentDraftQuotation(this.quotation!);
          } else {
            this.orderService.setCurrentDraftOrder(this.quotation!);
          }

          this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[2]);
          this.appService.hideLoadingSpinner();
          this.appService.openRoute(undefined, "quotation/required-information", undefined);
        });
      } else {
        let promises = [];
        for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++) {
          promises.push(this.serviceService.addOrUpdateServices(this.selectedServiceTypes[i], this.quotation.assoAffaireOrders[i].id));
        }
        combineLatest(promises).subscribe(response => {
          this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[2]);
          this.appService.hideLoadingSpinner();
          this.appService.openRoute(undefined, "quotation/required-information", undefined);
        });
      }
    }
  }

  goBackQuotation() {
    this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[0]);
    this.appService.openRoute(undefined, "quotation/identification", undefined);
  }

  hasServiceForUnregisteredAffaire(serviceFamily: ServiceFamily) {
    if (serviceFamily) {
      for (let service of serviceFamily.services)
        if (service.isRequiringNewUnregisteredAffaire)
          return true;
    }
    return false;
  }
}
