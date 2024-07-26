import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { NatureCessationService } from 'src/app/modules/miscellaneous/services/guichet-unique/nature.cessation.service';
import { NatureCessation } from 'src/app/modules/quotation/model/guichet-unique/referentials/NatureCessation';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-nature-cessation',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupNatureCessationComponent extends GenericRadioGroupComponent<NatureCessation> implements OnInit {
  types: NatureCessation[] = [] as Array<NatureCessation>;

  constructor(
    private formBuild: UntypedFormBuilder, private NatureCessationService: NatureCessationService,) {
    super(formBuild,);
  }

  initTypes(): void {
    this.NatureCessationService.getNatureCessation().subscribe(response => { this.types = response })
  }
}
