import { Directive, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { CustomErrorStateMatcher } from 'src/app/app.component';

@Directive()
export abstract class GenericLocalAutocompleteComponent<T> implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();

  /**
   * The model of T property
   * Mandatory
   */
  @Input() model: T | undefined;
  @Output() modelChange: EventEmitter<T> = new EventEmitter<T>();
  /**
   * The formgroup to bind component
   * Mandatory
   */
  @Input() form: FormGroup | undefined;
  /**
   * The name of the input
   * local-autocomplete by default
   */
  @Input() propertyName: string = "local-autocomplete";
  /**
   * Indicate if the field is required or not in the formgroup provided
   * Default : false
   */
  @Input() isMandatory: boolean = false;
  /**
* Indicate if the field is disabled or not in the formgroup provided
* Must be defined everytime because disabled is not inherited from parent fieldset state
* Default : false
*/
  @Input() isDisabled: boolean = false;
  /**
   * Add condition to check if the field is required.
   * If true (and isMandatory is also true), Validators.required is applied
   * If false, Validators.required is not applied regardless the value of isMandatory
   * Default : true
   */
  @Input() conditionnalRequired: boolean = true;
  /**
   * Additionnal validators to check
   */
  @Input() customValidators: ValidatorFn[] | undefined;
  /**
   * Fired when an option is selected in the autocomplete list.
   * Give the selected City in parameter
   */
  @Output() onOptionSelected: EventEmitter<T> = new EventEmitter();

  abstract types: T[];
  filteredTypes: Observable<T[]> | undefined;

  constructor(private formBuilder: FormBuilder) { }

  ngOnChanges(changes: SimpleChanges) {
    if (this.form && (this.isMandatory || this.customValidators))
      this.form.get(this.propertyName)?.updateValueAndValidity();
  }

  ngOnDestroy() {
    if (this.form != undefined)
      this.form.removeControl(this.propertyName);
  }

  ngOnInit() {
    this.initTypes();
    if (this.form != undefined) {
      let validators: ValidatorFn[] = [] as Array<ValidatorFn>;
      validators.push(this.checkAutocompleteField());
      if (this.isMandatory) {
        if (this.conditionnalRequired != undefined) {
          validators.push(this.checkFieldFilledIfIsConditionalRequired());
        } else {
          validators.push(Validators.required);
        }
      }

      if (this.customValidators != undefined && this.customValidators != null && this.customValidators.length > 0)
        validators.push(...this.customValidators);

      this.form.addControl(this.propertyName, this.formBuilder.control('', validators));
      this.filteredTypes = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => this.filterEntities(this.types, value)),
      );
      this.form.markAllAsTouched();
    }
  }

  abstract filterEntities(types: T[], value: string): T[];

  checkAutocompleteField(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(this.propertyName)?.value;
      if (fieldValue != undefined && fieldValue != null && (fieldValue.id == undefined || fieldValue.id == null))
        return {
          notFilled: true
        };
      return null;
    };
  }

  // Check if the propertiy given in parameter is filled when conditionnalRequired is set
  checkFieldFilledIfIsConditionalRequired(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;

      const fieldValue = root.get(this.propertyName)?.value;
      if (this.conditionnalRequired && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  displayLabel(object: any): string {
    return object ? object.label : '';
  }

  optionSelected(type: T): void {
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(type);
  }

  abstract initTypes(): void;

}
