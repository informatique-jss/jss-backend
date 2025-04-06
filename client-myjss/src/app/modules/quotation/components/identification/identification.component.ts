import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from '../../../../libs/app.service';
import { ConstantService } from '../../../../libs/constant.service';
import { validateSiret } from '../../../../libs/CustomFormsValidatorsHelper';
import { Affaire } from '../../../my-account/model/Affaire';
import { AssoAffaireOrder } from '../../../my-account/model/AssoAffaireOrder';
import { AffaireService } from '../../../my-account/services/affaire.service';
import { CustomerOrderService } from '../../../my-account/services/customer.order.service';
import { QuotationService } from '../../../my-account/services/quotation.service';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { AffaireType, individual, notIndividual } from '../../model/AffaireType';
import { IQuotation } from '../../model/IQuotation';
import { order, quotation, QuotationType } from '../../model/QuotationType';
import { ServiceFamilyGroup } from '../../model/ServiceFamilyGroup';
import { ServiceFamilyGroupService } from '../../services/service.family.group.service';

@Component({
  selector: 'app-identification',
  templateUrl: './identification.component.html',
  styleUrls: ['./identification.component.css'],
  standalone: false,
})
export class IdentificationComponent implements OnInit {

  selectedQuotationType: QuotationType = quotation;
  familyGroupService: ServiceFamilyGroup[] = [];
  @Input() hideFamilyGroupServiceSelection: boolean = false;

  quotation: IQuotation = {} as IQuotation;
  isRegisteredAffaire: Boolean[] = [];
  affaireTypes: AffaireType[] = [];
  currentOpenedPanel: number | undefined = 0;

  siretSearched: string = "";
  debounce: any;
  loadingSiretSearch: boolean = false;

  notIndividual: AffaireType = notIndividual;
  individual: AffaireType = individual;

  currentUser: Responsable | undefined;
  isSavingQuotation: boolean = false;

  constructor(private formBuilder: FormBuilder,
    private serviceFamilyGroupService: ServiceFamilyGroupService,
    private affaireService: AffaireService,
    private constantService: ConstantService,
    private quotationService: QuotationService,
    private orderService: CustomerOrderService,
    private loginService: LoginService,
    private appService: AppService
  ) { }

  idenficationForm = this.formBuilder.group({});

  ngOnInit() {
    this.serviceFamilyGroupService.getServiceFamilyGroups().subscribe(response => this.familyGroupService = response);
    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
      this.initIQuotation();
    })
    this.initIQuotation();
  }

  selectFamilyGroupService(item: ServiceFamilyGroup) {
    this.initIQuotation();
    this.quotation.serviceFamilyGroup = item;
  }

  initIQuotation() {
    if (this.currentUser) {
      if (this.quotationService.getCurrentDraftQuotationId()) {
        this.quotationService.getQuotation(parseInt(this.quotationService.getCurrentDraftQuotationId()!)).subscribe(response => {
          this.quotation = response;
          this.refreshIsRegisteredAffaire();
        });
        return;
      } else if (this.orderService.getCurrentDraftOrderId()) {
        this.orderService.getCustomerOrder(parseInt(this.orderService.getCurrentDraftOrderId()!)).subscribe(response => {
          this.quotation = response
          this.refreshIsRegisteredAffaire();
        });
        return;
      }
    } else {
      if (this.quotationService.getCurrentDraftQuotation()) {
        this.quotation = this.quotationService.getCurrentDraftQuotation()!;
        this.refreshIsRegisteredAffaire();
        return;
      } else if (this.orderService.getCurrentDraftOrder()) {
        this.quotation = this.orderService.getCurrentDraftOrder()!;
        this.refreshIsRegisteredAffaire();
        return;
      }
    }
    this.quotation.assoAffaireOrders = [];
    this.addAffaire();
  }

  refreshIsRegisteredAffaire() {
    if (this.quotation)
      for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++)
        this.isRegisteredAffaire[i] = this.quotation.assoAffaireOrders[i].affaire.siret != null && this.quotation.assoAffaireOrders[i].affaire.siret != undefined && this.quotation.assoAffaireOrders[i].affaire.siret != "";
  }

  addAffaire() {
    if (this.quotation && this.quotation.assoAffaireOrders)
      this.quotation.assoAffaireOrders.push({ affaire: { isIndividual: false, country: this.constantService.getCountryFrance() } as Affaire } as AssoAffaireOrder);
    this.isRegisteredAffaire[this.quotation.assoAffaireOrders.length - 1] = true;
    this.affaireTypes[this.quotation.assoAffaireOrders.length - 1] = notIndividual;
    this.currentOpenedPanel = this.quotation.assoAffaireOrders.length - 1;
  }

  openPanel(index: number) {
    if (index == this.currentOpenedPanel)
      this.currentOpenedPanel = undefined;
    else
      this.currentOpenedPanel = index;
  }

  deleteAffaire(indexAsso: number) {
    this.quotation.assoAffaireOrders.splice(indexAsso, 1);
    this.isRegisteredAffaire.splice(indexAsso, 1);
    this.affaireTypes.splice(indexAsso, 1);
  }

  searchSiret(indexAsso: number) {
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.effectiveSearchSiret(indexAsso);
    }, 500);
  }

  effectiveSearchSiret(indexAsso: number) {
    if (this.siretSearched && validateSiret(this.siretSearched)) {
      this.loadingSiretSearch = true;
      this.affaireService.getAffaireBySiret(this.siretSearched).subscribe(response => {
        this.loadingSiretSearch = false;
        if (response && response.length == 1) {
          this.quotation.assoAffaireOrders[indexAsso].affaire = response[0];
          this.siretSearched = "";
        }
      })
    }
  }

  canStartQuotation() {
    if (this.quotation && this.quotation.assoAffaireOrders && this.quotation.assoAffaireOrders.length > 0) {
      let allAssoOk = true;
      for (let asso of this.quotation.assoAffaireOrders) {
        if (!asso.affaire) {
          allAssoOk = false;
        } else if (!asso.affaire.id) {
          if (asso.affaire.isIndividual) {
            if (!asso.affaire.firstname || !asso.affaire.lastname) {
              allAssoOk = false
            }
          } else {
            if (!asso.affaire.denomination) {
              allAssoOk = false;
            }
          }
          if (!asso.affaire.city || !asso.affaire.city.id || !asso.affaire.country || !asso.affaire.country.id || !asso.affaire.postalCode)
            allAssoOk = false;
        } else {
          if (!asso.affaire.siret)
            allAssoOk = false;
        }
      }
      if (allAssoOk)
        return true;
    }
    return false;
  }

  startQuotation() {
    this.isSavingQuotation = true;
    if (this.selectedQuotationType)
      if (this.currentUser) {
        if (this.selectedQuotationType.id == quotation.id) {
          this.quotation.isQuotation = true;
          this.quotationService.saveQuotation(this.quotation).subscribe(response => {
            this.quotation = response;
            this.quotationService.setCurrentDraftQuotationId(this.quotation.id);
            this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[1]);
            this.appService.openRoute(undefined, "quotation", undefined);
          })
        } else if (this.selectedQuotationType.id == order.id) {
          this.quotation.isQuotation = false;
          this.orderService.saveOrder(this.quotation).subscribe(response => {
            this.quotation = response;
            this.orderService.setCurrentDraftOrderId(this.quotation.id);
            this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[1]);
            this.appService.openRoute(undefined, "quotation", undefined);
          })
        }
      } else {
        if (this.selectedQuotationType.id == quotation.id) {
          this.quotation.isQuotation = true;
          this.quotationService.setCurrentDraftQuotation(this.quotation);
        } else if (this.selectedQuotationType.id == order.id) {
          this.quotation.isQuotation = false;
          this.orderService.setCurrentDraftOrder(this.quotation);
        }
        this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[1]);
        this.appService.openRoute(undefined, "quotation", undefined);
      }
  }

}
