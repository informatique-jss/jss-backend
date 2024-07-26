import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActType } from 'src/app/modules/quotation/model/ActType';
import { ActTypeService } from 'src/app/modules/quotation/services/act-type.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-act-type',
  templateUrl: '../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupActTypeComponent extends GenericRadioGroupComponent<ActType> implements OnInit {
  types: ActType[] = [] as Array<ActType>;

  constructor(
    private formBuild: UntypedFormBuilder, private actTypeService: ActTypeService,) {
    super(formBuild);
  }

  initTypes(): void {
    this.actTypeService.getActTypes().subscribe(response => { this.types = response })
  }
}
