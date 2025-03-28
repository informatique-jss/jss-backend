import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { validateSiret } from '../../../../libs/CustomFormsValidatorsHelper';
import { Affaire } from '../../../my-account/model/Affaire';
import { AssoAffaireOrder } from '../../../my-account/model/AssoAffaireOrder';
import { AffaireService } from '../../../my-account/services/affaire.service';
import { IQuotation } from '../../model/IQuotation';
import { quotation, QuotationType } from '../../model/QuotationType';
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
  currentOpenedPanel: number = 0;

  siretSearched: string = "";
  debounce: any;
  loadingSiretSearch: boolean = false;

  constructor(private formBuilder: FormBuilder,
    private serviceFamilyGroupService: ServiceFamilyGroupService,
    private affaireService: AffaireService,
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
    this.quotation.assoAffaireOrders = [];
    this.addAffaire();
  }

  addAffaire() {
    if (this.quotation && this.quotation.assoAffaireOrders)
      this.quotation.assoAffaireOrders.push({ affaire: { isIndividual: false } as Affaire } as AssoAffaireOrder);
    this.isRegisteredAffaire[this.quotation.assoAffaireOrders.length - 1] = true;
    this.currentOpenedPanel = this.quotation.assoAffaireOrders.length - 1;
    console.log(this.currentOpenedPanel);
  }

  openPanel(index: number) {
    this.currentOpenedPanel = index;
  }

  deleteAffaire(indexAsso: number) {
    this.quotation.assoAffaireOrders.splice(indexAsso, 1);
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

}
