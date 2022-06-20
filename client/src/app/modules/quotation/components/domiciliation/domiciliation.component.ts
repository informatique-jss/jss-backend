import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { CNI_ATTACHMENT_TYPE_CODE, COUNTRY_CODE_FRANCE, DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_1_CODE, DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_2_CODE, DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_1_CODE, DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_2_CODE, DOMICILIATION_MAIL_REDIRECTION_TYPE_OTHER_CODE, KBIS_ATTACHMENT_TYPE_CODE, PROOF_OF_ADDRESS_ATTACHMENT_TYPE_CODE } from 'src/app/libs/Constants';
import { validateSiren } from 'src/app/libs/CustomFormsValidatorsHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Civility } from 'src/app/modules/miscellaneous/model/Civility';
import { Language } from 'src/app/modules/miscellaneous/model/Language';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { CivilityService } from 'src/app/modules/miscellaneous/services/civility.service';
import { LanguageService } from 'src/app/modules/miscellaneous/services/language.service';
import { DOMICILIATION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { BuildingDomiciliation } from '../../model/BuildingDomiciliation';
import { Domiciliation } from '../../model/Domiciliation';
import { DomiciliationContractType } from '../../model/DomiciliationContractType';
import { MailRedirectionType } from '../../model/MailRedirectionType';
import { Provision } from '../../model/Provision';
import { Siren } from '../../model/Siren';
import { BuildingDomiciliationService } from '../../services/building.domiciliation.service';
import { DomiciliationContractTypeService } from '../../services/domiciliation.contract.type.service';
import { MailRedirectionTypeService } from '../../services/mail.redirection.type.service';

@Component({
  selector: 'domiciliation',
  templateUrl: './domiciliation.component.html',
  styleUrls: ['./domiciliation.component.css']
})
export class DomiciliationComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() provision: Provision = {} as Provision;
  @Input() editMode: boolean = false;

  @ViewChild('tabs', { static: false }) tabs: any;

  DOMICILIATION_ENTITY_TYPE = DOMICILIATION_ENTITY_TYPE;
  KBIS_ATTACHMENT_TYPE_CODE = KBIS_ATTACHMENT_TYPE_CODE;
  CNI_ATTACHMENT_TYPE_CODE = CNI_ATTACHMENT_TYPE_CODE;
  PROOF_OF_ADDRESS_ATTACHMENT_TYPE_CODE = PROOF_OF_ADDRESS_ATTACHMENT_TYPE_CODE;

  languages: Language[] = [] as Array<Language>;
  buildingDomiciliations: BuildingDomiciliation[] = [] as Array<BuildingDomiciliation>;
  mailRedirectionTypes: MailRedirectionType[] = [] as Array<MailRedirectionType>;
  contractTypes: DomiciliationContractType[] = [] as Array<DomiciliationContractType>;

  civilities: Civility[] = [] as Array<Civility>;

  DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_1_CODE = DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_1_CODE;
  DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_2_CODE = DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_2_CODE;
  DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_1_CODE = DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_1_CODE;
  DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_2_CODE = DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_2_CODE;
  DOMICILIATION_MAIL_REDIRECTION_TYPE_OTHER_CODE = DOMICILIATION_MAIL_REDIRECTION_TYPE_OTHER_CODE;


  constructor(private formBuilder: FormBuilder,
    protected domiciliationContractTypeService: DomiciliationContractTypeService,
    protected languageService: LanguageService,
    private buildingDomiciliationService: BuildingDomiciliationService,
    private cityService: CityService,
    private mailRedirectionTypeService: MailRedirectionTypeService,
    protected civilityService: CivilityService,
  ) { }

  ngOnInit() {
    this.languageService.getLanguages().subscribe(response => {
      this.languages = response;
      if (this.provision.domiciliation! != null && this.provision.domiciliation!.language == undefined || this.provision.domiciliation!.language == null)
        this.provision.domiciliation!.language = this.languages[0];
    })
    this.buildingDomiciliationService.getBuildingDomiciliations().subscribe(response => {
      this.buildingDomiciliations = response;
      if (this.provision.domiciliation! != null && this.provision.domiciliation!.buildingDomiciliation == undefined || this.provision.domiciliation!.buildingDomiciliation == null)
        this.provision.domiciliation!.buildingDomiciliation = this.buildingDomiciliations[0];
    })
    this.domiciliationContractTypeService.getContractTypes().subscribe(response => {
      this.contractTypes = response;
      if (this.provision && this.provision.domiciliation && (this.provision.domiciliation.domiciliationContractType == null || this.provision.domiciliation.domiciliationContractType == undefined))
        this.provision.domiciliation.domiciliationContractType = this.contractTypes[0];
    })
    this.mailRedirectionTypeService.getMailRedirectionTypes().subscribe(response => {
      this.mailRedirectionTypes = response;
      if (this.provision.domiciliation! != null && this.provision.domiciliation!.mailRedirectionType == undefined || this.provision.domiciliation!.mailRedirectionType == null)
        this.provision.domiciliation!.mailRedirectionType = this.mailRedirectionTypes[0];
    })
    this.civilityService.getCivilities().subscribe(response => {
      this.civilities = response;
      if (this.provision.domiciliation! != null && this.provision.domiciliation!.legalGardianCivility == undefined)
        this.provision.domiciliation!.legalGardianCivility = this.civilities[0];
    })


  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.provision != undefined) {
      if (this.provision.domiciliation! == undefined || this.provision.domiciliation! == null)
        this.provision.domiciliation! = {} as Domiciliation;
      if (this.provision.domiciliation!.language == undefined || this.provision.domiciliation!.language == null)
        this.provision.domiciliation!.language = this.languages[0];
      if (this.provision.domiciliation! != null && this.provision.domiciliation!.mailRedirectionType == undefined || this.provision.domiciliation!.mailRedirectionType == null)
        this.provision.domiciliation!.mailRedirectionType = this.mailRedirectionTypes[0];
      if (this.provision.domiciliation! != null && this.provision.domiciliation!.buildingDomiciliation == undefined || this.provision.domiciliation!.buildingDomiciliation == null)
        this.provision.domiciliation!.buildingDomiciliation = this.buildingDomiciliations[0];
      if (this.provision.domiciliation! != null && this.provision.domiciliation!.startDate != null && this.provision.domiciliation!.startDate != undefined)
        this.provision.domiciliation!.startDate = new Date(this.provision.domiciliation!.startDate);
      if (this.provision.domiciliation! != null && this.provision.domiciliation!.legalGardianBirthdate != null && this.provision.domiciliation!.legalGardianBirthdate != undefined)
        this.provision.domiciliation!.legalGardianBirthdate = new Date(this.provision.domiciliation!.legalGardianBirthdate);
      if (this.provision.domiciliation!.isLegalPerson == null || this.provision.domiciliation!.isLegalPerson == undefined)
        this.provision.domiciliation!.isLegalPerson = false;
      if (this.provision && this.provision.domiciliation && (this.provision.domiciliation.domiciliationContractType == null || this.provision.domiciliation.domiciliationContractType == undefined))
        this.provision.domiciliation.domiciliationContractType = this.contractTypes[0];
      this.domiciliationForm.markAllAsTouched();
      this.toggleTabs();
    }
  }

  domiciliationForm = this.formBuilder.group({
    domiciliationContractType: ['', []],
    buildingDomiciliation: ['', []],
    mailRedirectionType: ['', []],
    activityCity: ['', []],
    activityPostalCode: ['', []],
    legalGardianCity: ['', []],
    legalGardianSiren: ['', []],
    legalGardianPostalCode: ['', []],
  });

  getFormStatus(): boolean {
    this.domiciliationForm.markAllAsTouched();
    return this.domiciliationForm.valid;
  }

  toggleTabs() {
    if (this.tabs != undefined)
      this.tabs.realignInkBar();
  }

  mustDecribeAdresse(): boolean {
    return this.provision.domiciliation != null && this.provision.domiciliation.domiciliationContractType &&
      (this.provision.domiciliation.domiciliationContractType.code == DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_1_CODE
        || this.provision.domiciliation.domiciliationContractType.code == DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_2_CODE)
      && this.provision.domiciliation.mailRedirectionType && this.provision.domiciliation.mailRedirectionType.code == this.DOMICILIATION_MAIL_REDIRECTION_TYPE_OTHER_CODE;
  }

  mustDecribeMail() {
    return this.provision.domiciliation != null && this.provision.domiciliation.domiciliationContractType &&
      (this.provision.domiciliation.domiciliationContractType.code == DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_1_CODE
        || this.provision.domiciliation.domiciliationContractType.code == DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_2_CODE)
      && this.provision.domiciliation.mailRedirectionType && this.provision.domiciliation!.mailRedirectionType.code == this.DOMICILIATION_MAIL_REDIRECTION_TYPE_OTHER_CODE;
  }

  fillPostalCode(city: City) {
    if (this.provision.domiciliation! != null) {
      if (this.provision.domiciliation!.country == null || this.provision.domiciliation!.country == undefined)
        this.provision.domiciliation!.country = city.country;

      if (this.provision.domiciliation!.country.code == COUNTRY_CODE_FRANCE && city.postalCode != null)
        this.provision.domiciliation!.postalCode = city.postalCode;
    }
  }

  fillCity(postalCode: string) {
    if (this.provision.domiciliation! != null) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.provision.domiciliation! != null) {
            if (this.provision.domiciliation!.country == null || this.provision.domiciliation!.country == undefined)
              this.provision.domiciliation!.country = city.country;

            this.provision.domiciliation!.city = city;
          }
        }
      })
    }
  }

  fillActivityPostalCode(city: City) {
    if (this.provision.domiciliation! != null) {
      if (this.provision.domiciliation!.activityCountry == null || this.provision.domiciliation!.activityCountry == undefined)
        this.provision.domiciliation!.activityCountry = city.country;

      if (this.provision.domiciliation!.activityCountry.code == COUNTRY_CODE_FRANCE && city.postalCode != null)
        this.provision.domiciliation!.activityPostalCode = city.postalCode;
    }
  }

  fillActivityCity(postalCode: string) {
    if (this.provision.domiciliation! != null) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.provision.domiciliation! != null) {
            if (this.provision.domiciliation!.activityCountry == null || this.provision.domiciliation!.activityCountry == undefined)
              this.provision.domiciliation!.activityCountry = city.country;

            this.provision.domiciliation!.activityCity = city;
          }
        }
      })
    }
  }

  fillLegalGardianPostalCode(city: City) {
    if (this.provision.domiciliation! != null) {
      if (this.provision.domiciliation!.legalGardianCountry == null || this.provision.domiciliation!.legalGardianCountry == undefined)
        this.provision.domiciliation!.legalGardianCountry = city.country;

      if (this.provision.domiciliation!.legalGardianCountry.code == COUNTRY_CODE_FRANCE && city.postalCode != null)
        this.provision.domiciliation!.legalGardianPostalCode = city.postalCode;
    }
  }

  fillLegalGardianCity(postalCode: string) {
    if (this.provision.domiciliation! != null) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.provision.domiciliation! != null) {
            if (this.provision.domiciliation!.legalGardianCountry == null || this.provision.domiciliation!.legalGardianCountry == undefined)
              this.provision.domiciliation!.legalGardianCountry = city.country;

            this.provision.domiciliation!.legalGardianCity = city;
          }
        }
      })
    }
  }

  limitTextareaSize(numberOfLine: number) {
    if (this.provision.domiciliation?.mailRecipient != null) {
      var l = this.provision.domiciliation?.mailRecipient.replace(/\r\n/g, "\n").replace(/\r/g, "").split(/\n/g);//split lines
      var outValue = "";
      if (l.length > numberOfLine) {
        outValue = l.slice(0, numberOfLine).join("\n");
        this.provision.domiciliation.mailRecipient = outValue;
      }
    }
  }

  limitTextareaSizeActivityMailRecipient(numberOfLine: number) {
    if (this.provision.domiciliation?.activityMailRecipient != null) {
      var l = this.provision.domiciliation?.activityMailRecipient.replace(/\r\n/g, "\n").replace(/\r/g, "").split(/\n/g);//split lines
      var outValue = "";
      if (l.length > numberOfLine) {
        outValue = l.slice(0, numberOfLine).join("\n");
        this.provision.domiciliation.activityMailRecipient = outValue;
      }
    }
  }

  limitTextareaSizeLegalGardianMailRecipient(numberOfLine: number) {
    if (this.provision.domiciliation?.legalGardianMailRecipient != null) {
      var l = this.provision.domiciliation?.legalGardianMailRecipient.replace(/\r\n/g, "\n").replace(/\r/g, "").split(/\n/g);//split lines
      var outValue = "";
      if (l.length > numberOfLine) {
        outValue = l.slice(0, numberOfLine).join("\n");
        this.provision.domiciliation.legalGardianMailRecipient = outValue;
      }
    }
  }

  checkSiren(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (this.provision.domiciliation! != undefined && this.provision.domiciliation!.isLegalPerson && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0 || !validateSiren(fieldValue)))
        return {
          notFilled: true
        };
      return null;
    };
  }

  fillSiren(siren: Siren) {
    if (siren != undefined && siren != null) {
      this.provision.domiciliation!.legalGardianSiren = siren!.uniteLegale.siren;
      if (siren!.uniteLegale.siren != undefined && siren!.uniteLegale.siren != null) {
        if (siren.uniteLegale.periodesUniteLegale != null && siren.uniteLegale.periodesUniteLegale != undefined && siren.uniteLegale.periodesUniteLegale.length > 0) {
          siren.uniteLegale.periodesUniteLegale.forEach(periode => {
            if (periode.dateFin == null)
              this.provision.domiciliation!.legalGardianDenomination = periode.denominationUniteLegale;
            this.domiciliationForm.markAllAsTouched();
          });
        }
      }
    }
  }
}
