import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { GenericFormComponent } from '../generic-form.components';

@Component({
  selector: 'generic-toggle',
  templateUrl: './generic-toggle.component.html',
  styleUrls: ['./generic-toggle.component.css']
})
export class GenericToggleComponent extends GenericFormComponent implements OnInit {
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

  callOnNgInit(): void {
    if (this.form && (this.model == null || this.model == undefined)) {
      this.model = false;
      this.modelChange.emit(this.model);
    }
    if (this.form)
      this.form.get(this.propertyName)!.valueChanges.subscribe(
        (newValue) => {
          this.onToggleChange.emit(newValue);
        }
      )
  }

  ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
    if (this.form && (this.model == null || this.model == undefined)) {
      this.model = false;
      this.modelChange.emit(this.model);
    }
  }
  getPreviewActionLinkFunction(entity: any): string[] | undefined {
    return undefined;
  }
}
