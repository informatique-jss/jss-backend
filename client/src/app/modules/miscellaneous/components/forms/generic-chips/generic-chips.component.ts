import { Directive, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material/chips';
import { SEPARATOR_KEY_CODES } from 'src/app/libs/Constants';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericFormComponent } from '../generic-form.components';

@Directive()
export abstract class GenericChipsComponent<T> extends GenericFormComponent implements OnInit {

  SEPARATOR_KEY_CODES = SEPARATOR_KEY_CODES;



  /**
   * The model of T property
   * Mandatory
   */
  @Input() model: T[] | undefined;


  constructor(private formBuilder3: UntypedFormBuilder,
    private userNoteService3: UserNoteService) {
    super(formBuilder3, userNoteService3);
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


}
