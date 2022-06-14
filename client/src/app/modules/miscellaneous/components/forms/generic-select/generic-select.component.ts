import { ChangeDetectorRef, Directive, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { compareWithId } from 'src/app/libs/CompareHelper';

@Directive()
export abstract class GenericSelectComponent<T> implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();

  /**
   * The model of T property
   * Mandatory
   */
  @Input() model: T = {} as T;
  @Output() modelChange: EventEmitter<T> = new EventEmitter<T>();
  /**
  * The label to display
  * Mandatory
  */
  @Input() label: string = "";
  /**
   * The formgroup to bind component
   * Mandatory
   */
  @Input() form: FormGroup | undefined;
  /**
   * The name of the input
   * Default : T
   */
  @Input() propertyName: string = "T";
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

  abstract types: T[];

  constructor(
    private cdr: ChangeDetectorRef,
    private formBuilder: FormBuilder) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.model)
      this.cdr.detectChanges();
    if (changes.isDisabled) {
      if (this.isDisabled) {
        this.form?.get(this.propertyName)?.disable();
      } else {
        this.form?.get(this.propertyName)?.enable();
      }
    }
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
      if (this.isMandatory) {
        if (this.conditionnalRequired != undefined) {
          validators.push(this.checkFieldFilledIfIsConditionalRequired());
        } else {
          validators.push(Validators.required);
        }
      }

      if (this.customValidators != undefined && this.customValidators != null && this.customValidators.length > 0)
        validators.push(...this.customValidators);

      this.form.addControl(this.propertyName, this.formBuilder.control({ value: '', disabled: this.isDisabled }, validators));
      this.form.get(this.propertyName)?.valueChanges.subscribe(
        (newValue) => {
          this.model = newValue;
          this.modelChange.emit(this.model);
        }
      );
      this.form.markAllAsTouched();
    }
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

  abstract initTypes(): void;

  compareWithId = compareWithId;
}
