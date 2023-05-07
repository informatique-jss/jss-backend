import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericFormComponent } from '../generic-form.components';

@Component({
  selector: 'generic-datepicker',
  templateUrl: './generic-datepicker.component.html',
  styleUrls: ['./generic-datepicker.component.css']
})
export class GenericDatepickerComponent extends GenericFormComponent implements OnInit {


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

  /**
* Hint to display
*/
  @Input() hint: string = "";

  constructor(
    private formBuilder3: UntypedFormBuilder, userNoteService3: UserNoteService) {
    super(formBuilder3, userNoteService3);
  }

  ngOnInit() {
    if (this.form != undefined) {
      this.form.addControl(this.propertyName, this.formBuilder3.control({ value: '', disabled: this.isDisabled, validators: this.customValidators }, { updateOn: 'blur' }));
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

  callOnNgInit(): void {
  }

  dateChange(value: Date) {
    this.onDateChange.emit(value);
  }
}
