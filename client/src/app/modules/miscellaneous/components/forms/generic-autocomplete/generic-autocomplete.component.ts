import { Directive, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, switchMap, tap } from 'rxjs/operators';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { UserNoteService } from 'src/app/services/user.notes.service';

@Directive()
export abstract class GenericAutocompleteComponent<T, U> implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();

  /**
   * The model of T property
   * Mandatory
   */
  @Input() model: any | undefined;
  @Output() modelChange: EventEmitter<any> = new EventEmitter<any>();
  /**
 * The label to display
 * Mandatory
 */
  @Input() label: string = "";
  /**
   * The formgroup to bind component
   * Mandatory
   */
  @Input() form: UntypedFormGroup | undefined;
  /**
   * The name of the input
   * autocomplete by default
   */
  @Input() propertyName: string = "autocomplete";
  /**
   * Indicate if the field is required or not in the formgroup provided
   * Default : false
   */
  @Input() isMandatory: boolean = false;
  /**
   * Add condition to check if the field is required.
   * If true (and isMandatory is also true), Validators.required is applied
   * If false, Validators.required is not applied regardless the value of isMandatory
   * Default : true
   */
  @Input() conditionnalRequired: boolean = true;
  /**
   * Additionnal validators to check
   */
  @Input() customValidators: ValidatorFn[] | undefined;
  /**
   * Fired when an option is selected in the autocomplete list.
   * Give the selected object in parameter
   */
  @Output() onOptionSelected: EventEmitter<T> = new EventEmitter();
  /**
 * Indicate if the field is disabled or not in the formgroup provided
 * Default : false
 */
  @Input() isDisabled: boolean = false;
  /**
  * Override matFormField class
  * Default = full-width
  */
  @Input() matFormFieldClass: string = "full-width";

  @Input() byPassAutocompletValidator: boolean = false;

  expectedMinLengthInput: number = 2;

  filteredTypes: T[] | undefined;

  constructor(private formBuilder: UntypedFormBuilder,
    private userNoteService: UserNoteService) { }

  ngOnChanges(changes: SimpleChanges) {
    if (this.form && !this.form.get(this.propertyName))
      this.ngOnInit();

    if (changes.model && this.form != undefined) {
      this.form.get(this.propertyName)?.setValue(this.model);
      this.modelChange.emit(this.model);
    }
    if (changes.isDisabled) {
      if (this.isDisabled) {
        this.form?.get(this.propertyName)?.disable();
      } else {
        this.form?.get(this.propertyName)?.enable();
      }
    }
    if (this.form && (this.isMandatory || this.customValidators))
      this.form.get(this.propertyName)?.updateValueAndValidity();
  }

  ngOnDestroy() {
    if (this.form != undefined)
      this.form.removeControl(this.propertyName);
  }

  ngOnInit() {
    if (this.form != undefined) {
      this.form.addControl(this.propertyName, this.formBuilder.control(''));
      let validators: ValidatorFn[] = [] as Array<ValidatorFn>;
      validators.push(this.checkAutocompleteField());
      if (this.isMandatory) {
        if (this.conditionnalRequired != undefined) {
          validators.push(this.checkFieldFilledIfIsConditionalRequired());
        } else {
          validators.push(Validators.required);
        }
      }

      if (this.customValidators != undefined && this.customValidators != null && this.customValidators.length > 0)
        validators.push(...this.customValidators);

      this.form.addControl(this.propertyName, this.formBuilder.control('', validators));
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
      this.form.get(this.propertyName)?.setValue(this.model);
      this.form.markAllAsTouched();
    }
  }

  onBlur() {
    if (!this.checkAutocompletFieldCheck() || !this.checkFieldFilledIfIsConditionalRequiredCheck()) {
      this.model = undefined;
      this.modelChange.emit(this.model);
      this.form!.get(this.propertyName)?.setValue(undefined);
    }
  }
  abstract searchEntities(value: string): Observable<U[]>;

  mapResponse(response: U[]): T[] {
    return (response as unknown) as Array<T>;
  }

  checkAutocompleteField(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (this.checkAutocompletFieldCheck())
        return {
          notFilled: true
        };
      return null;
    };
  }

  checkAutocompletFieldCheck() {
    if (this.byPassAutocompletValidator)
      return true;
    const fieldValue = this.form!.get(this.propertyName)?.value;
    if (fieldValue != undefined && fieldValue != null && (typeof fieldValue !== 'object'))
      return false;
    return true;
  }

  // Check if the propertiy given in parameter is filled when conditionnalRequired is set
  checkFieldFilledIfIsConditionalRequired(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValue = root.get(this.propertyName)?.value;
      if (this.checkFieldFilledIfIsConditionalRequiredCheck())
        return {
          notFilled: true
        };
      return null;
    };
  }

  checkFieldFilledIfIsConditionalRequiredCheck() {
    const fieldValue = this.form!.get(this.propertyName)?.value;
    if (this.conditionnalRequired && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
      return false;
    return true;
  }

  displayLabel(object: any): string {
    return object ? object.label : '';
  }

  optionSelected(type: T): void {
    this.model = type;
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(type);
  }

  clearField(): void {
    this.model = undefined;
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(undefined);
    if (this.form)
      this.form.get(this.propertyName)?.setValue(null);
  }

  addToNotes(event: any) {
    let isHeader = false;
    if (event && event.ctrlKey)
      isHeader = true;
    this.userNoteService.addToNotes(this.label, this.displayLabel(this.model), undefined, isHeader);
  }
}
