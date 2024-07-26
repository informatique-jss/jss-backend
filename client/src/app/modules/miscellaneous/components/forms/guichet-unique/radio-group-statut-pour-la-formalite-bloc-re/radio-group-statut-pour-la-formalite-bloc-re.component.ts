import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { StatutPourLaFormaliteBlocReService } from 'src/app/modules/miscellaneous/services/guichet-unique/statut.pour.la.formalite.bloc.re.service';
import { StatutPourLaFormaliteBlocRe } from 'src/app/modules/quotation/model/guichet-unique/referentials/StatutPourLaFormaliteBlocRe';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-statut-pour-la-formalite-bloc-re',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupStatutPourLaFormaliteBlocReComponent extends GenericRadioGroupComponent<StatutPourLaFormaliteBlocRe> implements OnInit {
  types: StatutPourLaFormaliteBlocRe[] = [] as Array<StatutPourLaFormaliteBlocRe>;

  constructor(
    private formBuild: UntypedFormBuilder, private StatutPourLaFormaliteBlocReService: StatutPourLaFormaliteBlocReService,) {
    super(formBuild,);
  }

  initTypes(): void {
    this.StatutPourLaFormaliteBlocReService.getStatutPourLaFormaliteBlocRe().subscribe(response => { this.types = response })
  }
}
