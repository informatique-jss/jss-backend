import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { combineLatest } from 'rxjs';
import { AppService } from '../../../../libs/app.service';
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
  standalone: false
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

  servicesForm = this.formBuilder.group({});


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
        this.serviceFamilies = response;
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
      if (!this.currentUser) {
        let promises = [];
        for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++) {
          promises.push(this.serviceService.getServiceForServiceTypeAndAffaire(this.selectedServiceTypes[i], this.quotation.assoAffaireOrders[i].affaire));
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
          this.appService.openRoute(undefined, "quotation", undefined);
        });
      } else {
        let promises = [];
        for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++) {
          promises.push(this.serviceService.addOrUpdateServices(this.selectedServiceTypes[i], this.quotation.assoAffaireOrders[i].affaire.id, this.quotation.assoAffaireOrders[i].id));
        }
        combineLatest(promises).subscribe(response => {
          this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[2]);
          this.appService.openRoute(undefined, "quotation", undefined);
        });
      }
    }
  }

  goBackQuotation() {
    this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[0]);
    this.appService.openRoute(undefined, "quotation", undefined);
  }
}
