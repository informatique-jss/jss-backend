import { Directive, Input, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { AppService } from 'src/app/services/app.service';
import { GenericFormComponent } from '../generic-form.components';

@Directive()
export abstract class GenericSingleChipsComponent<T> extends GenericFormComponent implements OnInit {

  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;

  /**
   * The model of T property
   * Mandatory
   */
  @Input() model: T | undefined;


  constructor(private formBuilder3: UntypedFormBuilder, private appService2: AppService) {
    super(formBuilder3, appService2);
  }

  ngOnInit() {
    if (this.form != undefined) {
      this.form.addControl(this.propertyName, this.formBuilder3.control({ value: '', disabled: this.isDisabled }));
      this.form.addValidators(this.checkField());
      this.callOnNgInit();
      this.form.get(this.propertyName)?.setValue(this.model);
    }
  }

  addElement(event: MatChipInputEvent): void {
    if (this.form != undefined) {
      const value = (event.value || '').trim();
      let element: T = {} as T;
      if (value && this.validateInput(value)) {
        element = this.setValueToObject(value, element);
        this.model = element;
      }
      event.chipInput!.clear();
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.form.markAllAsTouched();
      this.form.updateValueAndValidity();
    }
  }

  removeElement(inputElement: T): void {
    this.model = undefined;
    this.modelChange.emit(this.model);
    this.form!.markAllAsTouched();
    this.form!.updateValueAndValidity();
    return;
  }

  abstract validateInput(value: string): boolean | null;

  abstract setValueToObject(value: string, object: T): T;

  abstract getValueFromObject(object: T): string;

  displayLabel(object: any): string {
    return object ? object.label : '';
  }

  // Check if the propertiy given in parameter is filled when conditionnalRequired is set
  checkField(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValue = this.model;
      if (this.form && this.form!.get(this.propertyName)) {
        if (this.conditionnalRequired != undefined) {
          if (this.conditionnalRequired && (fieldValue == undefined || fieldValue == null)) {
            this.form!.get(this.propertyName)!.setErrors({ notFilled: this.propertyName });
            return {
              notFilled: this.propertyName
            };
          }
        } else if (this.isMandatory && (fieldValue == undefined || fieldValue == null)) {
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

  getPreviewActionLinkFunction(entity: T): string[] | undefined {
    return undefined;
  }
}
