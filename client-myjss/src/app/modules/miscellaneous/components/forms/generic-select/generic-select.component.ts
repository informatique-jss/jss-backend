import { Directive, EventEmitter, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from '../../../../../libs/app.service';
import { compareWithId } from '../../../../../libs/CompareHelper';
import { GenericFormComponent } from '../generic-form.components';

@Directive()
export abstract class GenericSelectComponent<T> extends GenericFormComponent implements OnInit {

  @Output() selectionChange: EventEmitter<T> = new EventEmitter();
  selectedType: T | undefined;

  abstract types: T[];
  constructor(
    private formBuilder3: UntypedFormBuilder, private appService2: AppService
  ) {
    super(formBuilder3,)
  }

  callOnNgInit(): void {
    this.initTypes();
    if (this.types) {
      this.types.sort((a, b) => this.displayLabel(a).localeCompare(this.displayLabel(b)));
    }

    if (this.form) {
      this.form.get(this.propertyName)?.valueChanges.subscribe((newValue) => {
        this.selectionChange.emit(newValue);
      });
    }

  }

  abstract initTypes(): void;

  compareWithId = compareWithId;


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
