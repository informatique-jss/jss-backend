import { Directive, Input, OnInit, SimpleChanges } from "@angular/core";
import { UntypedFormBuilder } from "@angular/forms";
import { GenericFormComponent } from "../generic-form.components";

@Directive()
export abstract class GenericRadioGroupComponent<T> extends GenericFormComponent implements OnInit {

  abstract types: T[];

  /**
 * Indicate if the field is required or not in the formgroup provided
 * Default : false
 */
  @Input() override isMandatory: boolean = true;

  constructor(private formBuilder3: UntypedFormBuilder,) {
    super(formBuilder3)
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    if (this.model && this.types)
      for (let type of this.types) {
        let typeAny = type as any;
        if (typeAny.id && this.model.id && typeAny.id == this.model.id) {
          this.model = type;
          this.form!.get(this.propertyName)?.setValue(this.model);
          this.modelChange.emit(this.model);
        }
      }
  }

  callOnNgInit(): void {
    this.initTypes();
  }

  abstract initTypes(): void;

}
