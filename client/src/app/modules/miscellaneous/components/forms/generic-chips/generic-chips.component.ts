import { Directive, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { UserNoteService } from 'src/app/services/user.notes.service';

@Directive()
export abstract class GenericChipsComponent<T> implements OnInit {

  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();

  /**
   * The model of T property
   * Mandatory
   */
  @Input() model: T[] | undefined;
  @Output() modelChange: EventEmitter<T[]> = new EventEmitter<T[]>();
  /**
   * The formgroup to bind component
   * Mandatory
   */
  @Input() form: UntypedFormGroup | undefined;
  /**
   * The name of the input
   * chips by default
   */
  @Input() propertyName: string = "chips";
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
  @Input() label: string = "";

  constructor(private formBuilder: UntypedFormBuilder,
    private userNoteService: UserNoteService) { }

  ngOnChanges(changes: SimpleChanges) {
    if (this.form && (this.isMandatory || this.customValidators))
      this.form.get(this.propertyName)?.updateValueAndValidity();
  }

  ngOnDestroy() {
    if (this.form != undefined)
      this.form.removeControl(this.propertyName);
  }

  ngOnInit() {
    if (this.form != undefined) {
      let validators: ValidatorFn[] = [] as Array<ValidatorFn>;

      if (this.isMandatory) {
        if (!this.conditionnalRequired) {
          this.conditionnalRequired = true;
        }
        validators.push(this.checkFieldFilledIfIsConditionalRequired());
      }

      if (this.customValidators != undefined && this.customValidators != null && this.customValidators.length > 0)
        validators.push(...this.customValidators);

      this.form.addControl(this.propertyName, this.formBuilder.control('', validators));
      this.form.markAllAsTouched();
    }
  }

  // Check if the propertiy given in parameter is filled when conditionnalRequired is set
  checkFieldFilledIfIsConditionalRequired(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValue = root.get(this.propertyName)?.value;
      if (this.conditionnalRequired && (!this.model || this.model?.length == 0) && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  addElement(event: MatChipInputEvent): void {
    if (this.form != undefined) {
      const value = (event.value || '').trim();
      let element: T = {} as T;
      if (value && this.validateInput(value)) {
        element = this.setValueToObject(value, element);
        if (this.model == undefined || this.model == null)
          this.model = [] as T[];
        this.model.push(element);
      }
      event.chipInput!.clear();
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
    }
  }

  removeElement(inputElement: T): void {
    if (this.model != undefined && this.model != null && !this.isDisabled)
      for (let i = 0; i < this.model.length; i++) {
        const element = this.model[i];
        if (this.getValueFromObject(element) == this.getValueFromObject(inputElement)) {
          this.model.splice(i, 1);
          this.modelChange.emit(this.model);
          return;
        }
      }
  }

  abstract validateInput(value: string): boolean | null;

  abstract setValueToObject(value: string, object: T): T;

  abstract getValueFromObject(object: T): string;

  displayLabel(object: any): string {
    return object ? object.label : '';
  }

  addToNotes(event: any) {
    let isHeader = false;
    if (event && event.ctrlKey)
      isHeader = true;
    this.userNoteService.addToNotes(this.label, this.displayLabel(this.model), undefined, isHeader);
  }

}
