import { Directive, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ValidatorFn } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';

@Directive()
export abstract class GenericRadioGroupComponent<T> implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();

  /**
   * The model of Civility property
   * Mandatory
   */
  @Output() modelChange: EventEmitter<T> = new EventEmitter<T>();
  @Input() model: T | undefined;
  /**
   * The formgroup to bind component
   * Mandatory
   */
  @Input() form: FormGroup | undefined;
  /**
   * The name of the input
   * Default : civility
   */
  @Input() propertyName: string = "civility";

  abstract types: T[];

  constructor(private formBuilder: FormBuilder) { }

  ngOnChanges(changes: SimpleChanges) {
    if (this.form && (!this.model) && this.types)
      this.model = this.types[0];
  }

  ngOnDestroy() {
    if (this.form != undefined)
      this.form.removeControl(this.propertyName);
  }

  ngOnInit() {
    this.initTypes();

    if (this.form != undefined) {
      let validators: ValidatorFn[] = [] as Array<ValidatorFn>;

      this.form.addControl(this.propertyName, this.formBuilder.control('', validators));
      this.form.controls[this.propertyName].valueChanges.subscribe(
        (newValue) => {
          this.model = newValue;
          this.modelChange.emit(this.model);
        }
      )
      this.form.markAllAsTouched();
    }
  }

  abstract initTypes(): void;
}
