import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, ValidatorFn } from '@angular/forms';
import { CustomErrorStateMatcher } from 'src/app/app.component';

@Component({
  selector: 'generic-toggle',
  templateUrl: './generic-toggle.component.html',
  styleUrls: ['./generic-toggle.component.css']
})
export class GenericToggleComponent implements OnInit {
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
  @Input() form: UntypedFormGroup | undefined;
  /**
   * The name of the input
   * Default : toggle
   */
  @Input() propertyName: string = "toggle";
  /**
 * Hint to display
 */
  @Input() hint: string | undefined;


  constructor(
    private formBuilder: UntypedFormBuilder) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.model && this.form != undefined) {
      this.form.get(this.propertyName)?.setValue(this.model);
    }
  }

  ngOnDestroy() {
    if (this.form != undefined)
      this.form.removeControl(this.propertyName);
  }

  ngOnInit() {
    if (this.form != undefined) {
      let validators: ValidatorFn[] = [] as Array<ValidatorFn>;
      this.form.addControl(this.propertyName, this.formBuilder.control('', validators));
      this.form.controls[this.propertyName].valueChanges.subscribe(
        (newValue) => {
          this.model = newValue;
          if (this.model == undefined)
            this.model = false;
          this.modelChange.emit(this.model);
        }
      )
      this.form.get(this.propertyName)?.setValue(this.model);
      this.form.markAllAsTouched();
    }
  }
}
