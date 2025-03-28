import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AffaireType, individual, notIndividual } from '../../../../quotation/model/AffaireType';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-affaire-type',
  templateUrl: './radio-group-affaire-type.component.html',
  styleUrls: ['./radio-group-affaire-type.component.css']
})
export class RadioGroupAffaireTypeComponent extends GenericRadioGroupComponent<AffaireType> implements OnInit {
  types: AffaireType[] = [] as Array<AffaireType>;

  constructor(
    private formBuild: UntypedFormBuilder) {
    super(formBuild);
  }

  initTypes(): void {
    this.types.push(notIndividual, individual);
  }
}
