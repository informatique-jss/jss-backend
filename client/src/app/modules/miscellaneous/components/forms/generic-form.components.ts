import { Directive, EventEmitter, Input, OnInit, Output, SimpleChanges } from "@angular/core";
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { UserNoteService } from "src/app/services/user.notes.service";

@Directive()
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
  constructor(
    private formBuilder: UntypedFormBuilder,
    private userNoteService: UserNoteService,
  ) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.model && this.form != undefined) {
      this.form.get(this.propertyName)?.setValue(this.model);
      this.modelChange.emit(this.model);
    }
    if (changes.isDisabled) {
      if (this.isDisabled) {
        this.form?.get(this.propertyName)?.disable();
      } else {
        this.form?.get(this.propertyName)?.enable();
      }
    }
    if (this.form) {
      this.form.get(this.propertyName)?.updateValueAndValidity();
      this.form.get(this.propertyName)?.markAllAsTouched();
    }
  }

  ngOnDestroy() {
    if (this.form != undefined)
      this.form.removeControl(this.propertyName);
  }

  ngOnInit() {
    if (this.form != undefined) {
      this.form.addControl(this.propertyName, this.formBuilder.control({ value: '', disabled: this.isDisabled, validators: this.customValidators }));
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
        if (this.conditionnalRequired != undefined) {
          if (this.conditionnalRequired && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0)) {
            this.form!.get(this.propertyName)!.setErrors({ notFilled: this.propertyName });
            return {
              notFilled: this.propertyName
            };
          }
        } else if (this.isMandatory && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0)) {
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

  addToNotes(event: any) {
    let isHeader = false;
    if (event && event.ctrlKey)
      isHeader = true;
    this.userNoteService.addToNotes(this.label, this.displayLabel(this.model), undefined, isHeader);
  }

  displayLabel(object: any): string {
    if (object && object.label)
      return object.label;
    if (typeof object === "string")
      return object;
    return "";
  }
}
