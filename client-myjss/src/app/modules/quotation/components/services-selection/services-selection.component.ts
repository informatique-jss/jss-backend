import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { combineLatest } from 'rxjs';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { GenericToggleComponent } from '../../../miscellaneous/components/forms/generic-toggle/generic-toggle.component';
import { AssoAffaireOrder } from '../../../my-account/model/AssoAffaireOrder';
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
  imports: [SHARED_IMPORTS, GenericInputComponent, NgbNavModule, GenericToggleComponent]
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
  serviceLinkToggles: boolean[][] = [];
  activeId = 0;

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
    this.serviceLinkToggles = [];
    if (this.quotation && this.quotation.assoAffaireOrders)
      for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++) {
        this.serviceLinkToggles[i] = [];
      }

    this.selectedAssoIndex = 0;
    if (this.quotation && this.quotation.serviceFamilyGroup)
      this.serviceFamilyService.getServiceFamiliesForFamilyGroup(this.quotation.serviceFamilyGroup.id).subscribe(response => {
        this.serviceFamilies = response.filter(s => s.myJssOrder != null && s.myJssOrder != undefined).sort((a: ServiceFamily, b: ServiceFamily) => a.myJssOrder - b.myJssOrder);
        for (let family of this.serviceFamilies) {
          if (family.services)
            family.services.sort((a, b) => a.label.localeCompare(b.label));
        }
        this.selectedServiceFamily = this.serviceFamilies[0];
        this.manageFamilySelection();
      });

    if (this.quotation && this.quotation.assoAffaireOrders) {
      for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++) {
        if (this.selectedServiceTypes[i] == null)
          this.selectedServiceTypes[i] = [];
      }
    }
  }

  manageFamilySelection() {
    if (this.quotation && this.selectedAssoIndex != undefined && this.serviceFamilies) {
      for (let i = 0; i < this.serviceFamilies.length; i++) {
        if (this.shouldDisplayServiceFamily(this.serviceFamilies[i], this.quotation.assoAffaireOrders[this.selectedAssoIndex])) {
          this.activeId = i;
          this.selectedServiceFamily = this.serviceFamilies[i];
          return;
        }
      }
    }

  }

  getFamilyLabelForService(service: ServiceType) {
    if (this.serviceFamilies)
      for (let family of this.serviceFamilies)
        if (family.services)
          for (let ser of family.services)
            if (ser.id == service.id)
              return family.customLabel ? family.customLabel : family.label;
    return "";
  }

  selectCard(affaireId: number) {
    this.selectedAssoIndex = affaireId;
    this.manageFamilySelection();
  }

  selecteServiceFamily(serviceFamily: ServiceFamily) {
    this.selectedServiceFamily = serviceFamily;
  }

  addServiceToCurrentAffaire(service: ServiceType, indexService: number) {
    if (this.applyToAllAffaires) {
      if (this.quotation && this.quotation.assoAffaireOrders) {
        for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++) {
          if (this.selectedServiceTypes[i].indexOf(service) < 0) {
            this.selectedServiceTypes[i].push(service);
            this.serviceLinkToggles[i][service.id] = false;
          }
        }
      }
    } else {
      if (this.selectedAssoIndex != null && this.selectedAssoIndex >= 0) {
        if (!this.selectedServiceTypes[this.selectedAssoIndex])
          this.selectedServiceTypes[this.selectedAssoIndex] = [];
        if (this.selectedServiceTypes[this.selectedAssoIndex].indexOf(service) < 0)
          this.selectedServiceTypes[this.selectedAssoIndex].push(service);
        this.serviceLinkToggles[this.selectedAssoIndex][service.id] = false;
      }
    }
  }

  isServiceSelected(service: ServiceType) {
    if (this.selectedServiceTypes && this.selectedAssoIndex != undefined)
      return this.selectedServiceTypes[this.selectedAssoIndex].indexOf(service) >= 0;
    return false;
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
    if (this.quotation && this.selectedAssoIndex != null && this.selectedServiceTypes.length > 0) {
      if (this.selectedServiceTypes[this.selectedAssoIndex] != null) {
        if (this.selectedServiceTypes[this.selectedAssoIndex].indexOf(service) >= 0)
          return this.selectedServiceTypes[this.selectedAssoIndex].indexOf(service);
        if (service.serviceTypeLinked)
          return this.selectedServiceTypes[this.selectedAssoIndex].indexOf(service.serviceTypeLinked);
        return this.selectedServiceTypes[this.selectedAssoIndex].indexOf(service);
      }
    }
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
          promises.push(this.serviceService.getServiceForServiceType(this.getServiceListForAsso(i), this.quotation.assoAffaireOrders[i].affaire.city));
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
          promises.push(this.serviceService.addOrUpdateServices(this.getServiceListForAsso(i), this.quotation.assoAffaireOrders[i].id));
        }
        combineLatest(promises).subscribe(response => {
          this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[2]);
          this.appService.hideLoadingSpinner();
          this.appService.openRoute(undefined, "quotation/required-information", undefined);
        });
      }
    }
  }

  getServiceListForAsso(indexAssoAffaireOrder: number) {
    let outList = [];
    if (this.selectedServiceTypes && this.selectedServiceTypes[indexAssoAffaireOrder]) {
      for (let j = 0; j < this.selectedServiceTypes[indexAssoAffaireOrder].length; j++) {
        if (this.serviceLinkToggles && this.serviceLinkToggles[indexAssoAffaireOrder] && this.serviceLinkToggles[indexAssoAffaireOrder][this.selectedServiceTypes[indexAssoAffaireOrder][j].id])
          outList.push(this.selectedServiceTypes[indexAssoAffaireOrder][j].serviceTypeLinked);
        else
          outList.push(this.selectedServiceTypes[indexAssoAffaireOrder][j]);
      }
    }
    return outList;
  }

  goBackQuotation() {
    this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[0]);
    this.appService.openRoute(undefined, "quotation/identification", undefined);
  }

  shouldDisplayServiceFamily(serviceFamily: ServiceFamily, asso: AssoAffaireOrder) {
    if (serviceFamily && asso && asso.affaire.siret) {
      for (let service of serviceFamily.services)
        if (service.isRequiringNewRegisteredAffaire)
          return true;
    } else if (serviceFamily && asso && asso.affaire && (asso.affaire.siret == null || asso.affaire.siret == undefined || asso.affaire.siret == "")) {
      for (let service of serviceFamily.services)
        if (service.isRequiringNewUnregisteredAffaire)
          return true;
    }
    return false;
  }
}
