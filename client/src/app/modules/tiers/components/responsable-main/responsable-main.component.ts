import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, map, startWith, switchMap, tap } from 'rxjs/operators';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { AppService } from 'src/app/app.service';
import { compareWithId, isTiersTypeProspect } from 'src/app/libs/CompareHelper';
import { COUNTRY_CODE_FRANCE, SEPARATOR_KEY_CODES, SUSCRIPTION_TYPE_CODE_PERIODE_12M } from 'src/app/libs/Constants';
import { validateEmail, validateFrenchPhone, validateInternationalPhone } from 'src/app/libs/CustomFormsValidatorsHelper';
import { callNumber, prepareMail } from 'src/app/libs/MailHelper';
import { instanceOfResponsable } from 'src/app/libs/TypeHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { Country } from 'src/app/modules/miscellaneous/model/Country';
import { Mail } from 'src/app/modules/miscellaneous/model/Mail';
import { Phone } from 'src/app/modules/miscellaneous/model/Phone';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { CivilityService } from 'src/app/modules/miscellaneous/services/civility.service';
import { CountryService } from 'src/app/modules/miscellaneous/services/country.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { RESPONSABLE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { Civility } from '../../../miscellaneous/model/Civility';
import { Language } from '../../../miscellaneous/model/Language';
import { LanguageService } from '../../../miscellaneous/services/language.service';
import { JssSubscription } from '../../model/JssSubscription';
import { Responsable } from '../../model/Responsable';
import { SubscriptionPeriodType } from '../../model/SubscriptionPeriodType';
import { Tiers } from '../../model/Tiers';
import { TiersCategory } from '../../model/TiersCategory';
import { TiersType } from '../../model/TiersType';
import { SubscriptionPeriodTypeService } from '../../services/subscription.period.type.service';
import { TiersCategoryService } from '../../services/tiers.category.service';
import { TiersService } from '../../services/tiers.service';
import { TiersTypeService } from '../../services/tiers.type.service';
import { SettlementBillingComponent } from '../settlement-billing/settlement-billing.component';

@Component({
  selector: 'responsable-main',
  templateUrl: './responsable-main.component.html',
  styleUrls: ['./responsable-main.component.css']
})

export class ResponsableMainComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() tiers: Tiers = {} as Tiers;
  @Input() editMode: boolean = false;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild('tabs', { static: false }) tabs: any;

  displayedColumns: string[] = ['id', 'name', 'address', 'city', 'salesEmployee', 'formalisteEmployee', 'insertionEmployee', 'actions'];

  responsableDataSource: MatTableDataSource<Responsable> = new MatTableDataSource<Responsable>();

  RESPONSABLE_ENTITY_TYPE = RESPONSABLE_ENTITY_TYPE;

  filterValue: string = "";

  selectedResponsable: Responsable | null = null;

  selectedResponsableId: number | null = null;

  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;
  SUSCRIPTION_TYPE_CODE_PERIODE_12M = SUSCRIPTION_TYPE_CODE_PERIODE_12M;

  civilities: Civility[] = [] as Array<Civility>;
  languages: Language[] = [] as Array<Language>;

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

  tiersTypes: TiersType[] = [] as Array<TiersType>;
  tiersCategories: TiersCategory[] = [] as Array<TiersCategory>;
  subscriptionPeriodTypes: SubscriptionPeriodType[] = [] as Array<SubscriptionPeriodType>;

  isSubscriptionPaper: boolean = false;
  isSubscriptionWeb: boolean = false;

  @ViewChild(SettlementBillingComponent) documentSettlementBillingComponent: SettlementBillingComponent | undefined;

  constructor(private formBuilder: FormBuilder,
    private civilityService: CivilityService,
    private languageService: LanguageService,
    private employeeService: EmployeeService,
    private cityService: CityService,
    private countryService: CountryService,
    private appService: AppService,
    protected tiersService: TiersService,
    protected tiersTypeService: TiersTypeService,
    private snackBar: MatSnackBar,
    protected subscriptionPeriodTypeService: SubscriptionPeriodTypeService,
    protected tiersCategoryService: TiersCategoryService) { }

  // TODO : reprendre les RG (notamment facturation / commande) lorsque les modules correspondants seront faits

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tiers != undefined && this.tiers.responsables != undefined && this.tiers.responsables != null) {
      this.principalForm.markAllAsTouched();
      this.setDataTable();
      this.initDefaultValues();
      this.toggleTabs();
      if (this.selectedResponsableId != null)
        this.selectResponsable(this.selectedResponsableId);
    }
  }

  ngOnInit() {
    // Referential loading
    this.languageService.getLanguages().subscribe(response => {
      this.languages = response;
    })
    this.countryService.getCountries().subscribe(response => {
      this.countries = response;
    })

    this.civilityService.getCivilities().subscribe(response => {
      this.civilities = response;
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
    this.tiersTypeService.getTiersTypes().subscribe(response => {
      this.tiersTypes = response;
    })
    this.tiersCategoryService.getTiersCategories().subscribe(response => {
      this.tiersCategories = response;
    })
    this.subscriptionPeriodTypeService.getSubscriptionPeriodTypes().subscribe(response => {
      this.subscriptionPeriodTypes = response;
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

  toggleTabs() {
    if (this.tabs != undefined)
      this.tabs.realignInkBar();
  }

  setDataTable() {
    this.tiers.responsables.sort(function (a: Responsable, b: Responsable) {
      return (a.firstname + "" + a.lastname).localeCompare(b.firstname + "" + a.lastname);
    });

    this.responsableDataSource = new MatTableDataSource(this.tiers.responsables);
    setTimeout(() => {
      this.responsableDataSource.sort = this.sort;
      this.responsableDataSource.sortingDataAccessor = (item: Responsable, property) => {
        switch (property) {
          case 'id': return item.id;
          case 'name': return item.firstname + "" + item.lastname;
          case 'address': return item.address + "";
          case 'city': return item.city.label + "";
          case 'salesEmployee': return item.salesEmployee?.firstname + "" + item.salesEmployee?.lastname;
          case 'formalisteEmployee': return item.formalisteEmployee?.firstname + "" + item.formalisteEmployee?.lastname;
          case 'insertionEmployee': return item.insertionEmployee?.firstname + "" + item.insertionEmployee?.lastname;
          default: return item.firstname + "" + item.lastname;
        }
      };

      this.responsableDataSource.filterPredicate = (data: any, filter) => {
        const dataStr = JSON.stringify(data).toLowerCase();
        return dataStr.indexOf(filter) != -1;
      }
      this.toggleTabs();
    });
  }

  setSelectedResponsableId(selectedResponsableId: number) {
    this.selectedResponsableId = selectedResponsableId;
  }

  selectResponsable(responsableId: number) {
    if (this.selectedResponsable == null || this.getFormStatus()) {
      this.tiers.responsables.forEach(responsable => {
        if (responsable.id == responsableId) {
          this.selectedResponsable = responsable;
          this.tiersService.setCurrentViewedResponsable(responsable);
          this.toggleTabs();
          this.initDefaultValues();
          if (this.tiers.denomination != null) {
            this.appService.changeHeaderTitle(this.tiers.denomination + " - " + (this.selectedResponsable.firstname != null ? (this.selectedResponsable.firstname + " " + this.selectedResponsable.lastname) : ""));
          } else if (this.tiers.firstname != null) {
            this.appService.changeHeaderTitle(this.tiers.firstname + " " + this.tiers.lastname + " - " + (this.selectedResponsable.firstname != null ? (this.selectedResponsable.firstname + " " + this.selectedResponsable.lastname) : ""));
          }
        }
      })
    } else {
      let sb = this.snackBar.open("Compléter la saisie du responsable courant avant de continuer", 'Fermer', {
        duration: 50 * 1000, panelClass: ["red-snackbar"]
      });
    }
  }

  deleteResponsable(responsableRow: any) {
    let hash = JSON.stringify(responsableRow).toLowerCase();
    for (let i = 0; i < this.tiers.responsables.length; i++) {
      let responsable = this.tiers.responsables[i];
      if (JSON.stringify(responsable).toLowerCase() == hash) {
        this.tiers.responsables.splice(i, 1);
        this.selectedResponsable = null;
        this.tiersService.setCurrentViewedResponsable(null);
        this.setDataTable();
        return;
      }
    }
  }

  addResponsable() {
    if (this.selectedResponsable == null || this.getFormStatus()) {
      this.selectedResponsable = {} as Responsable;
      this.tiers.responsables.push(this.selectedResponsable);
      this.tiersService.setCurrentViewedResponsable(this.selectedResponsable);
      this.setDataTable();
      this.toggleTabs();
      this.initDefaultValues();
    } else {
      let sb = this.snackBar.open("Compléter la saisie du responsable courant avant de continuer", 'Fermer', {
        duration: 50 * 1000, panelClass: ["red-snackbar"]
      });
    }
  }

  initDefaultValues() {
    if (this.selectedResponsable && !this.selectedResponsable.civility)
      this.selectedResponsable.civility = this.civilities[0];
    if (this.selectedResponsable != null && (this.selectedResponsable.language == undefined || this.selectedResponsable.language == null))
      this.selectedResponsable.language = this.languages[0];
    if (this.selectedResponsable != null && (this.selectedResponsable.country == null || this.selectedResponsable.country == undefined))
      this.selectedResponsable.country = this.countries[0];
    if (this.selectedResponsable != null && (this.selectedResponsable.isActive == null || this.selectedResponsable.isActive == undefined))
      this.selectedResponsable.isActive = false;
    if (this.selectedResponsable != null && (this.selectedResponsable.isBouclette == null || this.selectedResponsable.isBouclette == undefined))
      this.selectedResponsable.isBouclette = false;

    if (this.selectedResponsable != null && (this.selectedResponsable?.jssSubscription == null || this.selectedResponsable.jssSubscription == undefined)) {
      this.selectedResponsable.jssSubscription = { isPaperSubscription: false, isWebSubscription: false } as JssSubscription;
    }
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    filterValue = filterValue.toLowerCase();
    this.responsableDataSource.filter = filterValue;
  }

  isResponsableTypeProspect(): boolean {
    return isTiersTypeProspect(this.tiers);
  }

  principalForm = this.formBuilder.group({
    responsableId: [{ value: '', disabled: true }],
    firstBilling: [{ value: '', disabled: true }],
    civility: ['', Validators.required],
    firstname: ['', [Validators.required, Validators.maxLength(20)]],
    lastname: ['', [Validators.required, Validators.maxLength(20)]],
    isActive: [''],
    tiersType: ['', Validators.required],
    tiersCategory: [''],
    idTiers: [{ value: '', disabled: true }],
    salesEmployee: ['', [Validators.required, this.checkAutocompleteField("salesEmployee")]],
    formalisteEmployee: ['', [this.checkAutocompleteField("formalisteEmployee")]],
    insertionEmployee: ['', [this.checkAutocompleteField("insertionEmployee")]],
    function: ['', Validators.maxLength(20)],
    mailRecipient: [''],
    language: ['', Validators.required],
    address: ['', [Validators.required, Validators.maxLength(20)]],
    postalCode: ['', [this.checkFieldFilledIfIsInFrance("postalCode")]],
    city: ['', [Validators.required, Validators.maxLength(30)]],
    country: ['', [Validators.required, this.checkAutocompleteField("country")]],
    building: ['', Validators.maxLength(20)],
    floor: ['', Validators.maxLength(8)],
    subscriptionPeriodType: ['', this.checkFieldFilledIfIsSubscription("subscriptionPeriodType")],
    jssSubscription1: ['', []],
    jssSubscription2: ['', []],
    rcaFormaliteRate: ['', []],
    rcaInsertionRate: ['', []],
    mails: ['', []],
    phones: ['', []],
    observations: ['', []],
    isBouclette: ['', []],
  });

  checkFieldFilledIfIsInFrance(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;
      const fieldValue = root.get(fieldName)?.value;
      if (this.selectedResponsable != null && this.selectedResponsable.country != null && this.selectedResponsable.country.code == COUNTRY_CODE_FRANCE && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  checkFieldFilledIfIsSubscription(fieldName: string): ValidationErrors | null {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;
      const fieldValue = root.get(fieldName)?.value;
      if (this.selectedResponsable != null && this.selectedResponsable.jssSubscription != null && (this.selectedResponsable.jssSubscription.isPaperSubscription || this.selectedResponsable.jssSubscription.isWebSubscription) && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
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
    if (this.selectedResponsable != null) {
      if (this.selectedResponsable.country == null || this.selectedResponsable.country == undefined)
        this.selectedResponsable.country = city.country;

      if (this.selectedResponsable.country.code == COUNTRY_CODE_FRANCE && city.postalCode != null)
        this.selectedResponsable.postalCode = city.postalCode;
    }
  }

  fillCity(postalCode: string) {
    if (this.selectedResponsable != null) {
      this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
        if (response != null && response != undefined && response.length == 1) {
          let city = response[0];
          if (this.selectedResponsable != null) {
            if (this.selectedResponsable.country == null || this.selectedResponsable.country == undefined)
              this.selectedResponsable.country = city.country;

            this.selectedResponsable.city = city;
          }
        }
      })
    }
  }

  addMail(event: MatChipInputEvent): void {
    if (this.selectedResponsable != null) {
      const value = (event.value || '').trim();
      let mail: Mail = {} as Mail;
      if (value && validateEmail(value)) {
        mail.mail = value;
        if (this.selectedResponsable.mails == undefined || this.selectedResponsable.mails == null)
          this.selectedResponsable.mails = [] as Mail[];
        this.selectedResponsable.mails.push(mail);
      }
      event.chipInput!.clear();
      this.principalForm.get("mails")?.setValue(null);
    }
  }

  removeMail(inputMail: Mail): void {
    if (this.selectedResponsable != null) {
      if (this.selectedResponsable.mails != undefined && this.selectedResponsable.mails != null && this.editMode)
        for (let i = 0; i < this.selectedResponsable.mails.length; i++) {
          const mail = this.selectedResponsable.mails[i];
          if (mail.mail == inputMail.mail) {
            this.selectedResponsable.mails.splice(i, 1);
            return;
          }
        }
    }
  }

  addPhone(event: MatChipInputEvent): void {
    if (this.selectedResponsable != null) {
      const value = (event.value || '').trim();
      let phone: Phone = {} as Phone;
      if (value && (validateFrenchPhone(value) || validateInternationalPhone(value))) {
        phone.phoneNumber = value;
        if (this.selectedResponsable.phones == undefined || this.selectedResponsable.phones == null)
          this.selectedResponsable.phones = [] as Phone[];
        this.selectedResponsable.phones.push(phone);
      }
      event.chipInput!.clear();
      this.principalForm.get("phones")?.setValue(null);
    }
  }

  removePhone(inputPhone: Phone): void {
    if (this.selectedResponsable != null) {
      if (this.selectedResponsable.phones != undefined && this.selectedResponsable.phones != null && this.editMode)
        for (let i = 0; i < this.selectedResponsable.phones.length; i++) {
          const phone = this.selectedResponsable.phones[i];
          if (phone.phoneNumber == inputPhone.phoneNumber) {
            this.selectedResponsable.phones.splice(i, 1);
            return;
          }
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
  instanceOfResponsable = instanceOfResponsable;

  getFormStatus(): boolean {
    let status = true;
    if (this.selectedResponsable != null) {
      let documentSettlementBillingFormStatus = this.documentSettlementBillingComponent?.getFormStatus();
      this.principalForm.markAllAsTouched();

      if (this.selectedResponsable != null && (this.selectedResponsable.isBouclette == null || this.selectedResponsable.isBouclette == undefined))
        this.selectedResponsable.isBouclette = false;

      if (this.selectedResponsable?.jssSubscription != undefined && this.selectedResponsable.jssSubscription.isPaperSubscription)
        this.selectedResponsable.jssSubscription.isWebSubscription = true;

      status = status && this.principalForm.valid && documentSettlementBillingFormStatus!;
    }
    return status;
  }
}
