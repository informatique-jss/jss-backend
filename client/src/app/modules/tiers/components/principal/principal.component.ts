import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { Civility } from '../../../miscellaneous/model/Civility';
import { Tiers } from '../../model/Tiers';
import { TiersCategory } from '../../model/TiersCategory';
import { TiersType } from '../../model/TiersType';
import { TiersCategoryService } from '../../services/tiers.category.service';
import { TiersTypeService } from '../../services/tiers.type.service';
import { LanguageService } from '../../../miscellaneous/services/language.service';
import { CivilityService } from 'src/app/modules/miscellaneous/services/civility.service';
import { Language } from 'src/app/modules/miscellaneous/model/Language';
import { DeliveryService } from 'src/app/modules/miscellaneous/model/DeliveryService';
import { DeliveryServiceService } from 'src/app/modules/miscellaneous/services/delivery.service.service';

@Component({
  selector: 'tiers-principal',
  templateUrl: './principal.component.html',
  styleUrls: ['./principal.component.css']
})

export class PrincipalComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();

  @Input() tiers: Tiers = {} as Tiers;
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

  constructor(private formBuilder: FormBuilder,
    private tiersTypeService: TiersTypeService,
    private tiersCategoryService: TiersCategoryService,
    private civilityService: CivilityService,
    private languageService: LanguageService,
    private deliveryServiceService: DeliveryServiceService,
    private employeeService: EmployeeService) { }

  ngOnInit() {
    // Referential loading
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
    this.languageService.getLanguages().subscribe(response => {
      this.languages = response;
      // TODO : put it in tiers loading to avoid null
      if (this.tiers.language == undefined || this.tiers.language == null)
        this.tiers.language = this.languages[0];
    })
    this.deliveryServiceService.getDeliveryServices().subscribe(response => {
      this.deliveryServices = response;
      // TODO : put it in tiers loading to avoid null
      this.tiers.deliveryService = this.deliveryServices[0];
    })

    // Set default value
    if (this.tiers.isIndividual == undefined || this.tiers.isIndividual == null)
      this.tiers.isIndividual = false;

    // Initialize autocomplete fields
    this.filteredSalesEmployees = this.generatorForm.get("salesEmployee")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterEmployee(this.salesEmployees, value)),
    );

    this.filteredFormalisteEmployees = this.generatorForm.get("formalisteEmployee")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterEmployee(this.formalisteEmployees, value)),
    );

    this.filteredInsertionEmployees = this.generatorForm.get("insertionEmployee")?.valueChanges.pipe(
      startWith(''),
      map(value => this._filterEmployee(this.insertionEmployees, value)),
    );
  }

  generatorForm = this.formBuilder.group({
    tiersType: ['', Validators.required],
    tiersId: [{ value: '', disabled: true }],
    denomination: ['', [Validators.required, Validators.maxLength(60)]],
    firstBilling: [''],
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
  });



  saveTiers() {
    console.log(this.tiers);
    if (this.tiers.isIndividual == true)
      this.tiers.denomination = null;

    if (this.tiers.isIndividual == false)
      this.tiers.civility = null;

    if (this.generatorForm.valid) {
    }
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
    const filterValue = value != undefined ? value.toLowerCase() : "";
    return employees.filter(employee => employee.firstname != undefined && employee.lastname != undefined && employee.firstname.toLowerCase().includes(filterValue) || employee.lastname.toLowerCase().includes(filterValue));
  }

  public displayEmployee(employee: Employee): string {
    return employee ? employee.firstname + " " + employee.lastname : '';
  }

  limitTextareaSize(fieldName: string, maxLines: number) {
    let fieldValue = this.generatorForm.get(fieldName)?.value != undefined ? this.generatorForm.get(fieldName)?.value : "";
    var l = fieldValue.replace(/\r\n/g, "\n").replace(/\r/g, "").split(/\n/g);//split lines
    if (l.length > maxLines) {
      fieldValue = l.slice(0, maxLines).join("\n");
    }

    this.generatorForm.get(fieldName)?.setValue(fieldValue);
  }
}
