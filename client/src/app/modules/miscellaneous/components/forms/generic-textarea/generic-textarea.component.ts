import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericFormComponent } from '../generic-form.components';


@Component({
  selector: 'generic-textarea',
  templateUrl: './generic-textarea.component.html',
  styleUrls: ['./generic-textarea.component.css']
})
export class GenericTextareaComponent extends GenericFormComponent implements OnInit {
  /**
 * Max length of input
 * No check if not devined
 */
  @Input() maxLength: number | undefined;
  /**
 * Min length of input
 * No check if not devined
 */
  @Input() minLength: number | undefined;
  /**
  * Event triggered on keyup, keydown and paste
  */
  @Output() filterInput: EventEmitter<any> = new EventEmitter();
  /**
 * Number of lines to display
 * Default : 3
 */
  @Input() numberOfLines: number = 3;
  /**
* Hint to display
*/
  @Input() hint: string = "";

  constructor(
    private formBuilder3: UntypedFormBuilder,
    private userNoteService3: UserNoteService) { super(formBuilder3, userNoteService3) }

  callOnNgInit(): void {
  }


  clearField(): void {
    this.model = undefined;
    this.modelChange.emit(this.model);
    if (this.form)
      this.form.get(this.propertyName)?.setValue(null);
  }

  onInputChange(): void {
    this.modelChange.emit(this.model);
    this.filterInput.emit();
  }

}
