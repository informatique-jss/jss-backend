import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { GenericFormComponent } from '../generic-form.components';
import { AppService } from 'src/app/services/app.service';

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
    private formBuilder3: UntypedFormBuilder, private appService2: AppService) {
    super(formBuilder3, appService2);
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
