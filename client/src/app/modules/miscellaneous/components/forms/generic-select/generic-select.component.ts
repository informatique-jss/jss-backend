import { Directive, EventEmitter, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericFormComponent } from '../generic-form.components';

@Directive()
export abstract class GenericSelectComponent<T> extends GenericFormComponent implements OnInit {

  /**
 * Triggered when value is changed by user
 */
  @Output() selectionChange: EventEmitter<T> = new EventEmitter();



  abstract types: T[];

  constructor(
    private formBuilder3: UntypedFormBuilder,
    private userNoteService3: UserNoteService) {
    super(formBuilder3, userNoteService3)
  }

  callOnNgInit(): void {
    this.initTypes();
    if (this.form)
      this.form.get(this.propertyName)?.valueChanges.subscribe(
        (newValue) => {
          this.selectionChange.emit(this.model);
        }
      );
  }

  abstract initTypes(): void;

  compareWithId = compareWithId;

  clearField(): void {
    this.model = undefined;
    this.modelChange.emit(this.model);
    this.selectionChange.emit(undefined);
  }


}
