import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { formatDateIso } from '../../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { GenericFormComponent } from '../generic-form.components';

@Component({
  selector: 'generic-date-picker',
  templateUrl: './generic-date-picker.component.html',
  styleUrls: ['./generic-date-picker.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class GenericDatePickerComponent extends GenericFormComponent implements OnInit {

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

  getDateFormated(date: Date | undefined): string {
    if (date)
      return formatDateIso(date);
    return "";
  }
}
