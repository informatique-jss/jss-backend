import { Directive, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { Observable } from 'rxjs';
import { debounceTime, filter, switchMap, tap } from 'rxjs/operators';
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

  doNotOpenTwice: boolean = false;

  @ViewChild(MatAutocompleteTrigger) trigger: MatAutocompleteTrigger | undefined;

  @Input() fieldToCheckAgainstForValidation: string = "id";

  constructor(private formBuilder3: UntypedFormBuilder,
    private userNoteService3: UserNoteService) {
    super(formBuilder3, userNoteService3);
  }

  callOnNgInit(): void {
  }

  ngOnInit() {
    if (this.form != undefined) {
      this.form.addControl(this.propertyName, this.formBuilder3.control({ value: '', disabled: this.isDisabled }));
      this.form.addValidators(this.checkField());
      this.form.addValidators(this.checkAutocompleteField());
      this.form.get(this.propertyName)?.valueChanges.pipe(
        filter(res => {
          return res != undefined && res !== null && res.length > this.expectedMinLengthInput
        }),
        debounceTime(300),
        tap((value) => {
          this.filteredTypes = [];
          this.modelChange.emit(this.model);
        }),
        switchMap(value => this.searchEntities(value)
        )
      ).subscribe(response => {
        this.filteredTypes = this.mapResponse(response);
        if (!this.isDisabled && !this.doNotOpenTwice)
          this.trigger?.openPanel();
      });
      this.form.get(this.propertyName)?.setValue(this.model);
      this.callOnNgInit();
    }
  }

  abstract searchEntities(value: string): Observable<U[]>;

  mapResponse(response: U[]): T[] {
    return (response as unknown) as Array<T>;
  }

  optionSelected(type: T): void {
    this.model = type;
    this.doNotOpenTwice = true;
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(type);
    this.form?.updateValueAndValidity();
  }

  checkAutocompleteField(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValue = root.get(this.propertyName)?.value;
      if (this.form && this.form!.get(this.propertyName)) {
        if (this.conditionnalRequired != undefined) {
          if (this.conditionnalRequired && (!fieldValue || fieldValue[this.fieldToCheckAgainstForValidation] == null)) {
            this.form!.get(this.propertyName)!.setErrors({ notFilled: this.propertyName });
            return {
              notFilled: this.propertyName
            };
          }
        } else if (this.isMandatory && (!fieldValue || fieldValue[this.fieldToCheckAgainstForValidation] == null)) {
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

  clearField(): void {
    this.model = undefined;
    this.doNotOpenTwice = false;
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(undefined);
    if (this.form) {
      this.form.get(this.propertyName)?.setValue(null);
      this.form.markAllAsTouched();
      this.form.updateValueAndValidity();
    }
  }
}
