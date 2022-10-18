
import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';

@Component({
  selector: 'generic-date-range-picker',
  templateUrl: './generic-date-range-picker.component.html',
  styleUrls: ['./generic-date-range-picker.component.css']
})
export class GenericDateRangePickerComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();

  /**
   * The model of input property
   * Mandatory
   */
  @Input() modelStart: Date | undefined;
  @Output() modelStartChange: EventEmitter<Date> = new EventEmitter<Date>();
  /**
 * The model of input property
 * Mandatory
 */
  @Input() modelEnd: Date | undefined;
  @Output() modelEndChange: EventEmitter<Date> = new EventEmitter<Date>();
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
   * Default : datepicker
   */
  @Input() propertyNameStart: string = "datepickerStart";
  /**
 * The name of the input
 * Default : datepicker
 */
  @Input() propertyNameEnd: string = "datepickerEnd";
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
   * Define a min date selection for user
   */
  @Input() minDate: Date | undefined;
  /**
   * Define a min date selection for user
   */
  @Input() maxDate: Date | undefined;
  /**
   * Fired when a date is selected in the calendar
   */
  @Output() onDateChange: EventEmitter<Date> = new EventEmitter();

  constructor(
    private formBuilder: UntypedFormBuilder) { }

  ngOnChanges(changes: SimpleChanges) {
    if (this.form && (this.isMandatory || this.customValidators)) {
      this.form.get(this.propertyNameStart)?.updateValueAndValidity();
      this.form.get(this.propertyNameEnd)?.updateValueAndValidity();
    }
    if (changes.modelStart && this.form != undefined) {
      this.form.get(this.propertyNameStart)?.setValue(this.modelStart);
    }
    if (changes.modelEnd && this.form != undefined) {
      this.form.get(this.propertyNameEnd)?.setValue(this.modelEnd);
    }
    if (changes.isDisabled) {
      if (this.isDisabled) {
        this.form?.get(this.propertyNameStart)?.disable();
        this.form?.get(this.propertyNameEnd)?.disable();
      } else {
        this.form?.get(this.propertyNameStart)?.enable();
        this.form?.get(this.propertyNameEnd)?.enable();
      }
    }
  }

  ngOnDestroy() {
    if (this.form != undefined) {
      this.form.removeControl(this.propertyNameEnd);
      this.form.removeControl(this.propertyNameStart);
    }
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

      this.form.addControl(this.propertyNameStart, this.formBuilder.control({ value: '', disabled: this.isDisabled }, validators));
      this.form.addControl(this.propertyNameEnd, this.formBuilder.control({ value: '', disabled: this.isDisabled }, validators));
      this.form.controls[this.propertyNameEnd].valueChanges.subscribe(
        (newValue) => {
          this.modelEnd = newValue;
          this.modelEndChange.emit(this.modelEnd);
        }
      )
      this.form.controls[this.propertyNameStart].valueChanges.subscribe(
        (newValue) => {
          this.modelStart = newValue;
          this.modelStartChange.emit(this.modelStart);
        }
      )
      this.form.get(this.propertyNameEnd)?.setValue(this.modelEnd);
      this.form.get(this.propertyNameStart)?.setValue(this.modelStart);
      this.form.markAllAsTouched();
    }
  }

  // Check if the propertiy given in parameter is filled when conditionnalRequired is set
  checkFieldFilledIfIsConditionalRequired(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValueStart = root.get(this.propertyNameStart)?.value;
      const fieldValueEnd = root.get(this.propertyNameEnd)?.value;
      if (this.conditionnalRequired && (fieldValueStart == undefined || fieldValueStart == null || fieldValueStart.length == 0))
        return {
          notFilled: true
        };
      if (this.conditionnalRequired && (fieldValueEnd == undefined || fieldValueEnd == null || fieldValueEnd.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  dateChange(value: Date) {
    this.onDateChange.emit(value);
  }

}
