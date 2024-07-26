import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TaciteReconductionService } from 'src/app/modules/miscellaneous/services/guichet-unique/tacite.reconduction.service';
import { TaciteReconduction } from 'src/app/modules/quotation/model/guichet-unique/referentials/TaciteReconduction';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-tacite-reconduction',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupTaciteReconductionComponent extends GenericRadioGroupComponent<TaciteReconduction> implements OnInit {
  types: TaciteReconduction[] = [] as Array<TaciteReconduction>;

  constructor(
    private formBuild: UntypedFormBuilder, private TaciteReconductionService: TaciteReconductionService,) {
    super(formBuild,);
  }

  initTypes(): void {
    this.TaciteReconductionService.getTaciteReconduction().subscribe(response => { this.types = response })
  }
}
