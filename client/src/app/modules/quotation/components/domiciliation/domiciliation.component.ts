import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, map, startWith, switchMap, tap } from 'rxjs/operators';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { CNI_ATTACHMENT_TYPE_CODE, COUNTRY_CODE_FRANCE, DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_1_CODE, DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_2_CODE, DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_1_CODE, DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_2_CODE, DOMICILIATION_MAIL_REDIRECTION_TYPE_OTHER_CODE, KBIS_ATTACHMENT_TYPE_CODE, PROOF_OF_ADDRESS_ATTACHMENT_TYPE_CODE, SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { validateEmail, validateFrenchPhone, validateInternationalPhone, validateSiren } from 'src/app/libs/CustomFormsValidatorsHelper';
import { callNumber, prepareMail } from 'src/app/libs/MailHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Civility } from 'src/app/modules/miscellaneous/model/Civility';
import { Country } from 'src/app/modules/miscellaneous/model/Country';
import { Language } from 'src/app/modules/miscellaneous/model/Language';
import { LegalForm } from 'src/app/modules/miscellaneous/model/LegalForm';
import { Mail } from 'src/app/modules/miscellaneous/model/Mail';
import { Phone } from 'src/app/modules/miscellaneous/model/Phone';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { CivilityService } from 'src/app/modules/miscellaneous/services/civility.service';
import { CountryService } from 'src/app/modules/miscellaneous/services/country.service';
import { LanguageService } from 'src/app/modules/miscellaneous/services/language.service';
import { LegalFormService } from 'src/app/modules/miscellaneous/services/legal.form.service';
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
import { SirenService } from '../../services/siren.service';

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

  domiciliationContractTypes: DomiciliationContractType[] = [] as Array<DomiciliationContractType>;
  languages: Language[] = [] as Array<Language>;
  buildingDomiciliations: BuildingDomiciliation[] = [] as Array<BuildingDomiciliation>;
  mailRedirectionTypes: MailRedirectionType[] = [] as Array<MailRedirectionType>;

  filteredPostalCodes: String[] | undefined;

  filteredCities: City[] | undefined;

  countries: Country[] = [] as Array<Country>;
  filteredCountries: Observable<Country[]> | undefined;

  legalForms: LegalForm[] = [] as LegalForm[];
  filteredLegalForms: Observable<LegalForm[]> | undefined;

  filteredSiren: Siren | undefined;

  civilities: Civility[] = [] as Array<Civility>;

  DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_1_CODE = DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_1_CODE;
  DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_2_CODE = DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_2_CODE;
  DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_1_CODE = DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_1_CODE;
  DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_2_CODE = DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_2_CODE;
  DOMICILIATION_MAIL_REDIRECTION_TYPE_OTHER_CODE = DOMICILIATION_MAIL_REDIRECTION_TYPE_OTHER_CODE;
  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;


  constructor(private formBuilder: FormBuilder,
    protected domiciliationContractTypeService: DomiciliationContractTypeService,
    protected languageService: LanguageService,
    private buildingDomiciliationService: BuildingDomiciliationService,
    private cityService: CityService,
    private countryService: CountryService,
    private mailRedirectionTypeService: MailRedirectionTypeService,
    private legalFormService: LegalFormService,
    private sirenService: SirenService,
    protected civilityService: CivilityService,
  ) { }

  ngOnInit() {
    this.domiciliationContractTypeService.getContractTypes().subscribe(response => {
      this.domiciliationContractTypes = response;
    })
    this.countryService.getCountries().subscribe(response => {
      this.countries = response;
    })
    this.languageService.getLanguages().subscribe(response => {
      this.languages = response;
      if (this.provision.domiciliation != null && this.provision.domiciliation.language == undefined || this.provision.domiciliation.language == null)
        this.provision.domiciliation.language = this.languages[0];
    })
    this.buildingDomiciliationService.getBuildingDomiciliations().subscribe(response => {
      this.buildingDomiciliations = response;
      if (this.provision.domiciliation != null && this.provision.domiciliation.buildingDomiciliation == undefined || this.provision.domiciliation.buildingDomiciliation == null)
        this.provision.domiciliation.buildingDomiciliation = this.buildingDomiciliations[0];
    })
    this.mailRedirectionTypeService.getMailRedirectionTypes().subscribe(response => {
      this.mailRedirectionTypes = response;
      if (this.provision.domiciliation != null && this.provision.domiciliation.mailRedirectionType == undefined || this.provision.domiciliation.mailRedirectionType == null)
        this.provision.domiciliation.mailRedirectionType = this.mailRedirectionTypes[0];
    })
    this.legalFormService.getLegalForms().subscribe(response => {
      this.legalForms = response;
    })
    this.civilityService.getCivilities().subscribe(response => {
      this.civilities = response;
      if (this.provision.domiciliation != null && this.provision.domiciliation.legalGardianCivility == undefined)
        this.provision.domiciliation.legalGardianCivility = this.civilities[0];
    })

    this.filteredLegalForms = this.domiciliationForm.get("legalGardianLegalForm")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterByCodeOrLabel(this.legalForms, value)),
    );

    this.domiciliationForm.get("postalCode")?.valueChanges.pipe(
      filter(res => {
        return res != undefined && res !== null && res.length >= 2
      }),
      distinctUntilChanged(),
      debounceTime(300),
      tap(() => {
        this.filteredPostalCodes = [];
      }),
      switchMap(value => this.cityService.getCitiesFilteredByPostalCode(value)
      )
    ).subscribe((response: City[]) => {
      this.filteredPostalCodes = [...new Set(response.map(city => city.postalCode))];
    });

    this.domiciliationForm.get("city")?.valueChanges.pipe(
      filter(res => {
        return res != undefined && res !== null && res.length >= 2
      }),
      distinctUntilChanged(),
      debounceTime(300),
      tap(() => {
        this.filteredCities = [];
      }),
      switchMap(value => this.cityService.getCitiesFilteredByCountryAndName(value, this.provision.domiciliation.country)
      )
    ).subscribe(response => {
      this.filteredCities = response as City[];
    });

    this.filteredCountries = this.domiciliationForm.get("country")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterCountry(value)),
    );

    this.domiciliationForm.get("activityPostalCode")?.valueChanges.pipe(
      filter(res => {
        return res != undefined && res !== null && res.length >= 2
      }),
      distinctUntilChanged(),
      debounceTime(300),
      tap(() => {
        this.filteredPostalCodes = [];
      }),
      switchMap(value => this.cityService.getCitiesFilteredByPostalCode(value)
      )
    ).subscribe((response: City[]) => {
      this.filteredPostalCodes = [...new Set(response.map(city => city.postalCode))];
    });

    this.domiciliationForm.get("activityCity")?.valueChanges.pipe(
      filter(res => {
        return res != undefined && res !== null && res.length >= 2
      }),
      distinctUntilChanged(),
      debounceTime(300),
      tap(() => {
        this.filteredCities = [];
      }),
      switchMap(value => this.cityService.getCitiesFilteredByCountryAndName(value, this.provision.domiciliation.country)
      )
    ).subscribe(response => {
      this.filteredCities = response as City[];
    });

    this.filteredCountries = this.domiciliationForm.get("activityCountry")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterCountry(value)),
    );


    this.domiciliationForm.get("legalGardianPostalCode")?.valueChanges.pipe(
      filter(res => {
        return res != undefined && res !== null && res.length >= 2
      }),
      distinctUntilChanged(),
      debounceTime(300),
      tap(() => {
        this.filteredPostalCodes = [];
      }),
      switchMap(value => this.cityService.getCitiesFilteredByPostalCode(value)
      )
    ).subscribe((response: City[]) => {
      this.filteredPostalCodes = [...new Set(response.map(city => city.postalCode))];
    });

    this.domiciliationForm.get("legalGardianCity")?.valueChanges.pipe(
      filter(res => {
        return res != undefined && res !== null && res.length >= 2
      }),
      distinctUntilChanged(),
      debounceTime(300),
      tap(() => {
        this.filteredCities = [];
      }),
      switchMap(value => this.cityService.getCitiesFilteredByCountryAndName(value, this.provision.domiciliation.country)
      )
    ).subscribe(response => {
      this.filteredCities = response as City[];
    });

    this.filteredCountries = this.domiciliationForm.get("legalGardianCountry")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterCountry(value)),
    );

    this.domiciliationForm.get("legalGardianSiren")?.valueChanges.pipe(
      filter(res => {
        return res != undefined && res !== null && res.length == 9
      }),
      distinctUntilChanged(),
      debounceTime(300),
      tap(() => {
        this.filteredSiren = undefined;
      }),
      switchMap(value => this.sirenService.getSiren(value)
      )
    ).subscribe((response: Siren) => {
      this.filteredSiren = response;
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.provision != undefined) {
      if (this.provision.domiciliation == undefined || this.provision.domiciliation == null)
        this.provision.domiciliation = {} as Domiciliation;
      if (this.provision.domiciliation.language == undefined || this.provision.domiciliation.language == null)
        this.provision.domiciliation.language = this.languages[0];
      if (this.provision.domiciliation != null && this.provision.domiciliation.mailRedirectionType == undefined || this.provision.domiciliation.mailRedirectionType == null)
        this.provision.domiciliation.mailRedirectionType = this.mailRedirectionTypes[0];
      if (this.provision.domiciliation != null && this.provision.domiciliation.buildingDomiciliation == undefined || this.provision.domiciliation.buildingDomiciliation == null)
        this.provision.domiciliation.buildingDomiciliation = this.buildingDomiciliations[0];
      if (this.provision.domiciliation != null && this.provision.domiciliation.startDate != null && this.provision.domiciliation.startDate != undefined)
        this.provision.domiciliation.startDate = new Date(this.provision.domiciliation.startDate);
      if (this.provision.domiciliation != null && this.provision.domiciliation.legalGardianBirthdate != null && this.provision.domiciliation.legalGardianBirthdate != undefined)
        this.provision.domiciliation.legalGardianBirthdate = new Date(this.provision.domiciliation.legalGardianBirthdate);
      if (this.provision.domiciliation.isLegalPerson == null || this.provision.domiciliation.isLegalPerson == undefined)
        this.provision.domiciliation.isLegalPerson = false;
      this.domiciliationForm.markAllAsTouched();
      this.toggleTabs();
    }
  }

  domiciliationForm = this.formBuilder.group({
    domiciliationContractType: ['', [Validators.required]],
    language: ['', [Validators.required]],
    buildingDomiciliation: ['', [Validators.required]],
    mailRedirectionType: ['', [Validators.required]],
    startDate: ['', [Validators.required]],
    mailRecipient: ['', [Validators.maxLength(60), this.checkFieldFilledIfAdressMandatory("mailRecipient")]],
    address: ['', [Validators.maxLength(20), this.checkFieldFilledIfAdressMandatory("address")]],
    postalCode: ['', [this.checkFieldFilledIfIsInFrance("postalCode"), this.checkFieldFilledIfAdressMandatory("postalCode")]],
    city: ['', [Validators.maxLength(30), this.checkFieldFilledIfAdressMandatory("city")]],
    country: ['', [this.checkAutocompleteField("country"), this.checkFieldFilledIfAdressMandatory("country")]],
    activityMailRecipient: ['', [Validators.maxLength(60), this.checkFieldFilledIfAdressMandatory("activityMailRecipient")]],
    activityAddress: ['', [Validators.maxLength(20), Validators.required]],
    activityPostalCode: ['', [this.checkFieldFilledIfIsInFrance("activityPostalCode"), Validators.required]],
    activityCity: ['', [Validators.maxLength(30), Validators.required]],
    activityCountry: ['', [this.checkAutocompleteField("activityCountry"), Validators.required]],
    mails: ['', [this.checkFieldFilledIfMailMandatory("mails")]],
    activityDescription: ['', [Validators.required]],
    activityMails: ['', [this.checkFieldFilledIfMailMandatory("activityMails")]],
    accountingRecordDomiciliation: ['', [Validators.required, Validators.maxLength(60)]],
    isLegalPerson: ['', [Validators.required]],
    legalGardianLegalForm: ['', [this.checkFieldFilledIfIsLegalPerson("legalGardianLegalForm")]],
    legalGardianSiren: ['', [this.checkSiren("legalGardianSiren"), this.checkFieldFilledIfIsLegalPerson("legalGardianSiren"),]],
    legalGardianDenomination: ['', [this.checkFieldFilledIfIsLegalPerson("legalGardianDenomination"), Validators.maxLength(60)]],
    legalGardianFirstname: ['', [this.checkFieldFilledIfIsNotLegalPerson("legalGardianFirstname"), Validators.maxLength(20)]],
    legalGardianLastname: ['', [this.checkFieldFilledIfIsNotLegalPerson("legalGardianLastname"), Validators.maxLength(20)]],
    legalGardianJob: ['', [this.checkFieldFilledIfIsNotLegalPerson("legalGardianJob"), Validators.maxLength(20)]],
    legalGardianPlaceOfBirth: ['', [this.checkFieldFilledIfIsNotLegalPerson("legalGardianPlaceOfBirth"), Validators.maxLength(60)]],
    legalGardianBirthdate: ['', [this.checkFieldFilledIfIsNotLegalPerson("legalGardianBirthdate")]],
    legalGardianMailRecipient: ['', [Validators.maxLength(60), Validators.required]],
    legalGardianAddress: ['', [Validators.maxLength(20), Validators.required]],
    legalGardianPostalCode: ['', []],
    legalGardianCity: ['', [Validators.maxLength(30), Validators.required]],
    legalGardianCountry: ['', [Validators.required]],
    legalGardianMails: ['', []],
    legalGardianCivility: ['', [this.checkFieldFilledIfIsNotLegalPerson("legalGardianCivility")]],
    legalGardianPhones: ['', []],
  });

  getFormStatus(): boolean {
    this.domiciliationForm.markAllAsTouched();
    return this.domiciliationForm.valid;
  }

  toggleTabs() {
    if (this.tabs != undefined)
      this.tabs.realignInkBar();
  }

  checkFieldFilledIfAdressMandatory(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;
      const fieldValue = root.get(fieldName)?.value;
      if (this.mustDecribeAdresse() && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  checkFieldFilledIfMailMandatory(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;
      const fieldValue = root.get(fieldName)?.value;
      if (this.mustDecribeMail() && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  checkFieldFilledIfIsInFrance(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;
      const fieldValue = root.get(fieldName)?.value;
      if (this.provision.domiciliation != null && this.provision.domiciliation.country != null && this.provision.domiciliation.country.code == COUNTRY_CODE_FRANCE && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  checkAutocompleteField(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (fieldValue != undefined && fieldValue != null && (fieldValue.id == undefined || fieldValue.id == null))
        return {
          notFilled: true
        };
      return null;
    };
  }

  public displayLabel(object: any): string {
    return object ? object.label : '';
  }

  mustDecribeAdresse() {
    return this.provision.domiciliation != null && this.provision.domiciliation != undefined && this.provision.domiciliation.domiciliationContractType != null &&
      (this.provision.domiciliation.domiciliationContractType.code == DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_1_CODE
        || this.provision.domiciliation.domiciliationContractType.code == DOMICILIATION_MAIL_REDIRECTION_CONTRAT_TYPE_2_CODE)
      && this.provision.domiciliation.mailRedirectionType != null
      && this.provision.domiciliation.mailRedirectionType.code == this.DOMICILIATION_MAIL_REDIRECTION_TYPE_OTHER_CODE;
  }

  mustDecribeMail() {
    return this.provision.domiciliation != null && this.provision.domiciliation != undefined && this.provision.domiciliation.domiciliationContractType != null &&
      (this.provision.domiciliation.domiciliationContractType.code == DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_1_CODE
        || this.provision.domiciliation.domiciliationContractType.code == DOMICILIATION_EMAIL_REDIRECTION_CONTRAT_TYPE_2_CODE)
      && this.provision.domiciliation.mailRedirectionType != null
      && this.provision.domiciliation.mailRedirectionType.code == this.DOMICILIATION_MAIL_REDIRECTION_TYPE_OTHER_CODE;
  }


  private _filterCountry(value: string): Country[] {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return this.countries.filter(country => country.label != undefined && country.label.toLowerCase().includes(filterValue));
  }

  fillPostalCode(city: City) {
    if (this.provision.domiciliation != null) {
      if (this.provision.domiciliation.country == null || this.provision.domiciliation.country == undefined)
        this.provision.domiciliation.country = city.country;

      if (this.provision.domiciliation.country.code == COUNTRY_CODE_FRANCE && city.postalCode != null)
        this.provision.domiciliation.postalCode = city.postalCode;
    }
  }

  fillCity(postalCode: string) {
    if (this.provision.domiciliation != null) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.provision.domiciliation != null) {
            if (this.provision.domiciliation.country == null || this.provision.domiciliation.country == undefined)
              this.provision.domiciliation.country = city.country;

            this.provision.domiciliation.city = city;
          }
        }
      })
    }
  }

  fillActivityPostalCode(city: City) {
    if (this.provision.domiciliation != null) {
      if (this.provision.domiciliation.activityCountry == null || this.provision.domiciliation.activityCountry == undefined)
        this.provision.domiciliation.activityCountry = city.country;

      if (this.provision.domiciliation.activityCountry.code == COUNTRY_CODE_FRANCE && city.postalCode != null)
        this.provision.domiciliation.activityPostalCode = city.postalCode;
    }
  }

  fillActivityCity(postalCode: string) {
    if (this.provision.domiciliation != null) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.provision.domiciliation != null) {
            if (this.provision.domiciliation.activityCountry == null || this.provision.domiciliation.activityCountry == undefined)
              this.provision.domiciliation.activityCountry = city.country;

            this.provision.domiciliation.activityCity = city;
          }
        }
      })
    }
  }

  fillLegalGardianPostalCode(city: City) {
    if (this.provision.domiciliation != null) {
      if (this.provision.domiciliation.legalGardianCountry == null || this.provision.domiciliation.legalGardianCountry == undefined)
        this.provision.domiciliation.legalGardianCountry = city.country;

      if (this.provision.domiciliation.legalGardianCountry.code == COUNTRY_CODE_FRANCE && city.postalCode != null)
        this.provision.domiciliation.legalGardianPostalCode = city.postalCode;
    }
  }

  fillLegalGardianCity(postalCode: string) {
    if (this.provision.domiciliation != null) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.provision.domiciliation != null) {
            if (this.provision.domiciliation.legalGardianCountry == null || this.provision.domiciliation.legalGardianCountry == undefined)
              this.provision.domiciliation.legalGardianCountry = city.country;

            this.provision.domiciliation.legalGardianCity = city;
          }
        }
      })
    }
  }


  addMail(event: MatChipInputEvent): void {
    if (this.provision.domiciliation != null) {
      const value = (event.value || '').trim();
      let mail: Mail = {} as Mail;
      if (value && validateEmail(value)) {
        mail.mail = value;
        if (this.provision.domiciliation.mails == undefined || this.provision.domiciliation.mails == null)
          this.provision.domiciliation.mails = [] as Mail[];
        this.provision.domiciliation.mails.push(mail);
      }
      event.chipInput!.clear();
      this.domiciliationForm.get("mails")?.setValue(null);
    }
  }

  removeMail(inputMail: Mail): void {
    if (this.provision.domiciliation != null) {
      if (this.provision.domiciliation.mails != undefined && this.provision.domiciliation.mails != null && this.editMode)
        for (let i = 0; i < this.provision.domiciliation.mails.length; i++) {
          const mail = this.provision.domiciliation.mails[i];
          if (mail.mail == inputMail.mail) {
            this.provision.domiciliation.mails.splice(i, 1);
            return;
          }
        }
    }
  }

  addActivityMail(event: MatChipInputEvent): void {
    if (this.provision.domiciliation != null) {
      const value = (event.value || '').trim();
      let mail: Mail = {} as Mail;
      if (value && validateEmail(value)) {
        mail.mail = value;
        if (this.provision.domiciliation.activityMails == undefined || this.provision.domiciliation.activityMails == null)
          this.provision.domiciliation.activityMails = [] as Mail[];
        this.provision.domiciliation.activityMails.push(mail);
      }
      event.chipInput!.clear();
      this.domiciliationForm.get("activityMails")?.setValue(null);
    }
  }

  removeActivityMail(inputMail: Mail): void {
    if (this.provision.domiciliation != null) {
      if (this.provision.domiciliation.activityMails != undefined && this.provision.domiciliation.activityMails != null && this.editMode)
        for (let i = 0; i < this.provision.domiciliation.activityMails.length; i++) {
          const mail = this.provision.domiciliation.activityMails[i];
          if (mail.mail == inputMail.mail) {
            this.provision.domiciliation.activityMails.splice(i, 1);
            return;
          }
        }
    }
  }


  addLegalGardianyMail(event: MatChipInputEvent): void {
    if (this.provision.domiciliation != null) {
      const value = (event.value || '').trim();
      let mail: Mail = {} as Mail;
      if (value && validateEmail(value)) {
        mail.mail = value;
        if (this.provision.domiciliation.legalGardianMails == undefined || this.provision.domiciliation.legalGardianMails == null)
          this.provision.domiciliation.legalGardianMails = [] as Mail[];
        this.provision.domiciliation.legalGardianMails.push(mail);
      }
      event.chipInput!.clear();
      this.domiciliationForm.get("legalGardianMails")?.setValue(null);
    }
  }

  removeLegalGardianMail(inputMail: Mail): void {
    if (this.provision.domiciliation != null) {
      if (this.provision.domiciliation.legalGardianMails != undefined && this.provision.domiciliation.legalGardianMails != null && this.editMode)
        for (let i = 0; i < this.provision.domiciliation.legalGardianMails.length; i++) {
          const mail = this.provision.domiciliation.legalGardianMails[i];
          if (mail.mail == inputMail.mail) {
            this.provision.domiciliation.legalGardianMails.splice(i, 1);
            return;
          }
        }
    }
  }


  private _filterByCodeOrLabel(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.code != undefined && input.code.toLowerCase().includes(filterValue) || input.label != undefined && input.label.toLowerCase().includes(filterValue));
  }

  limitTextareaSize(fieldName: string, maxLines: number) {
    let fieldValue = this.domiciliationForm.get(fieldName)?.value != undefined ? this.domiciliationForm.get(fieldName)?.value : "";
    var l = fieldValue.replace(/\r\n/g, "\n").replace(/\r/g, "").split(/\n/g);//split lines
    if (l.length > maxLines) {
      fieldValue = l.slice(0, maxLines).join("\n");
    }

    this.domiciliationForm.get(fieldName)?.setValue(fieldValue);
  }

  // Check if the propertiy given in parameter is filled when it's a legal personn
  checkFieldFilledIfIsLegalPerson(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;
      const fieldValue = root.get(fieldName)?.value;
      if (this.provision.domiciliation != undefined && this.provision.domiciliation.isLegalPerson && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  // Check if the propertiy given in parameter is filled when it's not a legal personn
  checkFieldFilledIfIsNotLegalPerson(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;
      const fieldValue = root.get(fieldName)?.value;
      if (this.provision.domiciliation != undefined && !this.provision.domiciliation.isLegalPerson && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  checkSiren(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (this.provision.domiciliation != undefined && this.provision.domiciliation.isLegalPerson && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0 || !validateSiren(fieldValue)))
        return {
          notFilled: true
        };
      return null;
    };
  }

  fillSiren() {
    if (this.filteredSiren != undefined && this.filteredSiren != null) {
      this.provision.domiciliation.legalGardianSiren = this.filteredSiren!.uniteLegale.siren;
      if (this.filteredSiren!.uniteLegale.siren != undefined && this.filteredSiren!.uniteLegale.siren != null) {
        if (this.filteredSiren.uniteLegale.periodesUniteLegale != null && this.filteredSiren.uniteLegale.periodesUniteLegale != undefined && this.filteredSiren.uniteLegale.periodesUniteLegale.length > 0) {
          this.filteredSiren.uniteLegale.periodesUniteLegale.forEach(periode => {
            if (periode.dateFin == null)
              this.provision.domiciliation.legalGardianDenomination = periode.denominationUniteLegale;
            this.domiciliationForm.markAllAsTouched();
          });
        }
      }
    }
  }
  prepareMail = function (mail: Mail) {
    prepareMail(mail.mail, null, null);
  }


  addLegalGardianPhone(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    let phone: Phone = {} as Phone;
    if (value && (validateFrenchPhone(value) || validateInternationalPhone(value))) {
      phone.phoneNumber = value;
      if (this.provision.domiciliation.legalGardianPhones == undefined || this.provision.domiciliation.legalGardianPhones == null)
        this.provision.domiciliation.legalGardianPhones = [] as Phone[];
      this.provision.domiciliation.legalGardianPhones.push(phone);
    }
    event.chipInput!.clear();
    this.domiciliationForm.get("legalGardianPhones")?.setValue(null);
  }

  removeLegalGardianPhone(inputPhone: Phone): void {
    if (this.provision.domiciliation.legalGardianPhones != undefined && this.provision.domiciliation.legalGardianPhones != null && this.editMode)
      for (let i = 0; i < this.provision.domiciliation.legalGardianPhones.length; i++) {
        const phone = this.provision.domiciliation.legalGardianPhones[i];
        if (phone.phoneNumber == inputPhone.phoneNumber) {
          this.provision.domiciliation.legalGardianPhones.splice(i, 1);
          return;
        }
      }
  }

  call = function (phone: Phone) {
    callNumber(phone.phoneNumber);
  }

  compareWithId = compareWithId;
}
