import { Directive, EventEmitter, OnInit, Output } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericFormComponent } from '../generic-form.components';

@Directive()
export abstract class GenericLocalAutocompleteComponent<T> extends GenericFormComponent implements OnInit {

  /**
   * Fired when an option is selected in the autocomplete list.
   * Give the selected City in parameter
   */
  @Output() onOptionSelected: EventEmitter<T> = new EventEmitter();

  abstract types: T[];
  filteredTypes: Observable<T[]> | undefined;

  constructor(private formBuilder3: UntypedFormBuilder,
    private userNoteService3: UserNoteService) {
    super(formBuilder3, userNoteService3);
  }

  callOnNgInit(): void {

  }

  ngOnInit() {
    this.initTypes();
    if (this.form != undefined) {
      this.form.addControl(this.propertyName, this.formBuilder3.control({ value: '', disabled: this.isDisabled }));
      this.form.addValidators(this.checkField());
      this.form.addValidators(this.checkAutocompleteField());
      this.filteredTypes = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => this.filterEntities(this.types, value).sort((a, b) => this.displayLabel(a).localeCompare(this.displayLabel(b)))),
      );
      this.form.get(this.propertyName)?.setValue(this.model);
    }
  }

  abstract filterEntities(types: T[], value: string): T[];

  checkAutocompleteField(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValue = root.get(this.propertyName)?.value;
      if (this.form && this.form!.get(this.propertyName)) {
        if (this.conditionnalRequired != undefined) {
          if (this.conditionnalRequired && (!fieldValue || fieldValue.id == null && fieldValue.code == null)) {
            this.form!.get(this.propertyName)!.setErrors({ notFilled: this.propertyName });
            return {
              notFilled: this.propertyName
            };
          }
        } else if (this.isMandatory && (!fieldValue || fieldValue.id == null && fieldValue.code == null)) {
          this.form!.get(this.propertyName)!.setErrors({ notFilled: this.propertyName });
          return {
            notFilled: this.propertyName
          };
        }
        this.form!.get(this.propertyName)!.setErrors(null);
      }
      return null;
    };
  }


  optionSelected(type: T): void {
    this.model = type;
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(type);
    this.form?.updateValueAndValidity();
    this.form?.markAllAsTouched();
  }

  clearField(): void {
    this.model = undefined;
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(undefined);
  }

  abstract initTypes(): void;


}
