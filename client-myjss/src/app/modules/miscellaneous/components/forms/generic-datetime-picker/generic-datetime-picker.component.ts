import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { GenericFormComponent } from '../generic-form.components';

@Component({
  selector: 'generic-datetime-picker',
  templateUrl: './generic-datetime-picker.component.html',
  styleUrls: ['./generic-datetime-picker.component.css'],
  standalone: false
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

  constructor(private formBuilder3: UntypedFormBuilder) {
    super(formBuilder3);
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

  getPreviewActionLinkFunction(entity: any): string[] | undefined {
    return undefined;
  }
}
