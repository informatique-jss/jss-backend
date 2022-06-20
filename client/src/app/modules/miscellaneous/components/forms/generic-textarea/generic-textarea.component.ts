import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';


@Component({
  selector: 'generic-textarea',
  templateUrl: './generic-textarea.component.html',
  styleUrls: ['./generic-textarea.component.css']
})
export class GenericTextareaComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();

  /**
   * The model of input property
   * Mandatory
   */
  @Input() model: any | undefined;
  @Output() modelChange: EventEmitter<any> = new EventEmitter<any>();
  /**
   * The label to display
   * Mandatory
   */
  @Input() label: string = "";
  /**
   * The formgroup to bind component
   * Mandatory
   */
  @Input() form: UntypedFormGroup | undefined;
  /**
   * The name of the input
   * Default : input
   */
  @Input() propertyName: string = "input";
  /**
   * Indicate if the field is required or not in the formgroup provided
   * Default : false
   */
  @Input() isMandatory: boolean = false;
  /**
 * Indicate if the field is disabled or not in the formgroup provided
 * Default : false
 */
  @Input() isDisabled: boolean = false;
  /**
   * Add condition to check if the field is required.
   * If true (and isMandatory is also true), Validators.required is applied
   * If false, Validators.required is not applied regardless the value of isMandatory
   * Default : undefined
   */
  @Input() conditionnalRequired: boolean | undefined;
  /**
   * Additionnal validators to check
   */
  @Input() customValidators: ValidatorFn[] | undefined;
  /**
  * Event triggered on keyup, keydown and paste
  */
  @Output() filterInput: EventEmitter<any> = new EventEmitter();
  /**
 * Number of lines to display
 * Default : 3
 */
  @Input() numberOfLines: number = 3;

  constructor(
    private formBuilder: UntypedFormBuilder) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.isDisabled) {
      if (this.isDisabled) {
        this.form?.get(this.propertyName)?.disable();
      } else {
        this.form?.get(this.propertyName)?.enable();
      }
    }
    if (this.form && (this.isMandatory || this.customValidators)) {
      this.form.get(this.propertyName)?.updateValueAndValidity();
    }
    if (changes.model && this.form != undefined) {
      this.form.get(this.propertyName)?.setValue(this.model);
    }
  }

  ngOnDestroy() {
    if (this.form != undefined)
      this.form.removeControl(this.propertyName);
  }

  ngOnInit() {
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
      this.form.get(this.propertyName)!.valueChanges.subscribe(
        (newValue) => {
          if (newValue != undefined) {
            this.model = newValue;
            this.modelChange.emit(this.model);
          }
        }
      )
      this.form.get(this.propertyName)?.setValue(this.model);
      this.form.markAllAsTouched();
    }
  }

  // Check if the propertiy given in parameter is filled when conditionnalRequired is set
  checkFieldFilledIfIsConditionalRequired(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValue = root.get(this.propertyName)?.value;
      if (this.conditionnalRequired && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  onInputChange(): void {
    this.modelChange.emit(this.model);
    this.filterInput.emit();
  }
}
