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
  }

  selectCard(affaireId: number) {
    this.selectedAssoIndex = affaireId;
  }

  selecteServiceFamily(serviceFamily: ServiceFamily) {
    this.selectedServiceFamily = serviceFamily;
  }

  addServiceToCurrentAffaire(service: ServiceType) {
    if (this.quotation && this.selectedAssoIndex != null)
      if (this.currentUser) {
        if (!this.applyToAllAffaires)
          this.serviceService.getServiceForServiceTypeAndAffaire(service, this.quotation.assoAffaireOrders[this.selectedAssoIndex].affaire).subscribe(response => {
            if (!this.quotation!.assoAffaireOrders[this.selectedAssoIndex!].services)
              this.quotation!.assoAffaireOrders[this.selectedAssoIndex!].services = [];
            this.quotation!.assoAffaireOrders[this.selectedAssoIndex!].services.push(response);
          })
        else
          for (let asso of this.quotation.assoAffaireOrders) {
            let index = this.quotation.assoAffaireOrders.indexOf(asso);
            this.serviceService.getServiceForServiceTypeAndAffaire(service, this.quotation.assoAffaireOrders[this.selectedAssoIndex].affaire).subscribe(response => {
              if (!this.quotation!.assoAffaireOrders[index].services)
                this.quotation!.assoAffaireOrders[index].services = [];
              this.quotation!.assoAffaireOrders[index].services.push(response);
            })
          }
      } else {
        this.serviceService.getServiceForServiceTypeAndAffaire(service, this.quotation.assoAffaireOrders[this.selectedAssoIndex].affaire).subscribe(response => {
          if (!this.applyToAllAffaires) {
            if (!this.quotation!.assoAffaireOrders[this.selectedAssoIndex!].services)
              this.quotation!.assoAffaireOrders[this.selectedAssoIndex!].services = [];
            this.quotation!.assoAffaireOrders[this.selectedAssoIndex!].services.push(response);
          } else if (this.quotation) {
            for (let asso of this.quotation.assoAffaireOrders) {
              let index = this.quotation.assoAffaireOrders.indexOf(asso);
              if (!this.quotation!.assoAffaireOrders[index!].services)
                this.quotation!.assoAffaireOrders[index].services = [];
              this.quotation!.assoAffaireOrders[index].services.push(response);
            }
          }
        })
      }
  }

  removeServiceFromCurrentAffaire(service: ServiceType, assoIndex: number) {
    if (this.quotation)
      this.quotation!.assoAffaireOrders[assoIndex].services.splice(this.getServiceIndexInCurrentAffaire(service), 1);
  }

  getServiceIndexInCurrentAffaire(service: ServiceType): number {
    if (this.quotation && this.selectedAssoIndex != null)
      for (let asso of this.quotation.assoAffaireOrders)
        if (this.quotation.assoAffaireOrders.indexOf(asso) == this.selectedAssoIndex && asso.services)
          for (let serviceAsso of asso.services)
            if (serviceAsso.serviceType.id == service.id) {
              return asso.services.indexOf(serviceAsso);
            }
    return -1;
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
      } else {
        let promises = [];
        for (let asso of this.quotation.assoAffaireOrders) {
          promises.push(this.serviceService.addOrUpdateServices(asso.services, asso.affaire.id, asso.id));
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