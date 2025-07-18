import { Directive, EventEmitter, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { compareWithId } from '../../../libs/CompareHelper';
import { GenericFormComponent } from '../generic-form.components';

@Directive({
  standalone: true,
})
export abstract class GenericSelectComponent<T> extends GenericFormComponent implements OnInit {

  /**
 * Triggered when value is changed by user
 */
  @Output() selectionChange: EventEmitter<T> = new EventEmitter();

  abstract types: T[];

  constructor(
    private formBuilder3: UntypedFormBuilder
  ) {
    super(formBuilder3)
  }

  callOnNgInit(): void {
    this.initTypes();
    if (this.types) {
      this.types.sort((a, b) => this.displayLabel(a).localeCompare(this.displayLabel(b)));

      const control = this.form?.get(this.propertyName);
      if (control && (control.value === null || control.value === undefined) && this.types.length > 0) {
        control.setValue(this.types[0], { emitEvent: false }); // do not emit change event at loading
      }
    }

    if (this.form)
      this.form.get(this.propertyName)?.valueChanges.subscribe(
        (newValue) => {
          this.selectionChange.emit(this.model);
        }
      );
  }

  abstract initTypes(): void;

  compareWithId = compareWithId;

  clearField(): void {
    this.model = undefined;
    this.modelChange.emit(this.model);
    this.selectionChange.emit(undefined);
  }
  getPreviewActionLinkFunction(entity: T): string[] | undefined {
    return undefined;
  }

  override displayLabel(object: any): string {
    if (object && object.label)
      return object.label;
    if (object && object.name)
      return object.name;
    if (typeof object === "string")
      return object;
    return "";
  }
}
