import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ConstantService } from '../../../../libs/constant.service';
import { validateSiret } from '../../../../libs/CustomFormsValidatorsHelper';
import { Affaire } from '../../../my-account/model/Affaire';
import { ServiceType } from '../../../my-account/model/ServiceType';
import { AffaireService } from '../../../my-account/services/affaire.service';
import { City } from '../../../profile/model/City';
import { Country } from '../../../profile/model/Country';
import { ServiceFamily } from '../../model/ServiceFamily';
import { ServiceFamilyGroup } from '../../model/ServiceFamilyGroup';
import { ServiceTypeChosen } from '../../model/ServiceTypeChosen';
import { CityService } from '../../services/city.service';
import { CountryService } from '../../services/country.service';
import { ServiceFamilyGroupService } from '../../services/service.family.group.service';
import { ServiceFamilyService } from '../../services/service.family.service';
import { ServiceTypeService } from '../../services/service.type.service';

@Component({
  selector: 'choose-service',
  templateUrl: './choose-service.component.html',
  styleUrls: ['./choose-service.component.css']
})
export class ChooseServiceComponent implements OnInit {

  @Output() onChoseService = new EventEmitter<ServiceTypeChosen>();
  @Input() currentOrderAffaires: Affaire[] | undefined;
  service: ServiceType = {} as ServiceType;
  currentStep: number = 1;
  serviceFamilyGroupChosen: ServiceFamilyGroup | undefined;
  serviceFamilyChosen: ServiceFamily | undefined;
  serviceTypeChosen: ServiceType | undefined;

  serviceFamilyGroups: ServiceFamilyGroup[] | undefined;
  serviceFamilies: ServiceFamily[] | undefined;
  serviceTypes: ServiceType[] | undefined;
  filteredServiceTypes: ServiceType[] | undefined;

  searchService: string = "";
  siretSearched: string = "";
  affaire: Affaire = { isIndividual: false } as Affaire;
  loadingAffaireSearch: boolean = false;
  checkedOnce = false;

  @ViewChild('step2') step2: ElementRef | undefined;
  @ViewChild('step3') step3: ElementRef | undefined;
  @ViewChild('step4') step4: ElementRef | undefined;
  @ViewChild('step5') step5: ElementRef | undefined;
  @ViewChild('genericInput') genericInput: ElementRef | undefined;

  countryFrance: Country = this.constantService.getCountryFrance();
  countries: Country[] | undefined;
  foundCities: City[] | undefined;

  debounce: any;

  constructor(
    private serviceFamilyGroupService: ServiceFamilyGroupService,
    private serviceFamilyService: ServiceFamilyService,
    private serviceTypeService: ServiceTypeService,
    private formBuilder: FormBuilder,
    private affaireService: AffaireService,
    private constantService: ConstantService,
    private countryService: CountryService,
    private cityService: CityService,
  ) { }

  addServiceForm = this.formBuilder.group({});

  ngOnInit() {
    this.serviceFamilyGroupService.getServiceFamilyGroups().subscribe(response => {
      this.serviceFamilyGroups = response;
      this.choseFamilyGroup(response[2]); //debug
    })
    this.countryService.getCountries().subscribe(response => {
      this.countries = response.sort((a: Country, b: Country) => a.label.localeCompare(b.label));
      for (let country of this.countries)
        if (country.id == this.countryFrance.id)
          this.affaire.country = country;
      this.affaireService.getAffaire(4364468).subscribe(affaire => { this.affaire = affaire }); //debug
      this.validateAffaire(); //debug;
    })
  }

  validateService() {
    if (this.serviceTypeChosen) {
      if (!this.affaire.id) {
        this.checkedOnce = true;
        if (this.affaire.isIndividual) {
          if (!this.affaire.firstname || !this.affaire.lastname)
            return;
        } else if (!this.affaire.denomination)
          return;
        if (!this.affaire.city || !this.affaire.country)
          return;
        if (this.affaire.country.id == this.countryFrance.id && (!this.affaire.postalCode || this.affaire.postalCode.length < 4))
          return;
      }
    }
    this.onChoseService.emit({ affaire: this.affaire, service: this.serviceTypeChosen } as ServiceTypeChosen);
  }

  searchServices() {
    if (this.serviceTypes)
      this.filteredServiceTypes = this.serviceTypes.filter(service => JSON.stringify(service).toLowerCase().indexOf(this.searchService) >= 0);
  }

  choseFamilyGroup(serviceFamilyGroup: ServiceFamilyGroup) {
    this.serviceFamilyGroupChosen = serviceFamilyGroup;
    this.serviceFamilies = undefined;
    if (this.serviceFamilyGroupChosen) {
      this.currentStep++;
      this.serviceFamilyService.getServiceFamiliesForFamilyGroup(this.serviceFamilyGroupChosen.id).subscribe(response => {
        this.serviceFamilies = response;
        if (this.step2)
          setTimeout(this.step2.nativeElement.scrollIntoView({ behavior: 'smooth' }), 0);
        this.choseFamily(this.serviceFamilies[1]); //debug
      })
    }
  }

  choseFamily(serviceFamily: ServiceFamily) {
    this.serviceFamilyChosen = serviceFamily;
    this.serviceTypes = undefined;
    if (this.serviceFamilyChosen) {
      this.currentStep++;
      this.serviceTypeService.getServiceTypesForFamily(this.serviceFamilyChosen.id).subscribe(response => {
        this.serviceTypes = response;
        this.searchServices();
        if (this.step3)
          setTimeout(this.step3.nativeElement.scrollIntoView({ behavior: 'smooth' }), 200);
        if (this.genericInput)
          setTimeout(this.genericInput.nativeElement.focus(), 200);
        this.choseServiceType(this.serviceTypes[1]); //debug
      })
    }
  }

  choseServiceType(serviceType: ServiceType) {
    this.serviceTypeChosen = serviceType;
    if (this.serviceTypeChosen) {
      this.service = this.serviceTypeChosen;
      this.currentStep++;
      if (this.step4)
        setTimeout(this.step4.nativeElement.scrollIntoView({ behavior: 'smooth' }), 200);
    }
  }


  searchSiret() {
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.effectiveSearchSiret();
    }, 500);
  }

  effectiveSearchSiret() {
    if (this.siretSearched && validateSiret(this.siretSearched)) {
      this.loadingAffaireSearch = true;
      this.affaireService.getAffaireBySiret(this.siretSearched).subscribe(response => {
        this.loadingAffaireSearch = false;
        if (response && response.length == 1)
          this.affaire = response[0];
        if (this.affaire && this.affaire.country && this.countries)
          for (let country of this.countries)
            if (country.id == this.affaire.country.id) {
              this.affaire.country = country;
              break;
            }
      })
    }
  }

  fetchCities() {
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.effectiveFetchCities();
    }, 500);
  }

  effectiveFetchCities() {
    this.foundCities = undefined;
    if (this.affaire)
      if (this.affaire.country && this.affaire.country.id) {
        if (this.affaire.country.id != this.countryFrance.id) {
          this.cityService.getCitiesByCountry(this.affaire.country).subscribe(response => {
            this.foundCities = response;
          })
        } else if (this.affaire.postalCode && this.affaire.postalCode.length > 4) {
          this.cityService.getCitiesFilteredByCountryAndNameAndPostalCode(this.affaire.country, this.affaire.postalCode).subscribe(response => {
            this.foundCities = response;
            if (this.foundCities.length == 1)
              this.affaire.city = this.foundCities[0];
          })
        }
      }
  }

  validateAffaire() {
    this.currentStep++;
    if (this.step5)
      setTimeout(this.step5.nativeElement.scrollIntoView({ behavior: 'smooth' }), 200);
  }

  isAnnouncementValid() {

  }

}
