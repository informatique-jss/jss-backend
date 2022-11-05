import { Directive, EventEmitter, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericFormComponent } from '../generic-form.components';

@Directive()
export abstract class GenericLocalAutocompleteComponent<T> extends GenericFormComponent implements OnInit {

  @Output() onOptionSelected: EventEmitter<T> = new EventEmitter();

  abstract types: T[];
  filteredTypes: Observable<T[]> | undefined;

  constructor(private formBuilder3: UntypedFormBuilder,
    private userNoteService3: UserNoteService) {
    super(formBuilder3, userNoteService3);
  }

  callOnNgInit(): void {
    this.initTypes();
    if (this.form)
      this.filteredTypes = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => this.filterEntities(this.types, value)),
      );
  }

  abstract filterEntities(types: T[], value: string): T[];

  optionSelected(type: T): void {
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(type);
  }

  clearField(): void {
    this.model = undefined;
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(undefined);
  }

  abstract initTypes(): void;

}
