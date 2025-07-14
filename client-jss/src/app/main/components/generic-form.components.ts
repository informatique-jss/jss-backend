import { Directive, EventEmitter, Input, OnInit, Output, SimpleChanges } from "@angular/core";
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

@Directive({
  standalone: true
})
export abstract class GenericFormComponent implements OnInit {
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
 * Indicate if the label field should be displayed
 * Default : true
 */
  @Input() isDisplayLabel: boolean = true;
  /**
   * Fired when input is modified by user
   */
  @Output() onFormChange: EventEmitter<void> = new EventEmitter();
  /**
 * Fired when input blur
 */
  @Output() onFormBlur: EventEmitter<void> = new EventEmitter();
  /**
  * Additionnal validators to check
  */
  @Input() customValidators: ValidatorFn[] | undefined;

  isDisplayPreviewShortcut: boolean = false;
  previewActionLink: string[] | undefined;

  uniqueId: number = 0;

  constructor(
    private formBuilder: UntypedFormBuilder,
  ) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['model'] && this.form != undefined) {
      this.form.get(this.propertyName)?.setValue(this.model);
      this.modelChange.emit(this.model);
    }
    if (changes['isDisabled']) {
      if (this.isDisabled) {
        this.form?.get(this.propertyName)?.disable();
      } else {
        this.form?.get(this.propertyName)?.enable();
      }
    }
    if (this.form && (!changes['customValidators'] || Object.keys(changes).length > 1)) {
      this.form.get(this.propertyName)?.updateValueAndValidity();
      this.form.get(this.propertyName)?.markAllAsTouched();
    }
  }

  blurForm() {
    this.onFormBlur.emit();
  }

  ngOnDestroy() {
    if (this.form != undefined)
      this.form.removeControl(this.propertyName);
  }

  ngOnInit() {
    this.uniqueId = Math.round(Math.random() * 1000000000);
    if (this.form != undefined) {
      this.form.addControl(this.propertyName, this.formBuilder.control({ value: '', disabled: this.isDisabled }));
      this.form.addValidators(this.checkField());
      if (this.isDisabled) {
        this.form?.get(this.propertyName)?.disable();
      } else {
        this.form?.get(this.propertyName)?.enable();
      }
      this.form.get(this.propertyName)!.valueChanges.subscribe(
        (newValue) => {
          this.model = newValue;
          this.modelChange.emit(this.model);
          this.onFormChange.emit(this.model);
        }
      )
      this.callOnNgInit();
      this.form.get(this.propertyName)?.setValue(this.model);
    }
  }

  abstract callOnNgInit(): void;

  // Check if the propertiy given in parameter is filled when conditionnalRequired is set
  checkField(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValue = root.get(this.propertyName)?.value;
      if (this.form && this.form!.get(this.propertyName)) {
        if (this.isMandatory && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0)) {
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

  displayLabel(object: any): string {
    if (object && object.label)
      return object.label;
    if (typeof object === "string")
      return object;
    return "";
  }

}
