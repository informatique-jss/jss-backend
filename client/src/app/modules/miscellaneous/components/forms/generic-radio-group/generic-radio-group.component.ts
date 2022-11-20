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

  /**
   * Indicate that first option must be choosed by default
   */
  @Input() setFirstOptionDefaultChoice: boolean = false;


  constructor(private formBuilder3: UntypedFormBuilder, private userNoteService3: UserNoteService) {
    super(formBuilder3, userNoteService3)
  }

  ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
    if (this.setFirstOptionDefaultChoice && !this.model) {
      this.model = this.types[0];
      this.modelChange.emit(this.model);
    }
  }

  callOnNgInit(): void {
    this.initTypes();
  }

  abstract initTypes(): void;
}
