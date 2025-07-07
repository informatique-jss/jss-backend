import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { validateSiren, validateSiret } from '../../../../libs/CustomFormsValidatorsHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { ConstantService } from '../../../main/services/constant.service';
import { AutocompleteCityComponent } from '../../../miscellaneous/components/forms/autocomplete-city/autocomplete-city.component';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { GenericToggleComponent } from '../../../miscellaneous/components/forms/generic-toggle/generic-toggle.component';
import { RadioGroupAffaireTypeComponent } from '../../../miscellaneous/components/forms/radio-group-affaire-type/radio-group-affaire-type.component';
import { RadioGroupQuotationTypeComponent } from '../../../miscellaneous/components/forms/radio-group-quotation-type/radio-group-quotation-type.component';
import { SelectCountryComponent } from '../../../miscellaneous/components/forms/select-country/select-country.component';
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
import { CityService } from '../../services/city.service';
import { ServiceFamilyGroupService } from '../../services/service.family.group.service';

@Component({
  selector: 'app-identification',
  templateUrl: './identification.component.html',
  styleUrls: ['./identification.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS,
    GenericInputComponent,
    RadioGroupQuotationTypeComponent,
    RadioGroupAffaireTypeComponent,
    SelectCountryComponent,
    GenericToggleComponent,
    AutocompleteCityComponent]
})
export class IdentificationComponent implements OnInit {

  selectedQuotationType: QuotationType = quotation;
  familyGroupService: ServiceFamilyGroup[] = [];

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

  currentDraftStep: string | null = null;
  idFamilyGroup: number | undefined;
  idQuotationType: number | undefined;

  constructor(private formBuilder: FormBuilder,
    private serviceFamilyGroupService: ServiceFamilyGroupService,
    private affaireService: AffaireService,
    private constantService: ConstantService,
    private quotationService: QuotationService,
    private orderService: CustomerOrderService,
    private loginService: LoginService,
    private appService: AppService,
    private cityService: CityService,
    private activatedRoute: ActivatedRoute
  ) { }

  idenficationForm!: FormGroup;

  async ngOnInit() {
    this.idenficationForm = this.formBuilder.group({
      quotationType: []
    });

    this.idFamilyGroup = this.activatedRoute.snapshot.params['idFamilyGroup'];
    this.idQuotationType = this.activatedRoute.snapshot.params['idQuotationType'];

    this.currentDraftStep = this.quotationService.getCurrentDraftQuotationStep();

    this.serviceFamilyGroupService.getServiceFamilyGroups().subscribe(response => {
      this.familyGroupService = response;
      this.loginService.getCurrentUser().subscribe(response => {
        this.currentUser = response;
        this.initIQuotation();
      })
    });
    this.initIQuotation();
  }

  selectFamilyGroupService(item: ServiceFamilyGroup) {
    if (this.quotation.serviceFamilyGroup) {
      this.quotation.serviceFamilyGroup = item;
      return;
    }
    if (!this.quotation)
      this.initIQuotation();
    this.quotation.serviceFamilyGroup = item;
    this.quotation.assoAffaireOrders = [];
    this.addAffaire();
  }

  initIQuotation() {
    if (this.idFamilyGroup && this.idQuotationType) {
      this.quotationService.cleanStorageData();
      this.selectedQuotationType = this.idQuotationType == order.id ? order : quotation;
      this.changeQuotationType();
      this.selectFamilyGroupService(this.familyGroupService.find(group => group.id == this.idFamilyGroup)!);
    }
    else {
      if (this.currentUser) {
        if (this.quotationService.getCurrentDraftQuotationId()) {
          this.quotationService.getQuotation(parseInt(this.quotationService.getCurrentDraftQuotationId()!)).subscribe(response => {
            this.quotation = response;
            this.refreshIsRegisteredAffaire();
            this.recomputeAffaireType();
          });
          return;
        } else if (this.orderService.getCurrentDraftOrderId()) {
          this.orderService.getCustomerOrder(parseInt(this.orderService.getCurrentDraftOrderId()!)).subscribe(response => {
            this.selectedQuotationType = order;
            this.quotation = response
            this.refreshIsRegisteredAffaire();
            this.recomputeAffaireType();
          });
          return;
        }
      } else {
        if (this.quotationService.getCurrentDraftQuotation()) {
          this.quotation = this.quotationService.getCurrentDraftQuotation()!;
          this.refreshIsRegisteredAffaire();
          this.recomputeAffaireType();
          return;
        } else if (this.orderService.getCurrentDraftOrder()) {
          this.selectedQuotationType = order;
          this.quotation = this.orderService.getCurrentDraftOrder()!;
          this.refreshIsRegisteredAffaire();
          this.recomputeAffaireType();
          return;
        }
      }
    }
  }

  changeQuotationType() {
    if (this.quotation) {
      if (this.selectedQuotationType.id == quotation.id)
        this.quotation.isQuotation = true;
      else {
        this.quotation.isQuotation = false;
      }
    }

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

  recomputeAffaireType() {
    if (this.quotation && this.quotation.assoAffaireOrders)
      for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++)
        if (this.quotation.assoAffaireOrders[i].affaire.isIndividual)
          this.affaireTypes[this.quotation.assoAffaireOrders.length - 1] = individual;
        else
          this.affaireTypes[this.quotation.assoAffaireOrders.length - 1] = notIndividual;
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
    this.currentOpenedPanel = undefined;
    if (!this.quotation.assoAffaireOrders || this.quotation.assoAffaireOrders.length == 0)
      this.quotation.serviceFamilyGroup = undefined;
  }

  searchSiret(indexAsso: number) {
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.effectiveSearchSiret(indexAsso);
    }, 500);
  }

  effectiveSearchSiret(indexAsso: number) {
    if (this.siretSearched && (validateSiret(this.siretSearched) || validateSiren(this.siretSearched))) {
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
        }
      }
      if (allAssoOk)
        return true;
    }
    return false;
  }

  startQuotation() {
    this.appService.showLoadingSpinner();
    if (this.selectedQuotationType)
      if (this.currentUser) {
        if (this.selectedQuotationType.id == quotation.id) {
          this.quotation.isQuotation = true;
          this.quotationService.saveQuotation(this.quotation, false).subscribe(response => {
            this.appService.hideLoadingSpinner();
            this.quotationService.setCurrentDraftQuotationId(response);
            this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[1]);
            this.appService.openRoute(undefined, "quotation/services-selection", undefined);
          })
        } else if (this.selectedQuotationType.id == order.id) {
          this.quotation.isQuotation = false;
          this.orderService.saveOrder(this.quotation, false).subscribe(response => {
            this.appService.hideLoadingSpinner();
            this.orderService.setCurrentDraftOrderId(response);
            this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[1]);
            this.appService.openRoute(undefined, "quotation/services-selection", undefined);
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
        this.appService.hideLoadingSpinner();
        this.quotationService.setCurrentDraftQuotationStep(this.appService.getAllQuotationMenuItems()[1]);
        this.appService.openRoute(undefined, "quotation/services-selection", undefined);
      }
  }

  findCity(indexAsso: number) {
    if (this.quotation && this.quotation.assoAffaireOrders[indexAsso].affaire.postalCode) {
      this.cityService.getCitiesByPostalCode(this.quotation.assoAffaireOrders[indexAsso].affaire.postalCode).subscribe(response => {
        if (response && response.length == 1)
          this.quotation.assoAffaireOrders[indexAsso].affaire.city = response[0];
      })
    }
  }
}
