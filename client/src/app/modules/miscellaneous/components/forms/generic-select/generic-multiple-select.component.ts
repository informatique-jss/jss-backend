import { Directive, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericFormComponent } from '../generic-form.components';

@Directive()
export abstract class GenericMultipleSelectComponent<T> extends GenericFormComponent implements OnInit {

  /**
   * The model of T property
   * Mandatory
   */
  @Input() model: T[] = [] as Array<T>;
  @Output() modelChange: EventEmitter<T[]> = new EventEmitter<T[]>();

  /**
* Triggered when value is changed by user
*/
  @Output() selectionChange: EventEmitter<T[]> = new EventEmitter();

  abstract types: T[];

  constructor(
    private formBuilder3: UntypedFormBuilder,
    private userNoteService3: UserNoteService) {
    super(formBuilder3, userNoteService3);
  }

  callOnNgInit(): void {
    this.initTypes();
  }

  abstract initTypes(): void;

  compareWithId = compareWithId;

  clearField(): void {
    this.model = [] as Array<T>;
    this.modelChange.emit(this.model);
    this.selectionChange.emit(undefined);
  }
}
