import { ChangeDetectorRef, Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-responsable',
  templateUrl: './select-responsable.component.html',
  styleUrls: ['./select-responsable.component.css']
})
export class SelectResponsableComponent extends GenericMultipleSelectComponent<Responsable> implements OnInit {

  types: Responsable[] = [] as Array<Responsable>;

  /**
 * List of responsables to choose from
 */
  @Input() responsableList: Responsable[] | undefined;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: UntypedFormBuilder) {
    super(changeDetectorRef, formBuild);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.model && this.form != undefined) {
      if (this.responsableList != undefined)
        this.types = this.responsableList;

      this.form.get(this.propertyName)?.setValue(this.model);
      this.changeDetectorRef.detectChanges();
    }
    if (changes.isDisabled) {
      if (this.isDisabled) {
        this.form?.get(this.propertyName)?.disable();
      } else {
        this.form?.get(this.propertyName)?.enable();
      }
    }
    if (this.form && (this.isMandatory || this.customValidators))
      this.form.get(this.propertyName)?.updateValueAndValidity();
  }

  initTypes(): void {
    if (this.responsableList != undefined)
      this.types = this.responsableList;
  }
}
