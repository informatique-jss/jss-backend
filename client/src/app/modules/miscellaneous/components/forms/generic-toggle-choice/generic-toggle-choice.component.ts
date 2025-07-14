import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { GenericFormComponent } from '../generic-form.components';

@Component({
  selector: 'generic-toggle-choice',
  templateUrl: './generic-toggle-choice.component.html',
  styleUrls: ['./generic-toggle-choice.component.css']
})
export class GenericToggleChoiceComponent<T> extends GenericFormComponent implements OnInit {
  @Input() firstValue: T | undefined;
  @Input() secondValue: T | undefined;

  internalModel: boolean = false;

  /**
   * Indicate if the field is required or not in the formgroup provided
   * Default : false
   */
  @Input() isMandatory: boolean = true;
  /**
 * Hint to display
 */
  @Input() hint: string | undefined;
  /**
   * Fired when input is modified by user
   */
  @Output() onToggleChange: EventEmitter<Boolean> = new EventEmitter();


  constructor(
    private formBuilder3: UntypedFormBuilder, private appService2: AppService) {
    super(formBuilder3, appService2);
  }


  override ngOnInit(): void {
    if (this.form != undefined) {
      this.form.addControl(this.propertyName, this.formBuilder3.control({ value: '', disabled: this.isDisabled, validators: this.customValidators }));
      this.form.addValidators(this.checkField());
      if (this.isDisabled) {
        this.form?.get(this.propertyName)?.disable();
      } else {
        this.form?.get(this.propertyName)?.enable();
      }
      this.form.get(this.propertyName)!.valueChanges.subscribe(
        (newValue) => {
          this.internalModel = newValue;
          if (this.internalModel)
            this.model = this.secondValue;
          else
            this.model = this.firstValue;
          this.modelChange.emit(this.model);
          this.onFormChange.emit(this.model);
          this.checkPreviewIconAvailable();
        }
      )
      this.callOnNgInit();
      this.checkPreviewIconAvailable();
      this.form.get(this.propertyName)?.setValue(this.model);
    }
  }

  callOnNgInit(): void {
    if (this.form && (this.model == null || this.model == undefined)) {
      this.model = this.firstValue;
      this.modelChange.emit(this.model);
    }
    if (this.form)
      this.form.get(this.propertyName)!.valueChanges.subscribe(
        (newValue) => {
          this.internalModel = newValue;
          if (this.internalModel)
            this.model = this.secondValue;
          else
            this.model = this.firstValue;
          this.onToggleChange.emit(this.model);
        }
      )
  }

  override ngOnChanges(changes: SimpleChanges) {
    if (changes.model && this.form != undefined) {
      if (this.model && this.secondValue && (this.secondValue as any).id == (changes.model.currentValue as any).id)
        this.form.get(this.propertyName)?.setValue(true);
      else
        this.form.get(this.propertyName)?.setValue(false);
      this.modelChange.emit(this.model);
      this.checkPreviewIconAvailable();
    }
    if (changes.isDisabled) {
      if (this.isDisabled) {
        this.form?.get(this.propertyName)?.disable();
      } else {
        this.form?.get(this.propertyName)?.enable();
      }
    }
    if (this.form && (!changes.customValidators || Object.keys(changes).length > 1)) {
      this.form.get(this.propertyName)?.updateValueAndValidity();
      this.form.get(this.propertyName)?.markAllAsTouched();
    }
  }

  getPreviewActionLinkFunction(entity: any): string[] | undefined {
    return undefined;
  }
}
