import { Directive, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, switchMap, tap } from 'rxjs/operators';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericFormComponent } from '../generic-form.components';

@Directive()
export abstract class GenericAutocompleteComponent<T, U> extends GenericFormComponent implements OnInit {

  /**
   * Fired when an option is selected in the autocomplete list.
   * Give the selected object in parameter
   */
  @Output() onOptionSelected: EventEmitter<T> = new EventEmitter();
  /**
  * Override matFormField class
  * Default = full-width
  */
  @Input() matFormFieldClass: string = "full-width";

  @Input() byPassAutocompletValidator: boolean = false;

  expectedMinLengthInput: number = 2;

  filteredTypes: T[] | undefined;

  constructor(private formBuilder3: UntypedFormBuilder,
    private userNoteService3: UserNoteService) {
    super(formBuilder3, userNoteService3);
  }

  callOnNgInit(): void {
    if (this.form)
      this.form.get(this.propertyName)?.valueChanges.pipe(
        filter(res => {
          return res != undefined && res !== null && res.length >= this.expectedMinLengthInput
        }),
        distinctUntilChanged(),
        debounceTime(300),
        tap((value) => {
          this.filteredTypes = [];
          this.modelChange.emit(this.model);
        }),
        switchMap(value => this.searchEntities(value)
        )
      ).subscribe(response => {
        this.filteredTypes = this.mapResponse(response);
      });
  }

  abstract searchEntities(value: string): Observable<U[]>;

  mapResponse(response: U[]): T[] {
    return (response as unknown) as Array<T>;
  }

  optionSelected(type: T): void {
    this.model = type;
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(type);
    this.form?.updateValueAndValidity();
  }

  clearField(): void {
    this.model = undefined;
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(undefined);
    if (this.form) {
      this.form.get(this.propertyName)?.setValue(null);
      this.form.markAllAsTouched();
      this.form.updateValueAndValidity();
    }
  }
}
