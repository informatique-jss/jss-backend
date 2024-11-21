import { Directive, EventEmitter, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { AppService } from 'src/app/services/app.service';
import { GenericFormComponent } from '../generic-form.components';

@Directive()
export abstract class GenericSelectComponent<T> extends GenericFormComponent implements OnInit {

  /**
 * Triggered when value is changed by user
 */
  @Output() selectionChange: EventEmitter<T> = new EventEmitter();

  abstract types: T[];

  constructor(
    private formBuilder3: UntypedFormBuilder, private appService2: AppService
  ) {
    super(formBuilder3, appService2)
  }

  callOnNgInit(): void {
    this.initTypes();
    if (this.types)
      this.types.sort((a, b) => this.displayLabel(a).localeCompare(this.displayLabel(b)));

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

  displayLabel(object: any): string {
    if (object && object.label)
      return object.label;
    if (object && object.name)
      return object.name;
    if (typeof object === "string")
      return object;
    return "";
  }
}
