import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { DiffusionINSEEService } from 'src/app/modules/miscellaneous/services/guichet-unique/diffusion.insee.service';
import { DiffusionINSEE } from 'src/app/modules/quotation/model/guichet-unique/referentials/DiffusionINSEE';

import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-diffusion-insee',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupDiffusionINSEEComponent extends GenericRadioGroupComponent<DiffusionINSEE> implements OnInit {
  types: DiffusionINSEE[] = [] as Array<DiffusionINSEE>;

  constructor(
    private formBuild: UntypedFormBuilder, private DiffusionINSEEService: DiffusionINSEEService,) {
    super(formBuild,);
  }

  initTypes(): void {
    this.DiffusionINSEEService.getDiffusionINSEE().subscribe(response => { this.types = response })
  }
}
