import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SituationVisAVisMsaService } from 'src/app/modules/miscellaneous/services/guichet-unique/situation.visavis.msa.service';
import { SituationVisAVisMsa } from 'src/app/modules/quotation/model/guichet-unique/referentials/SituationVisAVisMsa';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-situation-visavis-msa',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupSituationVisAVisMsaComponent extends GenericRadioGroupComponent<SituationVisAVisMsa> implements OnInit {
  types: SituationVisAVisMsa[] = [] as Array<SituationVisAVisMsa>;

  constructor(
    private formBuild: UntypedFormBuilder, private SituationVisAVisMsaService: SituationVisAVisMsaService,) {
    super(formBuild,);
  }

  initTypes(): void {
    this.SituationVisAVisMsaService.getSituationVisAVisMsa().subscribe(response => { this.types = response })
  }
}
