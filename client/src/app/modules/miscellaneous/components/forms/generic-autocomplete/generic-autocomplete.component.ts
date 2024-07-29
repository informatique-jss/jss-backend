import { Directive, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { Observable } from 'rxjs';
import { debounceTime, filter, switchMap, tap } from 'rxjs/operators';
import { GenericFormComponent } from '../generic-form.components';
import { AppService } from '../../../../../services/app.service';

@Directive()
export abstract class GenericAutocompleteComponent<T, U> extends GenericFormComponent implements OnInit {

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

  expectedMinLengthInput: number = 2;

  filteredTypes: T[] | undefined;

  doNotOpenTwice: boolean = false;

  isLoading: boolean = false;

  @ViewChild(MatAutocompleteTrigger) trigger: MatAutocompleteTrigger | undefined;

  @Input() fieldToCheckAgainstForValidation: string = "id";

  constructor(private formBuilder3: UntypedFormBuilder, private appService2: AppService
  ) {
    super(formBuilder3, appService2);
  }

  callOnNgInit(): void {
  }

  ngOnInit() {
    if (this.form != undefined) {
      this.form.addControl(this.propertyName, this.formBuilder3.control({ value: '', disabled: this.isDisabled }));
      this.form.addValidators(this.checkField());
      this.form.addValidators(this.checkAutocompleteField());
      this.form.get(this.propertyName)?.valueChanges.pipe(
        filter(res => {
          if (!this.model && res != undefined && res !== null && res.length >= this.expectedMinLengthInput) {
            this.isLoading = true;
            return true;
          }
          return false;
        }),
        debounceTime(100),
        tap((value) => {
          this.filteredTypes = [];
          this.modelChange.emit(this.model);
        }),
        switchMap(value => this.searchEntities(value)
        )
      ).subscribe(response => {
        this.filteredTypes = this.mapResponse(response);

        if (this.filteredTypes)
          this.filteredTypes.sort((a, b) => this.displayLabel(a).localeCompare(this.displayLabel(b)));

        this.isLoading = false;
        if (!this.isDisabled && !this.doNotOpenTwice)
          this.trigger?.openPanel();
      });
      this.form.get(this.propertyName)?.setValue(this.model);
      this.callOnNgInit();
    }
  }

  abstract searchEntities(value: string): Observable<U[]>;

  mapResponse(response: U[]): T[] {
    return (response as unknown) as Array<T>;
  }

  optionSelected(type: T): void {
    this.model = type;
    this.doNotOpenTwice = true;
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(type);
    this.form?.updateValueAndValidity();
  }

  checkAutocompleteField(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValue = root.get(this.propertyName)?.value;
      if (this.form && this.form!.get(this.propertyName)) {
        if (this.conditionnalRequired != undefined) {
          if (this.conditionnalRequired && !this.byPassAutocompletValidator && (!fieldValue || fieldValue[this.fieldToCheckAgainstForValidation] == null)) {
            this.form!.get(this.propertyName)!.setErrors({ notFilled: this.propertyName });
            return {
              notFilled: this.propertyName
            };
          }
        } else if (this.isMandatory && !this.byPassAutocompletValidator && (!fieldValue || fieldValue[this.fieldToCheckAgainstForValidation] == null)) {
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
    this.model = undefined;
    this.doNotOpenTwice = false;
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

  getPreviewActionLinkFunction(entity: T): string[] | undefined {
    return undefined;
  }
}
