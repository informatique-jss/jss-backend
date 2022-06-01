import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { Observable, of } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, map, startWith, switchMap, tap } from 'rxjs/operators';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { COUNTRY_CODE_FRANCE, SEPARATOR_KEY_CODES, UNREGISTERED_COMPANY_LEGAL_FORM_CODE } from 'src/app/libs/Constants';
import { validateEmail, validateFrenchPhone, validateInternationalPhone, validateRna, validateSiren, validateSiret } from 'src/app/libs/CustomFormsValidatorsHelper';
import { callNumber, prepareMail } from 'src/app/libs/MailHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Civility } from 'src/app/modules/miscellaneous/model/Civility';
import { Country } from 'src/app/modules/miscellaneous/model/Country';
import { LegalForm } from 'src/app/modules/miscellaneous/model/LegalForm';
import { Mail } from 'src/app/modules/miscellaneous/model/Mail';
import { Phone } from 'src/app/modules/miscellaneous/model/Phone';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { CivilityService } from 'src/app/modules/miscellaneous/services/civility.service';
import { CountryService } from 'src/app/modules/miscellaneous/services/country.service';
import { LegalFormService } from 'src/app/modules/miscellaneous/services/legal.form.service';
import { Affaire } from '../../model/Affaire';
import { IQuotation } from '../../model/IQuotation';
import { Provision } from '../../model/Provision';
import { Rna } from '../../model/Rna';
import { Siren } from '../../model/Siren';
import { Siret } from '../../model/Siret';
import { RnaService } from '../../services/rna.service';
import { SirenService } from '../../services/siren.service';
import { SiretService } from '../../services/siret.service';

@Component({
  selector: 'affaire',
  templateUrl: './affaire.component.html',
  styleUrls: ['./affaire.component.css']
})
export class AffaireComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() affaire: Affaire = {} as Affaire;
  @Input() editMode: boolean = false;

  civilities: Civility[] = [] as Civility[];
  legalForms: LegalForm[] = [] as LegalForm[];
  filteredLegalForms: Observable<LegalForm[]> | undefined;

  filteredSiren: Siren | undefined;
  filteredSiret: Siret | undefined;
  filteredRna: Rna | undefined;

  filteredPostalCodes: String[] | undefined;

  filteredCities: City[] | undefined;

  countries: Country[] = [] as Array<Country>;
  filteredCountries: Observable<Country[]> | undefined;

  UNREGISTERED_COMPANY_LEGAL_FORM_CODE = UNREGISTERED_COMPANY_LEGAL_FORM_CODE;
  SEPARATOR_KEY_CODE = SEPARATOR_KEY_CODES;

  constructor(private formBuilder: FormBuilder,
    private civilityService: CivilityService,
    private cityService: CityService,
    private countryService: CountryService,
    private sirenService: SirenService,
    private siretService: SiretService,
    private rnaService: RnaService,
    private legalFormService: LegalFormService,
  ) { }

  ngOnInit() {
    this.civilityService.getCivilities().subscribe(response => {
      this.civilities = response;
    })
    this.legalFormService.getLegalForms().subscribe(response => {
      this.legalForms = response;
    })
    this.countryService.getCountries().subscribe(response => {
      this.countries = response;
      if (this.affaire != null && (this.affaire.country == null || this.affaire.country == undefined))
        this.affaire.country = this.countries[0];
    })

    this.filteredLegalForms = this.affaireForm.get("legalForm")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterByCodeOrLabel(this.legalForms, value)),
    );

    this.affaireForm.get("siren")?.valueChanges.pipe(
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

    this.affaireForm.get("siret")?.valueChanges.pipe(
      filter(res => {
        return res != undefined && res !== null && res.replace(/\s/g, '').length == 14
      }),
      distinctUntilChanged(),
      debounceTime(300),
      tap(() => {
        this.filteredSiret = undefined;
      }),
      switchMap(value => this.siretService.getSiret(value)
      )
    ).subscribe((response: Siret) => {
      this.filteredSiret = response;
    });


    this.affaireForm.get("postalCode")?.valueChanges.pipe(
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

    this.affaireForm.get("city")?.valueChanges.pipe(
      filter(res => {
        return res != undefined && res !== null && res.length >= 2
      }),
      distinctUntilChanged(),
      debounceTime(300),
      tap(() => {
        this.filteredCities = [];
      }),
      switchMap(value => this.cityService.getCitiesFilteredByCountryAndName(value, this.affaire.country)
      )
    ).subscribe(response => {
      this.filteredCities = response as City[];
    });

    this.filteredCountries = this.affaireForm.get("country")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterCountry(value)),
    );

  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.affaire != undefined) {
      if (this.affaire.isIndividual && (this.affaire.civility == null || this.affaire.civility == undefined))
        this.affaire.civility = this.civilities[0];

      if (this.affaire.isIndividual == undefined)
        this.affaire.isIndividual = false;

      if (this.affaire.country == null || this.affaire.country == undefined)
        this.affaire.country = this.countries[0];
      this.affaireForm.markAllAsTouched();
    }
  }

  affaireForm = this.formBuilder.group({
    civility: ['', this.checkFieldFilledIfIsIndividual("civility")],
    isIndividual: [''],
    firstname: ['', [this.checkFieldFilledIfIsIndividual("firstname"), Validators.maxLength(20)]],
    lastname: ['', [this.checkFieldFilledIfIsIndividual("lastname"), Validators.maxLength(20)]],
    legalForm: ['', [this.checkFieldFilledIfIsNotIndividual("legalForm")]],
    denomination: ['', [this.checkFieldFilledIfIsNotIndividual("denomination"), Validators.maxLength(60)]],
    siren: ['', [this.checkSiren("siren"), this.checkFieldFilledIfIsNotIndividualAndNotUnregistered("siren"),]],
    siret: ['', [this.checkFieldFilledIfIsNotIndividualAndNotUnregistered("siret"), this.checkSiret("siret")]],
    rna: ['', [this.checkRna("rna")]],
    address: ['', [Validators.required, Validators.maxLength(20)]],
    postalCode: ['', [this.checkFieldFilledIfIsInFrance("postalCode")]],
    city: ['', [Validators.required, Validators.maxLength(30)]],
    country: ['', [Validators.required, this.checkAutocompleteField("country")]],
    mails: ['', []],
    phones: ['', []],
    observations: ['', []],
    shareCapital: ['', []],
    externalReference: ['', [Validators.maxLength(60)]],
  });

  getFormStatus(): boolean {
    return this.affaireForm.valid;
  }

  // Check if the propertiy given in parameter is filled when the affaire is an individual
  checkFieldFilledIfIsIndividual(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (this.affaire.isIndividual && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  // Check if the propertiy given in parameter is filled when the affaire is an individual
  checkFieldFilledIfIsNotIndividual(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (!this.affaire.isIndividual && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  // Check if the propertiy given in parameter is filled when the affaire is an individual
  checkFieldFilledIfIsNotIndividualAndNotUnregistered(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;
      console.log("tt");
      const fieldValue = root.get(fieldName)?.value;
      if (!this.affaire.isIndividual && this.affaire.legalForm != null && this.affaire.legalForm.code != UNREGISTERED_COMPANY_LEGAL_FORM_CODE && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        if (this.affaire.rna == null && this.affaire.siret == null)
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
      if (this.affaire.country != null && this.affaire.country.code == COUNTRY_CODE_FRANCE && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
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

  checkSiren(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (!this.affaire.isIndividual && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0 || !validateSiren(fieldValue)))
        return {
          notFilled: true
        };
      return null;
    };
  }

  checkSiret(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (!this.affaire.isIndividual && (fieldValue != undefined && fieldValue != null && fieldValue.length > 0 && !validateSiret(fieldValue)))
        return {
          notFilled: true
        };
      return null;
    };
  }

  checkRna(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (!this.affaire.isIndividual && fieldValue != undefined && fieldValue != null && fieldValue.length > 0 && !validateRna(fieldValue))
        return {
          notFilled: true
        };
      return null;
    };
  }


  fillPostalCode(city: City) {
    if (this.affaire.country == null || this.affaire.country == undefined)
      this.affaire.country = city.country;

    if (this.affaire.country.code == COUNTRY_CODE_FRANCE && city.postalCode != null)
      this.affaire.postalCode = city.postalCode;
  }

  fillCity(postalCode: string) {
    this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
      if (response != null && response != undefined && response.length == 1) {
        let city = response[0];
        if (this.affaire.country == null || this.affaire.country == undefined)
          this.affaire.country = city.country;

        this.affaire.city = city;
      }
    })

  }

  fillSiren() {
    if (this.filteredSiren != undefined && this.filteredSiren != null) {
      this.affaire.siren = this.filteredSiren!.uniteLegale.siren;
      if (this.filteredSiren!.uniteLegale.identifiantAssociationUniteLegale != undefined && this.filteredSiren!.uniteLegale.identifiantAssociationUniteLegale != null
        && this.filteredSiren!.uniteLegale.identifiantAssociationUniteLegale.substring(0, 1) == "W") {
        this.rnaService.getRna(this.filteredSiren!.uniteLegale.identifiantAssociationUniteLegale).subscribe(response => {
          if (response != null) {
            this.filteredRna = response;
            this.fillRna();
          }
        })
      } else if (this.filteredSiren!.uniteLegale.siren != undefined && this.filteredSiren!.uniteLegale.siren != null) {
        if (this.filteredSiren.uniteLegale.periodesUniteLegale != null && this.filteredSiren.uniteLegale.periodesUniteLegale != undefined && this.filteredSiren.uniteLegale.periodesUniteLegale.length > 0) {
          this.filteredSiren.uniteLegale.periodesUniteLegale.forEach(periode => {
            if (periode.dateFin == null)
              this.affaire.denomination = periode.denominationUniteLegale;
            this.affaireForm.markAllAsTouched();
          });
        }
      }
    }
    this.affaireForm.markAllAsTouched();
  }

  fillSiret() {
    if (this.filteredSiret != undefined && this.filteredSiret != null) {
      this.affaire.siret = this.filteredSiret!.etablissement.siret;
      if (this.filteredSiret.etablissement.siren != null && this.filteredSiret.etablissement.siren != undefined) {
        this.sirenService.getSiren(this.filteredSiret.etablissement.siren).subscribe(response => {
          if (response != null) {
            this.filteredSiren = response;
            this.fillSiren();
          }
        })
        if (this.filteredSiret.etablissement.adresseEtablissement != null && this.filteredSiret.etablissement.adresseEtablissement.codePostalEtablissement != null) {
          this.cityService.getCitiesFilteredByPostalCode(this.filteredSiret.etablissement.adresseEtablissement.codePostalEtablissement).subscribe(response => {
            if (response != null && response.length == 1) {
              this.affaire.postalCode = this.filteredSiret!.etablissement.adresseEtablissement.codePostalEtablissement;
              this.fillPostalCode(response[0]);
              this.affaireForm.markAllAsTouched();
            } else if (this.filteredSiret!.etablissement.adresseEtablissement.libelleCommuneEtablissement) {
              this.cityService.getCitiesFilteredByCountryAndName(this.filteredSiret!.etablissement.adresseEtablissement.libelleCommuneEtablissement, this.countries[0]).subscribe(response2 => {
                if (response2 != null && response2.length == 1) {
                  this.affaire.city = response2[0];
                  this.affaire.country = response2[0].country;
                  this.affaireForm.markAllAsTouched();
                }
              })
            }
          })
          this.affaire.address = this.filteredSiret.etablissement.adresseEtablissement.numeroVoieEtablissement + " " + this.filteredSiret.etablissement.adresseEtablissement.typeVoieEtablissement + " " + this.filteredSiret.etablissement.adresseEtablissement.libelleVoieEtablissement;
        }
      }
    }
    this.affaireForm.markAllAsTouched();
  }

  fillRna() {
    if (this.filteredRna != undefined && this.filteredRna != null) {
      this.affaire.rna = this.filteredRna.association.id_association;
      this.affaire.denomination = this.filteredRna.association.titre_court;
      this.affaire.address = this.filteredRna.association.adresse_gestion_libelle_voie + "\n" + this.filteredRna.association.adresse_gestion_distribution;
      if (this.filteredRna.association.email != null)
        this.addMail(this.filteredRna.association.email);
      if (this.filteredRna.association.telephone != null)
        this.addPhone(this.filteredRna.association.telephone);
      this.cityService.getCitiesFilteredByPostalCode(this.filteredRna.association.adresse_code_postal).subscribe(response => {
        if (response != null && response.length == 1) {
          this.affaire.postalCode = this.filteredRna!.association.adresse_code_postal;
          this.fillPostalCode(response[0]);
        } else {
          this.cityService.getCitiesFilteredByCountryAndName(this.filteredRna!.association.adresse_libelle_commune, this.countries[0]).subscribe(response2 => {
            if (response2 != null && response2.length == 1) {
              this.affaire.city = response2[0];
              this.affaire.country = response2[0].country;
              this.affaireForm.markAllAsTouched();
            }
          })
        }
      })
    }
    this.affaireForm.markAllAsTouched();
  }

  private _filterCountry(value: string): Country[] {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return this.countries.filter(country => country.label != undefined && country.label.toLowerCase().includes(filterValue));
  }

  private _filterByCodeOrLabel(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.code != undefined && input.code.toLowerCase().includes(filterValue) || input.label != undefined && input.label.toLowerCase().includes(filterValue));
  }

  public displayLabel(object: any): string {
    return object ? object.label : '';
  }


  addMail(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    let mail: Mail = {} as Mail;
    if (value && validateEmail(value)) {
      mail.mail = value;
      if (this.affaire.mails == undefined || this.affaire.mails == null)
        this.affaire.mails = [] as Mail[];
      this.affaire.mails.push(mail);
    }
    event.chipInput!.clear();
    this.affaireForm.get("mails")?.setValue(null);
  }

  removeMail(inputMail: Mail): void {
    if (this.affaire.mails != undefined && this.affaire.mails != null && this.editMode)
      for (let i = 0; i < this.affaire.mails.length; i++) {
        const mail = this.affaire.mails[i];
        if (mail.mail == inputMail.mail) {
          this.affaire.mails.splice(i, 1);
          return;
        }
      }
  }

  addPhone(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    let phone: Phone = {} as Phone;
    if (value && (validateFrenchPhone(value) || validateInternationalPhone(value))) {
      phone.phoneNumber = value;
      if (this.affaire.phones == undefined || this.affaire.phones == null)
        this.affaire.phones = [] as Phone[];
      this.affaire.phones.push(phone);
    }
    event.chipInput!.clear();
    this.affaireForm.get("phones")?.setValue(null);
  }

  removePhone(inputPhone: Phone): void {
    if (this.affaire.phones != undefined && this.affaire.phones != null && this.editMode)
      for (let i = 0; i < this.affaire.phones.length; i++) {
        const phone = this.affaire.phones[i];
        if (phone.phoneNumber == inputPhone.phoneNumber) {
          this.affaire.phones.splice(i, 1);
          return;
        }
      }
  }

  prepareMail = function (mail: Mail) {
    prepareMail(mail.mail, null, null);
  }

  call = function (phone: Phone) {
    callNumber(phone.phoneNumber);
  }

}

