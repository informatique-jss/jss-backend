import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { StatutPraticienService } from 'src/app/modules/miscellaneous/services/guichet-unique/statut.praticien.service';
import { StatutPraticien } from 'src/app/modules/quotation/model/guichet-unique/referentials/StatutPraticien';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-statut-praticien',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupStatutPraticienComponent extends GenericRadioGroupComponent<StatutPraticien> implements OnInit {
  types: StatutPraticien[] = [] as Array<StatutPraticien>;

  constructor(
    private formBuild: UntypedFormBuilder, private StatutPraticienService: StatutPraticienService,) {
    super(formBuild,);
  }

  initTypes(): void {
    this.StatutPraticienService.getStatutPraticien().subscribe(response => { this.types = response })
  }
}
