import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericFormComponent } from '../generic-form.components';

@Component({
  selector: 'generic-toggle',
  templateUrl: './generic-toggle.component.html',
  styleUrls: ['./generic-toggle.component.css']
})
export class GenericToggleComponent extends GenericFormComponent implements OnInit {
  /**
   * Indicate if the field is required or not in the formgroup provided
   * Default : false
   */
  @Input() isMandatory: boolean = true;
  /**
 * Hint to display
 */
  @Input() hint: string | undefined;
  /**
   * Fired when input is modified by user
   */
  @Output() onToggleChange: EventEmitter<void> = new EventEmitter();


  constructor(
    private formBuilder3: UntypedFormBuilder, userNoteService3: UserNoteService) {
    super(formBuilder3, userNoteService3);
  }

  onChange() {
    if (this.onToggleChange)
      this.onToggleChange.emit();
  }

  callOnNgInit(): void {
    if (this.form && (this.model == null || this.model == undefined)) {
      this.model = false;
      this.modelChange.emit(this.model);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
    if (this.form && (this.model == null || this.model == undefined)) {
      this.model = false;
      this.modelChange.emit(this.model);
    }
  }
}
