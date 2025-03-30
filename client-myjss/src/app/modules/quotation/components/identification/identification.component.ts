import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ConstantService } from '../../../../libs/constant.service';
import { validateSiret } from '../../../../libs/CustomFormsValidatorsHelper';
import { Affaire } from '../../../my-account/model/Affaire';
import { AssoAffaireOrder } from '../../../my-account/model/AssoAffaireOrder';
import { AffaireService } from '../../../my-account/services/affaire.service';
import { CustomerOrderService } from '../../../my-account/services/customer.order.service';
import { QuotationService } from '../../../my-account/services/quotation.service';
import { AffaireType, individual, notIndividual } from '../../model/AffaireType';
import { IQuotation } from '../../model/IQuotation';
import { order, quotation, QuotationType } from '../../model/QuotationType';
import { ServiceFamilyGroup } from '../../model/ServiceFamilyGroup';
import { ServiceFamilyGroupService } from '../../services/service.family.group.service';

@Component({
  selector: 'app-identification',
  templateUrl: './identification.component.html',
  styleUrls: ['./identification.component.css']
})
export class IdentificationComponent implements OnInit {

  selectedQuotationType: QuotationType = quotation;
  familyGroupService: ServiceFamilyGroup[] = [];
  @Input() selectedFamilyGroupService: ServiceFamilyGroup | undefined;
  @Input() hideFamilyGroupServiceSelection: boolean = false;

  quotation: IQuotation = {} as IQuotation;
  isRegisteredAffaire: Boolean[] = [];
  affaireTypes: AffaireType[] = [];
  currentOpenedPanel: number = 0;

  siretSearched: string = "";
  debounce: any;
  loadingSiretSearch: boolean = false;

  notIndividual: AffaireType = notIndividual;
  individual: AffaireType = individual;

  constructor(private formBuilder: FormBuilder,
    private serviceFamilyGroupService: ServiceFamilyGroupService,
    private affaireService: AffaireService,
    private constantService: ConstantService,
    private quotationService: QuotationService,
    private orderService: CustomerOrderService,
  ) { }

  idenficationForm = this.formBuilder.group({});

  ngOnInit() {
    this.serviceFamilyGroupService.getServiceFamilyGroups().subscribe(response => this.familyGroupService = response);
    if (this.selectedFamilyGroupService)
      this.initIQuotation();
  }

  selectFamilyGroupService(item: ServiceFamilyGroup) {
    this.selectedFamilyGroupService = item;
    this.initIQuotation();
  }

  initIQuotation() {
    if (this.quotationService.getCurrentDraftQuotation()) {
      this.quotationService.getQuotation(parseInt(this.quotationService.getCurrentDraftQuotation()!)).subscribe(response => this.quotation = response);
    } else if (this.orderService.getCurrentDraftOrder()) {
      this.orderService.getCustomerOrder(parseInt(this.quotationService.getCurrentDraftQuotation()!)).subscribe(response => this.quotation = response);
    } else {
      this.quotation.assoAffaireOrders = [];
      this.addAffaire();
    }
  }

  addAffaire() {
    if (this.quotation && this.quotation.assoAffaireOrders)
      this.quotation.assoAffaireOrders.push({ affaire: { isIndividual: false, country: this.constantService.getCountryFrance() } as Affaire } as AssoAffaireOrder);
    this.isRegisteredAffaire[this.quotation.assoAffaireOrders.length - 1] = true;
    this.affaireTypes[this.quotation.assoAffaireOrders.length - 1] = notIndividual;
    this.currentOpenedPanel = this.quotation.assoAffaireOrders.length - 1;
  }

  openPanel(index: number) {
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
    if (this.selectedQuotationType)
      if (this.selectedQuotationType.id == quotation.id) {
        this.quotationService.saveQuotation(this.quotation).subscribe(response => {
          this.quotation = response;
          this.quotationService.setCurrentDraftQuotationId(this.quotation.id);
        })
      } else if (this.selectedQuotationType.id == order.id) {
        this.orderService.saveOrder(this.quotation).subscribe(response => {
          this.quotation = response;
          this.orderService.setCurrentDraftOrderId(this.quotation.id);
        })
      }
  }

}
