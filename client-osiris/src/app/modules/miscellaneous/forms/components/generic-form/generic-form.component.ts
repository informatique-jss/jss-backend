import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormControl, FormGroup, ValidatorFn } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { IId } from '../../../../../libs/tanstack-table/IId';
import { displayLabel } from './display-label-helper';
import { SelectFormHelper, SelectType } from './select-form-helper';

export const INPUT_TYPES = ['checkbox', 'text', 'datetime-local', 'date', 'email', 'number', 'password', 'tel'] as const;
export type InputType = typeof INPUT_TYPES[number];

export const FORM_TYPES = ['input', 'switch', 'select', 'minmax', 'autocomplete'] as const;
export type FormType = typeof FORM_TYPES[number];

@Component({
  selector: 'generic-form',
  templateUrl: './generic-form.component.html',
  styleUrls: ['./generic-form.component.css'],
  standalone: true,
  imports: [...SHARED_IMPORTS]
})
export class GenericFormComponent implements OnInit, OnChanges {
  @Input() formGroup!: FormGroup;
  @Input() model: any;
  @Output() modelChange = new EventEmitter<any>();
  @Input() label = '';
  @Input() validators: ValidatorFn[] | undefined;
  @Input() errorMessages: Record<string, string> | undefined;
  @Input() inputType?: InputType;
  @Input() selectType?: SelectType;
  @Input() type: FormType = 'input';

  fieldName = `field_${Math.random().toString(36).substring(2, 8)}`;
  minKey = 'minValue';
  maxKey = 'maxValue';
  displayLabel = displayLabel;
  selectValues: IId[] | undefined;

  constructor(
    private selectFormHelper: SelectFormHelper
  ) { }

  get control(): FormControl {
    return this.formGroup.get(this.fieldName) as FormControl;
  }

  ngOnInit() {
    if (!this.type)
      throw new Error("type not provided");

    if (this.type == 'input' && !this.inputType)
      throw new Error("input type not provided");

    if (this.type == 'select' && !this.selectType)
      throw new Error("select type not provided");

    if (this.type == 'minmax') {
      if (this.model == undefined)
        return;
      if (!('minValue' in this.model))
        throw new Error("minValue not available in provided model for minmax type")
      if (!('maxValue' in this.model))
        throw new Error("maxValue not available in provided model for minmax type")
      if (!('key' in this.model))
        throw new Error("maxValue not available in provided model for minmax type")

      this.minKey = this.minKey + this.model.key;
      this.maxKey = this.maxKey + this.model.key;

      if (!this.formGroup.contains(this.minKey)) {
        this.formGroup.addControl(
          this.minKey, new FormControl(this.model["minValue"] ?? null)
        );
      }
      if (!this.formGroup.contains(this.maxKey)) {
        this.formGroup.addControl(
          this.maxKey, new FormControl(this.model["maxValue"] ?? null)
        );
      }

      this.formGroup.get(this.minKey)!.valueChanges.subscribe(val => {
        this.model['minValue'] = val as number;
      });
      this.formGroup.get(this.maxKey)!.valueChanges.subscribe(val => {
        this.model['maxValue'] = val as number;
      });

    } else {

      if (this.type === 'input' && this.inputType === 'date' && typeof this.model === 'string') {
        this.model = this.model.substring(0, 10);
      }

      if (!this.formGroup.contains(this.fieldName)) {
        this.formGroup.addControl(
          this.fieldName,
          new FormControl(this.model, this.validators)
        );
      }

      this.control.valueChanges.subscribe(value => {
        if (value !== this.model) {
          this.modelChange.emit(value);
        }
      });
    }

    if (this.type == 'select')
      this.selectFormHelper.getValues(this.selectType).subscribe(response => this.selectValues = response);

  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['model'] != null && changes['model'] != undefined && !changes['model'].firstChange) {
      let newValue = changes['model'].currentValue;

      if (this.type === 'input' && this.inputType === 'date' && typeof newValue === 'string') {
        newValue = newValue.substring(0, 10);
      }

      if (this.control && this.control.value !== newValue) {
        this.control.setValue(newValue, { emitEvent: false });
      }
    }
  }

  get errors(): string[] {
    if (!this.control || !this.control.errors || !this.control.touched || !this.errorMessages) return [];
    return Object.keys(this.control.errors).map(
      key => this.errorMessages![key] ?? key
    );
  }

  compareById(o1: IId, o2: IId): boolean {
    return o1 && o2 ? o1.id === o2.id : o1 === o2;
  }
}
