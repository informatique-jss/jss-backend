import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { GenericFormComponent } from '../generic-form.components';

@Component({
  selector: 'generic-input',
  templateUrl: './generic-input.component.html',
  styleUrls: ['./generic-input.component.css']
})
export class GenericInputComponent extends GenericFormComponent implements OnInit {
  /**
 * Max length of input
 * No check if not devined
 */
  @Input() maxLength: number | undefined;
  /**
 * Min length of input
 * No check if not devined
 */
  @Input() minLength: number | undefined;
  /**
 * Type of input
 * text if not defined
 */
  @Input() type: string = "text";
  /**
 * Step used
 * Use decimal for float field, 1 for integer field
 * 1 if ot defined
 */
  @Input() step: string = "1";
  /**
 * Error message to display
 */
  @Input() errorMessage: string = "";

  constructor(
    private formBuilder2: UntypedFormBuilder
  ) {
    super(formBuilder2);
  }

  callOnNgInit(): void {
  }

  parse(event: any) {
    if (this.form && this.type == 'number') {
      this.model = (this.model + "").replace(',', '.');
      if (isNaN(this.model)) {
        this.model = undefined;
      } else {
        this.model = parseFloat(this.model);
      }

      this.modelChange.emit(this.model);
      this.onFormChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(this.model);
    }
  }

  getPreviewActionLinkFunction(entity: any): string[] | undefined {
    return undefined;
  }
}