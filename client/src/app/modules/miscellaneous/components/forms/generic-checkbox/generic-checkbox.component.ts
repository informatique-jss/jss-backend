import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ValidatorFn } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';

@Component({
  selector: 'generic-checkbox',
  templateUrl: './generic-checkbox.component.html',
  styleUrls: ['./generic-checkbox.component.css']
})
export class GenericCheckboxComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();

  /**
   * The model of input property
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
  @Input() form: FormGroup | undefined;
  /**
   * The name of the input
   * Default : checkbox
   */
  @Input() propertyName: string = "checkbox";
  /**
* Indicate if the field is disabled or not in the formgroup provided
* Default : false
*/
  @Input() isDisabled: boolean = false;

  constructor(
    private formBuilder: FormBuilder) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.model && this.form != undefined)
      this.form.get(this.propertyName)?.setValue(this.model);
    if (changes.isDisabled) {
      if (this.isDisabled) {
        this.form?.get(this.propertyName)?.disable();
      } else {
        this.form?.get(this.propertyName)?.enable();
      }
    }
  }

  ngOnDestroy() {
    if (this.form != undefined)
      this.form.removeControl(this.propertyName);
  }

  ngOnInit() {
    if (this.form != undefined) {
      let validators: ValidatorFn[] = [] as Array<ValidatorFn>;

      this.form.addControl(this.propertyName, this.formBuilder.control({ value: '', disabled: this.isDisabled }, validators));
      this.form.get(this.propertyName)!.valueChanges.subscribe(
        (newValue) => {
          // TODO  : le changement de modèle ne remonte pas dans le doc
          this.model = newValue;
          this.modelChange.emit(this.model);
        }
      )
      this.form.get(this.propertyName)?.setValue(this.model);
      this.form.markAllAsTouched();
    }
  }
}
