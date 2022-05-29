import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { Observable, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, map, startWith, switchMap, tap } from 'rxjs/operators';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { COUNTRY_CODE_FRANCE, SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { validateEmail, validateFrenchPhone, validateInternationalPhone, validateVat } from 'src/app/libs/CustomFormsValidatorsHelper';
import { callNumber, prepareMail } from 'src/app/libs/MailHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Country } from 'src/app/modules/miscellaneous/model/Country';
import { DeliveryService } from 'src/app/modules/miscellaneous/model/DeliveryService';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { CivilityService } from 'src/app/modules/miscellaneous/services/civility.service';
import { CountryService } from 'src/app/modules/miscellaneous/services/country.service';
import { DeliveryServiceService } from 'src/app/modules/miscellaneous/services/delivery.service.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { Civility } from '../../../miscellaneous/model/Civility';
import { LanguageService } from '../../../miscellaneous/services/language.service';
import { Mail } from '../../model/Mail';
import { Phone } from '../../model/Phone';
import { TiersCategory } from '../../model/TiersCategory';
import { TiersType } from '../../model/TiersType';
import { SpecialOfferService } from '../../services/special-offer.service';
import { TiersCategoryService } from '../../services/tiers.category.service';
import { TiersTypeService } from '../../services/tiers.type.service';
import { Language } from '../../../miscellaneous/model/Language';
import { SpecialOffer } from '../../model/SpecialOffer';
import { Tiers } from '../../model/Tiers';
import { SpecialOffersDialogComponent } from '../special-offers-dialog/special-offers-dialog.component';

@Component({
  selector: 'tiers-main',
  templateUrl: './tiers-main.component.html',
  styleUrls: ['./tiers-main.component.css']
})

export class PrincipalComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() tiers: Tiers = {} as Tiers;
  @Input() editMode: boolean = false;

  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;

  tiersTypes: TiersType[] = [] as Array<TiersType>;
  tiersCategories: TiersCategory[] = [] as Array<TiersCategory>;
  civilities: Civility[] = [] as Array<Civility>;
  languages: Language[] = [] as Array<Language>;
  deliveryServices: DeliveryService[] = [] as Array<DeliveryService>;

  salesEmployees: Employee[] = [] as Array<Employee>;
  filteredSalesEmployees: Observable<Employee[]> | undefined;

  formalisteEmployees: Employee[] = [] as Array<Employee>;
  filteredFormalisteEmployees: Observable<Employee[]> | undefined;

  insertionEmployees: Employee[] = [] as Array<Employee>;
  filteredInsertionEmployees: Observable<Employee[]> | undefined;

  filteredPostalCodes: String[] | undefined;

  filteredCities: City[] | undefined;

  countries: Country[] = [] as Array<Country>;
  filteredCountries: Observable<Country[]> | undefined;

  specialOffers: SpecialOffer[] = [] as Array<SpecialOffer>;
  filteredSpecialOffers: Observable<SpecialOffer[]> | undefined;

  constructor(private formBuilder: FormBuilder,
    private tiersTypeService: TiersTypeService,
    private tiersCategoryService: TiersCategoryService,
    private civilityService: CivilityService,
    private languageService: LanguageService,
    private deliveryServiceService: DeliveryServiceService,
    private employeeService: EmployeeService,
    private cityService: CityService,
    private countryService: CountryService,
    private sepcialOfferService: SpecialOfferService,
    public specialOfferDialog: MatDialog) { }

  // TODO : reprendre les RG (notamment facturation / commande) lorsque les modules correspondants seront faits

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tiers != undefined) {
      if (this.tiers.language == undefined || this.tiers.language == null)
        this.tiers.language = this.languages[0];
      if (this.tiers.deliveryService == null || this.tiers.deliveryService == undefined)
        this.tiers.deliveryService = this.deliveryServices[0];
      if (this.tiers.country == null || this.tiers.country == undefined)
        this.tiers.country = this.countries[0];
      if (this.tiers.isIndividual == undefined || this.tiers.isIndividual == null)
        this.tiers.isIndividual = false;
      this.principalForm.markAllAsTouched();
    }
  }

  ngOnInit() {
    // Referential loading
    this.languageService.getLanguages().subscribe(response => {
      this.languages = response;
    })
    this.deliveryServiceService.getDeliveryServices().subscribe(response => {
      this.deliveryServices = response;
    })
    this.countryService.getCountries().subscribe(response => {
      this.countries = response;
    })

    this.tiersTypeService.getTiersTypes().subscribe(response => {
      this.tiersTypes = response;
    });
    this.civilityService.getCivilities().subscribe(response => {
      this.civilities = response;
    })
    this.tiersCategoryService.getTiersCategories().subscribe(response => {
      this.tiersCategories = response;
    })
    this.employeeService.getSalesEmployees().subscribe(response => {
      this.salesEmployees = response;
    })
    this.employeeService.getFormalisteEmployees().subscribe(response => {
      this.formalisteEmployees = response;
    })
    this.employeeService.getInsetionEmployees().subscribe(response => {
      this.insertionEmployees = response;
    })
    this.employeeService.getInsetionEmployees().subscribe(response => {
      this.insertionEmployees = response;
    })
    this.sepcialOfferService.getSpecialOffers().subscribe(response => {
      this.specialOffers = response;
    })


    // Initialize autocomplete fields
    this.filteredSalesEmployees = this.principalForm.get("salesEmployee")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterEmployee(this.salesEmployees, value)),
    );

    this.filteredFormalisteEmployees = this.principalForm.get("formalisteEmployee")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterEmployee(this.formalisteEmployees, value)),
    );

    this.filteredInsertionEmployees = this.principalForm.get("insertionEmployee")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterEmployee(this.insertionEmployees, value)),
    );

    this.filteredSpecialOffers = this.principalForm.get("specialOffer")?.valueChanges.pipe(
      startWith(''),
      map(value => (typeof value === 'string') ? this._filterByCode(this.specialOffers, value) : [])
    );

    this.principalForm.get("postalCode")?.valueChanges.pipe(
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

    this.principalForm.get("city")?.valueChanges.pipe(
      filter(res => {
        return res != undefined && res !== null && res.length >= 2
      }),
      distinctUntilChanged(),
      debounceTime(300),
      tap(() => {
        this.filteredCities = [];
      }),
      switchMap(value => this.cityService.getCitiesFilteredByCountryAndName(value, this.tiers.country)
      )
    ).subscribe(response => {
      this.filteredCities = response as City[];
    });

    this.filteredCountries = this.principalForm.get("country")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterCountry(value)),
    );

    // Trigger it to show mandatory fields
    this.principalForm.markAllAsTouched();
  }

  principalForm = this.formBuilder.group({
    tiersType: ['', Validators.required],
    tiersId: [{ value: '', disabled: true }],
    denomination: ['', [Validators.required, Validators.maxLength(60)]],
    firstBilling: [{ value: '', disabled: true }],
    civility: ['', this.checkFieldFilledIfIsIndividual("civility")],
    isIndividual: [''],
    firstname: ['', [this.checkFieldFilledIfIsIndividual("firstname"), Validators.maxLength(20)]],
    lastname: ['', [this.checkFieldFilledIfIsIndividual("lastname"), Validators.maxLength(20)]],
    tiersCategory: [''],
    salesEmployee: ['', [Validators.required, this.checkAutocompleteField("salesEmployee")]],
    formalisteEmployee: ['', [this.checkAutocompleteField("formalisteEmployee")]],
    insertionEmployee: ['', [this.checkAutocompleteField("insertionEmployee")]],
    mailRecipient: [''],
    language: ['', Validators.required],
    deliveryService: ['', Validators.required],
    address: ['', [Validators.required, Validators.maxLength(20)]],
    postalCode: ['', [this.checkFieldFilledIfIsInFrance("postalCode")]],
    city: ['', [Validators.required, Validators.maxLength(30)]],
    country: ['', [Validators.required, this.checkAutocompleteField("country")]],
    intercom: ['', [Validators.maxLength(12)]],
    intercommunityVat: ['', [this.checkVAT("intercommunityVat")]],
    specialOffer: ['', [this.checkAutocompleteField("specialOffer")]],
    rcaFormaliteRate: ['', []],
    rcaInsertionRate: ['', []],
    mails: ['', []],
    phones: ['', []],
    responsibleSuscribersNumber: [{ value: '', disabled: true }, []],
    webAccountNumber: [{ value: '', disabled: true }, []],
    instructions: ['', []],
    observations: ['', []],
  });

  openSpecialOffersDialog() {
    let dialogSpecialOffer = this.specialOfferDialog.open(SpecialOffersDialogComponent, {
      width: '90%'
    });
    dialogSpecialOffer.afterClosed().subscribe(response => {
      if (response && response != null)
        this.tiers.specialOffer = response;
    });
  }

  // Check if the propertiy given in parameter is filled when the tiers is an individual
  checkFieldFilledIfIsIndividual(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (this.tiers.isIndividual && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
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
      if (this.tiers.country != null && this.tiers.country.code == COUNTRY_CODE_FRANCE && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  checkVAT(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(fieldName)?.value;
      if (!this.tiers.isIndividual && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0 || !validateVat(fieldValue)))
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

  private _filterEmployee(employees: Employee[], value: string): Employee[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return employees.filter(employee => employee.firstname != undefined && employee.lastname != undefined && employee.firstname.toLowerCase().includes(filterValue) || employee.lastname.toLowerCase().includes(filterValue));
  }

  private _filterByCode(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.code != undefined && input.code.toLowerCase().includes(filterValue));
  }

  private _filterCountry(value: string): Country[] {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return this.countries.filter(country => country.label != undefined && country.label.toLowerCase().includes(filterValue));
  }

  public displayEmployee(employee: Employee): string {
    return employee ? employee.firstname + " " + employee.lastname : '';
  }

  public displayLabel(object: any): string {
    return object ? object.label : '';
  }

  limitTextareaSize(fieldName: string, maxLines: number) {
    let fieldValue = this.principalForm.get(fieldName)?.value != undefined ? this.principalForm.get(fieldName)?.value : "";
    var l = fieldValue.replace(/\r\n/g, "\n").replace(/\r/g, "").split(/\n/g);//split lines
    if (l.length > maxLines) {
      fieldValue = l.slice(0, maxLines).join("\n");
    }

    this.principalForm.get(fieldName)?.setValue(fieldValue);
  }

  fillPostalCode(city: City) {
    if (this.tiers.country == null || this.tiers.country == undefined)
      this.tiers.country = city.country;

    if (this.tiers.country.code == COUNTRY_CODE_FRANCE && city.postalCode != null)
      this.tiers.postalCode = city.postalCode;
  }

  fillCity(postalCode: string) {
    this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
      if (response != null && response != undefined && response.length == 1) {
        let city = response[0];
        if (this.tiers.country == null || this.tiers.country == undefined)
          this.tiers.country = city.country;

        this.tiers.city = city;
      }
    })

  }

  addMail(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    let mail: Mail = {} as Mail;
    if (value && validateEmail(value)) {
      mail.mail = value;
      if (this.tiers.mails == undefined || this.tiers.mails == null)
        this.tiers.mails = [] as Mail[];
      this.tiers.mails.push(mail);
    }
    event.chipInput!.clear();
    this.principalForm.get("mails")?.setValue(null);
  }

  removeMail(inputMail: Mail): void {
    if (this.tiers.mails != undefined && this.tiers.mails != null && this.editMode)
      for (let i = 0; i < this.tiers.mails.length; i++) {
        const mail = this.tiers.mails[i];
        if (mail.mail == inputMail.mail) {
          this.tiers.mails.splice(i, 1);
          return;
        }
      }
  }

  addPhone(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    let phone: Phone = {} as Phone;
    if (value && (validateFrenchPhone(value) || validateInternationalPhone(value))) {
      phone.phoneNumber = value;
      if (this.tiers.phones == undefined || this.tiers.phones == null)
        this.tiers.phones = [] as Phone[];
      this.tiers.phones.push(phone);
    }
    event.chipInput!.clear();
    this.principalForm.get("phones")?.setValue(null);
  }

  removePhone(inputPhone: Phone): void {
    if (this.tiers.phones != undefined && this.tiers.phones != null && this.editMode)
      for (let i = 0; i < this.tiers.phones.length; i++) {
        const phone = this.tiers.phones[i];
        if (phone.phoneNumber == inputPhone.phoneNumber) {
          this.tiers.phones.splice(i, 1);
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

  compareWithId = compareWithId;

  getFormStatus(): boolean {
    this.principalForm.markAllAsTouched();
    if (this.tiers.isIndividual == true) {
      this.tiers.denomination = null;
      this.tiers.intercommunityVat = null;
    }

    if (this.tiers.isIndividual == false) {
      this.tiers.civility = null;
    }
    return this.principalForm.valid;
  }
}
