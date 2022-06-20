import { ChangeDetectorRef, Directive, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, filter, switchMap, tap } from 'rxjs/operators';
import { CustomErrorStateMatcher } from 'src/app/app.component';

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
   * The formgroup to bind component
   * Mandatory
   */
  @Input() form: FormGroup | undefined;
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

  expectedMinLengthInput: number = 2;

  filteredTypes: T[] | undefined;

  constructor(private formBuilder: FormBuilder, private cdr: ChangeDetectorRef) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.model && this.form != undefined) {
      this.form.get(this.propertyName)?.setValue(this.model);
      this.cdr.detectChanges();
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
      let validators: ValidatorFn[] = [] as Array<ValidatorFn>;
      validators.push(this.checkAutocompleteField());
      if (this.isMandatory) {
        if (this.conditionnalRequired != undefined) {
          validators.push(this.checkFieldFilledIfIsConditionalRequired());
        } else {
          validators.push(Validators.required);
        }
      }

      // TODO  : validators not working !
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
          this.cdr.detectChanges();
          this.modelChange.emit(this.model);
        }),
        switchMap(value => this.searchEntities(value)
        )
      ).subscribe(response => {
        this.filteredTypes = this.mapResponse(response);
        this.cdr.detectChanges();
      });
      this.form.get(this.propertyName)?.setValue(this.model);
      this.form.markAllAsTouched();
    }
  }

  installValidators() {

  }
  abstract searchEntities(value: string): Observable<U[]>;

  mapResponse(response: U[]): T[] {
    return (response as unknown) as Array<T>;
  }

  checkAutocompleteField(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;
      const fieldValue = root.get(this.propertyName)?.value;
      if (fieldValue != undefined && fieldValue != null && (fieldValue.id == undefined || fieldValue.id == null))
        return {
          notFilled: true
        };
      return null;
    };
  }

  // Check if the propertiy given in parameter is filled when conditionnalRequired is set
  checkFieldFilledIfIsConditionalRequired(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as FormGroup;
      const fieldValue = root.get(this.propertyName)?.value;
      if (this.conditionnalRequired && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0))
        return {
          notFilled: true
        };
      return null;
    };
  }

  displayLabel(object: any): string {
    return object ? object.label : '';
  }

  optionSelected(type: T): void {
    this.modelChange.emit(this.model);
    this.cdr.detectChanges();
    this.onOptionSelected.emit(type);
  }
}
