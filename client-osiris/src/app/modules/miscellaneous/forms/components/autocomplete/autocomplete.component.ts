import { AfterViewInit, Component, ElementRef, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ValidatorFn } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { ConstantService } from '../../../../main/services/constant.service';
import { CityService } from '../../../../profile/services/city.service';
import { CountryService } from '../../../../profile/services/country.service';
import { EmployeeService } from '../../../../profile/services/employee.service';

export const AUTOCOMPLETE_TYPES = ['city', 'zipCode', 'siret', 'employee', 'country'] as string[];
export type AutocompleteType = typeof AUTOCOMPLETE_TYPES[number] | undefined;

@Component({
  selector: 'generic-autocomplete',
  templateUrl: './autocomplete.component.html',
  styleUrls: ['./autocomplete.component.css'],
  standalone: true,
  imports: [...SHARED_IMPORTS]
})
export class AutocompleteComponent implements OnInit, OnChanges, AfterViewInit {

  @Input() formGroup!: FormGroup;
  @Input() model: any;
  @Input() label = '';
  @Input() validators: ValidatorFn[] | undefined;
  @Input() errorMessages: Record<string, string> | undefined;
  @Input() autocompleteType: AutocompleteType
  @Input() isLocalAutocomplete: boolean = true;
  @Output() modelChange = new EventEmitter<any>();
  @Output() onOptionSelected: EventEmitter<any> = new EventEmitter();

  @ViewChild('autoComp', { read: ElementRef }) autoCompEl!: ElementRef;
  @ViewChild('inputField') input: ElementRef | undefined;

  data: any[] = [];
  searchValue: string = '';
  page: number = 0;
  pageSize: number = 10;

  isMandatory: boolean = false;

  fieldName = `field_${Math.random().toString(36).substring(2, 8)}`;

  debounceTime = 200;
  expectedMinLengthInput: number = 3;

  // WARNING : if the value we want to filter the autocomplete is not label, 
  // it has to be changed so the autocomplete manages to fetch the data
  searchKeyword: string = "label";
  isLoading: boolean = false;
  selectedItem: any | undefined;

  constructor(
    private employeeService: EmployeeService,
    private countryService: CountryService,
    private cityService: CityService,
    private constantService: ConstantService,
  ) { }

  get control(): FormControl {
    return this.formGroup.get(this.fieldName) as FormControl;
  }


  ngOnInit() {
    if (this.isLocalAutocomplete) {
      switch (this.autocompleteType) {
        // WARNING : if the value we want to filter the autocomplete is not "label", it has to be 
        // changed so the autocomplete manages to fetch the data ie : change searchKeyword to the value we are filtering
        case "country":
          this.countryService.getCountries().subscribe(res => {
            this.data = res;
          })
          break;
        case "employee":
          this.searchKeyword = "lastname"
          this.employeeService.getEmployees().subscribe(res => {
            this.setData(res);
          })
          break;
      }
    }

    if (!this.formGroup.contains(this.fieldName)) {
      this.formGroup.addControl(
        this.fieldName,
        new FormControl(this.model, this.validators)
      );
    }

    this.control.valueChanges.subscribe(value => {
      if (value !== this.model) {
        this.modelChange.emit(value);
      }
    });
  }

  ngAfterViewInit() {
    setTimeout(() => {
      const clearButton = this.autoCompEl.nativeElement.querySelector('i[aria-label="Close"]');
      if (clearButton) {
        clearButton.addEventListener('click', () => {
          this.clearField();
        });
      }

      const inputEl = this.autoCompEl?.nativeElement?.querySelector('input');
      if (inputEl) {
        inputEl.setAttribute('autocomplete', 'off');
        inputEl.setAttribute('autocorrect', 'off');
        inputEl.setAttribute('autocapitalize', 'off');
        inputEl.setAttribute('spellcheck', 'false');
        inputEl.setAttribute('autocomplete', 'nope');
        if (this.isMandatory)
          inputEl.required = true;
        inputEl.classList.add('custom-class-input');
      }
    }, 0);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['model'] && !this.selectedItem) {
      const newValue = changes['model'].currentValue;
      this.selectedItem = newValue;
      if (this.data && this.data[0]) {
        const matchedItem = this.data.find(item => (item as any).id === newValue.id);
        if (matchedItem) {
          this.selectedItem = matchedItem;
          this.control.setValue(matchedItem, { emitEvent: false });
        } else {
          this.data.push(newValue);
          this.selectedItem = newValue;
          this.control.setValue(newValue, { emitEvent: false });
        }
      }
    }
  }

  fetchNextPage() {
    this.page++;
    this.fetchData();
  }

  onChangeSearch(newVal: string) {
    // Only for remote, to fetch new values
    if (!this.isLocalAutocomplete) {
      this.searchValue = newVal;
      this.selectedItem = undefined;
      this.page = 0;
      this.data = [];
      if (this.formGroup) {
        this.fetchData();
      }
    }
  }

  fetchData() {
    this.isLoading = true;
    switch (this.autocompleteType) {
      // WARNING : if the value we want to filter the autocomplete is not "label", it has to be 
      // changed so the autocomplete manages to fetch the data ie : change searchKeyword to the value we are filtering
      case "city":
        this.cityService.getCitiesFilteredByNameAndCountryAndPostalCode(this.searchValue, this.constantService.getCountryFrance(), "", this.page, this.pageSize).subscribe(res => {
          this.data = this.data.concat(res.content);
          this.isLoading = false;
        })
        break;
    }
  }
  // Emit event to parents
  optionSelected(model: any) {
    this.model = model;
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(model);
  }

  setData(values: any) {
    this.data = values;
  }

  displayLabel(object: any): string {
    if (object && object.lastname)
      return object.lastname;
    if (object && object.label)
      return object.label;
    if (typeof object === "string")
      return object;
    return "";
  }

  clearField(): void {
    this.searchValue = '';
    this.selectedItem = undefined;
    this.page = 0;
    this.model = undefined;
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(undefined);
    if (this.input)
      this.input.nativeElement.value = "";
    if (this.formGroup) {
      this.data = [];
      this.formGroup.markAllAsTouched();
      this.formGroup.updateValueAndValidity();
    }
  }


  get errors(): string[] {
    if (!this.control || !this.control.errors || !this.control.touched || !this.errorMessages) return [];
    return Object.keys(this.control.errors).map(
      key => this.errorMessages![key] ?? key
    );
  }
}
