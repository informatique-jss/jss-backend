import { Directive, Input, OnInit, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericFormComponent } from '../generic-form.components';

@Directive()
export abstract class GenericRadioGroupComponent<T> extends GenericFormComponent implements OnInit {

  abstract types: T[];

  /**
 * Indicate if the field is required or not in the formgroup provided
 * Default : false
 */
  @Input() isMandatory: boolean = true;

  constructor(private formBuilder3: UntypedFormBuilder, private userNoteService3: UserNoteService) {
    super(formBuilder3, userNoteService3)
  }

  callOnNgInit(): void {
    this.initTypes();
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    if (this.types && !this.model && this.types) {
      this.model = this.types[0];
      this.modelChange.emit();
    }
  }

  abstract initTypes(): void;
}
