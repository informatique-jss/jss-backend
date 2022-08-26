import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { UserNoteService } from 'src/app/services/user.notes.service';

@Component({
  selector: 'generic-input',
  templateUrl: './generic-input.component.html',
  styleUrls: ['./generic-input.component.css']
})
export class GenericInputComponent implements OnInit {

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
 * Max length of input
 * No check if not devined
 */
  @Input() maxLength: number | undefined;
  /**
 * Min length of input
 * No check if not devined
 */
  @Input() minLength: number | undefined;
  /**
 * Type of input
 * text if not defined
 */
  @Input() type: string = "text";
  /**
 * Step used
 * Use decimal for float field, 1 for integer field
 * 1 if ot defined
 */
  @Input() step: string = "1";
  /**
 * Hint to display
 */
  @Input() hint: string = "";
  /**
   * Fired when input is modified by user
   */
  @Output() onInputChange: EventEmitter<void> = new EventEmitter();

  constructor(
    private formBuilder: UntypedFormBuilder,
    private userNoteService: UserNoteService,
  ) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.model && this.form != undefined)
      this.form.get(this.propertyName)?.setValue(this.model);
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

      if (this.maxLength)
        validators.push(Validators.maxLength(this.maxLength));

      if (this.minLength)
        validators.push(Validators.minLength(this.minLength));

      if (this.customValidators != undefined && this.customValidators != null && this.customValidators.length > 0)
        validators.push(...this.customValidators);

      this.form.addControl(this.propertyName, this.formBuilder.control({ value: '', disabled: this.isDisabled }, validators));
      this.form.get(this.propertyName)!.valueChanges.subscribe(
        (newValue) => {
          this.model = newValue;
          this.modelChange.emit(this.model);
        }
      )
      this.form.get(this.propertyName)?.setValue(this.model);
      this.form.markAllAsTouched();
    }
  }

  inputChange() {
    this.onInputChange.emit();
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

  addToNotes(event: any) {
    let isHeader = false;
    if (event && event.ctrlKey)
      isHeader = true;
    this.userNoteService.addToNotes(this.label, this.model, undefined, isHeader);
  }
}
