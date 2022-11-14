import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericFormComponent } from '../generic-form.components';

@Component({
  selector: 'generic-datetime-picker',
  templateUrl: './generic-datetime-picker.component.html',
  styleUrls: ['./generic-datetime-picker.component.css']
})
export class GenericDatetimePickerComponent extends GenericFormComponent implements OnInit {

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

  callOnNgInit(): void {
  }

  clearField(): void {
    this.model = undefined;
    this.modelChange.emit(this.model);
    if (this.form)
      this.form.get(this.propertyName)?.setValue(null);
  }

  dateChange() {
    this.onDateChange.emit(this.model);
  }
}
