import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { instanceOfCustomerOrder } from 'src/app/libs/TypeHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Civility } from 'src/app/modules/miscellaneous/model/Civility';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { CivilityService } from 'src/app/modules/miscellaneous/services/civility.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { DOMICILIATION_ENTITY_TYPE, PROVISION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { BuildingDomiciliation } from '../../model/BuildingDomiciliation';
import { Domiciliation } from '../../model/Domiciliation';
import { DomiciliationContractType } from '../../model/DomiciliationContractType';
import { IQuotation } from '../../model/IQuotation';
import { MailRedirectionType } from '../../model/MailRedirectionType';
import { Provision } from '../../model/Provision';
import { BuildingDomiciliationService } from '../../services/building.domiciliation.service';
import { DomiciliationContractTypeService } from '../../services/domiciliation.contract.type.service';
import { MailRedirectionTypeService } from '../../services/mail.redirection.type.service';

@Component({
  selector: 'domiciliation',
  templateUrl: './domiciliation.component.html',
  styleUrls: ['./domiciliation.component.css']
})
export class DomiciliationComponent implements OnInit {


  @Input() domiciliation: Domiciliation = {} as Domiciliation;
  @Input() provision: Provision | undefined;;
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Input() editMode: boolean = false;
  @Input() quotation: IQuotation | undefined;

  DOMICILIATION_ENTITY_TYPE = DOMICILIATION_ENTITY_TYPE;
  PROVISION_ENTITY_TYPE = PROVISION_ENTITY_TYPE;
  instanceOfCustomerOrderFn = instanceOfCustomerOrder;
  attachmentTypeKbis = this.constantService.getAttachmentTypeKbis();
  attachmentTypeCni = this.constantService.getAttachmentTypeCni();
  attachmentProofOfAddress = this.constantService.getAttachmentTypeProofOfAddress();

  buildingDomiciliations: BuildingDomiciliation[] = [] as Array<BuildingDomiciliation>;
  mailRedirectionTypes: MailRedirectionType[] = [] as Array<MailRedirectionType>;
  contractTypes: DomiciliationContractType[] = [] as Array<DomiciliationContractType>;

  civilities: Civility[] = [] as Array<Civility>;

  mailRedirectionTypeOther: MailRedirectionType = this.constantService.getMailRedirectionTypeOther();
  domiciliationContractTypeKeepMail: DomiciliationContractType = this.constantService.getDomiciliationContractTypeKeepMail();
  domiciliationContractTypeRouteMail: DomiciliationContractType = this.constantService.getDomiciliationContractTypeRouteMail();
  domiciliationContractTypeRouteEmail: DomiciliationContractType = this.constantService.getDomiciliationContractTypeRouteEmail();
  domiciliationContractTypeRouteEmailAndMail: DomiciliationContractType = this.constantService.getDomiciliationContractTypeRouteEmailAndMail();


  constructor(private formBuilder: UntypedFormBuilder,
    protected domiciliationContractTypeService: DomiciliationContractTypeService,
    private buildingDomiciliationService: BuildingDomiciliationService,
    private cityService: CityService,
    private constantService: ConstantService,
    private mailRedirectionTypeService: MailRedirectionTypeService,
    protected civilityService: CivilityService,
    private userPreferenceService: UserPreferenceService
  ) { }

  ngOnInit() {
    this.buildingDomiciliationService.getBuildingDomiciliations().subscribe(response => {
      this.buildingDomiciliations = response;
      if (this.domiciliation! != null && this.domiciliation!.buildingDomiciliation == undefined || this.domiciliation!.buildingDomiciliation == null)
        this.domiciliation!.buildingDomiciliation = this.buildingDomiciliations[0];
    })
    this.domiciliationContractTypeService.getContractTypes().subscribe(response => {
      this.contractTypes = response;
      if (this.domiciliation && (this.domiciliation.domiciliationContractType == null || this.domiciliation.domiciliationContractType == undefined))
        this.domiciliation.domiciliationContractType = this.contractTypes[0];
    })
    this.mailRedirectionTypeService.getMailRedirectionTypes().subscribe(response => {
      this.mailRedirectionTypes = response;
      if (this.domiciliation! != null && this.domiciliation!.mailRedirectionType == undefined || this.domiciliation!.mailRedirectionType == null)
        this.domiciliation!.mailRedirectionType = this.mailRedirectionTypes[0];
    })
    this.civilityService.getCivilities().subscribe(response => {
      this.civilities = response;
      if (this.domiciliation! != null && this.domiciliation!.legalGardianCivility == undefined)
        this.domiciliation!.legalGardianCivility = this.civilities[0];
    })
    this.restoreTab();
  }

  getCurrentDate(): Date {
    return new Date();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.domiciliation != undefined) {
      if (this.domiciliation! == undefined || this.domiciliation! == null)
        this.domiciliation! = {} as Domiciliation;
      if (this.domiciliation! != null && this.domiciliation!.mailRedirectionType == undefined || this.domiciliation!.mailRedirectionType == null)
        this.domiciliation!.mailRedirectionType = this.mailRedirectionTypes[0];
      if (this.domiciliation! != null && this.domiciliation!.buildingDomiciliation == undefined || this.domiciliation!.buildingDomiciliation == null)
        this.domiciliation!.buildingDomiciliation = this.buildingDomiciliations[0];
      if (this.domiciliation! != null && this.domiciliation!.legalGardianBirthdate != null && this.domiciliation!.legalGardianBirthdate != undefined)
        this.domiciliation!.legalGardianBirthdate = new Date(this.domiciliation!.legalGardianBirthdate);
      if (this.domiciliation!.isLegalPerson == null || this.domiciliation!.isLegalPerson == undefined)
        this.domiciliation!.isLegalPerson = false;
      if (this.domiciliation && (this.domiciliation.domiciliationContractType == null || this.domiciliation.domiciliationContractType == undefined))
        this.domiciliation.domiciliationContractType = this.contractTypes[0];
      this.domiciliationForm.markAllAsTouched();
    }
  }

  domiciliationForm = this.formBuilder.group({
  });

  getFormStatus(): boolean {
    this.domiciliationForm.markAllAsTouched();
    if (this.domiciliation.legalGardianBirthdate)
      this.domiciliation.legalGardianBirthdate = new Date(this.domiciliation.legalGardianBirthdate.setHours(12));
    return this.domiciliationForm.valid;
  }

  mustDecribeAdresse(): boolean {
    return this.domiciliation != null && this.domiciliation.domiciliationContractType &&
      (this.domiciliation.domiciliationContractType.id == this.domiciliationContractTypeRouteEmailAndMail.id
        || this.domiciliation.domiciliationContractType.id == this.domiciliationContractTypeRouteMail.id)
      && this.domiciliation.mailRedirectionType && this.domiciliation.mailRedirectionType.id == this.mailRedirectionTypeOther.id;
  }

  mustDecribeActivityMail() {
    return this.domiciliation != null && this.domiciliation.domiciliationContractType &&
      (this.domiciliation.domiciliationContractType.id == this.domiciliationContractTypeRouteEmail.id
        || this.domiciliation.domiciliationContractType.id == this.domiciliationContractTypeRouteEmailAndMail.id)
      && this.domiciliation.mailRedirectionType && this.domiciliation!.mailRedirectionType.id == this.constantService.getMailRedirectionTypeActivity().id;
  }

  mustDecribeMail() {
    return this.domiciliation != null && this.domiciliation.domiciliationContractType &&
      (this.domiciliation.domiciliationContractType.id == this.domiciliationContractTypeRouteEmail.id
        || this.domiciliation.domiciliationContractType.id == this.domiciliationContractTypeRouteEmailAndMail.id)
      && this.domiciliation.mailRedirectionType && this.domiciliation!.mailRedirectionType.id == this.constantService.getMailRedirectionTypeOther().id;
  }

  fillPostalCode(city: City) {
    if (this.domiciliation! != null) {
      if (this.domiciliation!.country == null || this.domiciliation!.country == undefined)
        this.domiciliation!.country = city.country;

      if (this.domiciliation!.country.id == this.constantService.getCountryFrance().id && city.postalCode != null && !this.domiciliation!.postalCode)
        this.domiciliation!.postalCode = city.postalCode;
    }
  }

  fillCity(postalCode: string) {
    if (this.domiciliation! != null) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.domiciliation! != null) {
            if (this.domiciliation!.country == null || this.domiciliation!.country == undefined)
              this.domiciliation!.country = city.country;

            this.domiciliation!.city = city;
          }
        }
      })
    }
  }

  fillActivityPostalCode(city: City) {
    if (this.domiciliation! != null) {
      if (this.domiciliation!.activityCountry == null || this.domiciliation!.activityCountry == undefined)
        this.domiciliation!.activityCountry = city.country;

      if (this.domiciliation!.activityCountry.id == this.constantService.getCountryFrance().id && city.postalCode != null)
        this.domiciliation!.activityPostalCode = city.postalCode;
    }
  }

  fillActivityCity(postalCode: string) {
    if (this.domiciliation! != null) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.domiciliation! != null) {
            if (this.domiciliation!.activityCountry == null || this.domiciliation!.activityCountry == undefined)
              this.domiciliation!.activityCountry = city.country;

            this.domiciliation!.activityCity = city;
          }
        }
      })
    }
  }

  fillLegalGardianPostalCode(city: City) {
    if (this.domiciliation! != null) {
      if (this.domiciliation!.legalGardianCountry == null || this.domiciliation!.legalGardianCountry == undefined)
        this.domiciliation!.legalGardianCountry = city.country;

      if (this.domiciliation!.legalGardianCountry.id == this.constantService.getCountryFrance().id && city.postalCode != null)
        this.domiciliation!.legalGardianPostalCode = city.postalCode;
    }
  }

  fillLegalGardianCity(postalCode: string) {
    if (this.domiciliation! != null) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.domiciliation! != null) {
            if (this.domiciliation!.legalGardianCountry == null || this.domiciliation!.legalGardianCountry == undefined)
              this.domiciliation!.legalGardianCountry = city.country;

            this.domiciliation!.legalGardianCity = city;
          }
        }
      })
    }
  }

  limitTextareaSize(numberOfLine: number) {
    if (this.domiciliation?.mailRecipient != null) {
      var l = this.domiciliation?.mailRecipient.replace(/\r\n/g, "\n").replace(/\r/g, "").split(/\n/g);//split lines
      var outValue = "";
      if (l.length > numberOfLine) {
        outValue = l.slice(0, numberOfLine).join("\n");
        this.domiciliation.mailRecipient = outValue;
      }
    }
  }

  limitTextareaSizeActivityMailRecipient(numberOfLine: number) {
    if (this.domiciliation?.activityMailRecipient != null) {
      var l = this.domiciliation?.activityMailRecipient.replace(/\r\n/g, "\n").replace(/\r/g, "").split(/\n/g);//split lines
      var outValue = "";
      if (l.length > numberOfLine) {
        outValue = l.slice(0, numberOfLine).join("\n");
        this.domiciliation.activityMailRecipient = outValue;
      }
    }
  }

  limitTextareaSizeLegalGardianMailRecipient(numberOfLine: number) {
    if (this.domiciliation?.legalGardianMailRecipient != null) {
      var l = this.domiciliation?.legalGardianMailRecipient.replace(/\r\n/g, "\n").replace(/\r/g, "").split(/\n/g);//split lines
      var outValue = "";
      if (l.length > numberOfLine) {
        outValue = l.slice(0, numberOfLine).join("\n");
        this.domiciliation.legalGardianMailRecipient = outValue;
      }
    }
  }

  provisionChangeFunction() {
    this.provisionChange.emit(this.provision);
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('domiciliation', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('domiciliation');
  }

}
