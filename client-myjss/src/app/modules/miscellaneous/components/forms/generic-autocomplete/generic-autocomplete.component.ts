import { Directive, ElementRef, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { Observable } from 'rxjs';
import { PagedContent } from '../../../model/PagedContent';
import { GenericFormComponent } from '../generic-form.components';

@Directive()
export abstract class GenericAutocompleteComponent<T, U> extends GenericFormComponent implements OnInit {

  @ViewChild('autoComp', { read: ElementRef }) autoCompEl!: ElementRef;

  /**
   * Fired when an option is selected in the autocomplete list.
   * Give the selected object in parameter
   */
  @Output() onOptionSelected: EventEmitter<T> = new EventEmitter();
  /**
  * Override matFormField class
  * Default = full-width
  */
  @Input() matFormFieldClass: string = "full-width";

  /**
* Hint to display
*/
  @Input() hint: string = "";

  @Input() byPassAutocompletValidator: boolean = false;

  @ViewChild('inputField') input: ElementRef | undefined;

  expectedMinLengthInput: number = 3;

  filteredTypes: T[] = [];

  isLoading: boolean = false;

  @Input() fieldToCheckAgainstForValidation: string = "id";

  searchValue: string = '';
  page: number = 0;
  pageSize: number = 10;

  selectedItem: T | undefined;
  constructor(private formBuilder3: UntypedFormBuilder) {
    super(formBuilder3);
  }

  callOnNgInit(): void {
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

  override ngOnChanges(changes: SimpleChanges): void {
    if (changes['model'] && !this.selectedItem) {
      const newValue = changes['model'].currentValue;
      const matchedItem = this.filteredTypes.find(item => (item as any).id === newValue.id);
      if (matchedItem) {
        this.selectedItem = matchedItem;
      } else {
        this.filteredTypes.push(newValue);
        this.selectedItem = newValue;
      }
    }
  }

  override ngOnInit() {
    if (this.form != undefined) {
      this.form.addControl(this.propertyName, this.formBuilder3.control({ value: this.model, disabled: this.isDisabled, }));
      this.callOnNgInit();
    }
  }

  abstract searchEntities(value: string): Observable<PagedContent<U>>;

  mapResponse(response: U[]): T[] {
    return (response as unknown) as Array<T>;
  }

  onChangeSearch(newVal: string) {
    this.searchValue = newVal;
    this.selectedItem = undefined;
    this.page = 0;
    this.filteredTypes = [];
    if (this.form) {
      this.isLoading = true;
      this.searchEntities(newVal).subscribe(response => {
        this.filteredTypes = this.mapResponse(response.content);
        this.isLoading = false;
      })
    }
  }

  getFormStatus() {
    return this.form?.status;
  }

  optionSelected(type: T): void {
    this.model = type;
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(type);
    this.form?.updateValueAndValidity();
    setTimeout(() => {
      const clearButton = this.autoCompEl.nativeElement.querySelector('i[aria-label="Close"]');
      if (clearButton) {
        clearButton.addEventListener('click', () => {
          this.clearField();
        });
      }
    }, 0);
  }

  fetchNextPage() {
    if (this.form) {
      this.isLoading = true;
      this.page++;
      this.searchEntities(this.searchValue).subscribe(response => {
        this.filteredTypes = this.filteredTypes.concat(this.mapResponse(response.content));
        this.isLoading = false;
      })
    }
  }

  checkAutocompleteField(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValue = root.get(this.propertyName)?.value;
      if (this.form && this.form!.get(this.propertyName)) {
        if (this.isMandatory && !this.byPassAutocompletValidator && (!fieldValue || fieldValue[this.fieldToCheckAgainstForValidation] == null)) {
          this.form!.get(this.propertyName)!.setErrors({ notFilled: this.propertyName });
          return {
            notFilled: this.propertyName
          };
        }
        this.form!.get(this.propertyName)!.setErrors(null);
      }
      return null;
    };
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
    if (this.form) {
      this.form.get(this.propertyName)?.setValue(null);
      this.filteredTypes = [];
      this.form.markAllAsTouched();
      this.form.updateValueAndValidity();
    }
  }

}
