import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { StatutContratService } from 'src/app/modules/miscellaneous/services/guichet-unique/statut.contrat.service';
import { StatutContrat } from '../../../../../quotation/model/guichet-unique/referentials/StatutContrat';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-statut-contrat',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectStatutContratComponent extends GenericSelectComponent<StatutContrat> implements OnInit {

  types: StatutContrat[] = [] as Array<StatutContrat>;

  constructor(private formBuild: UntypedFormBuilder, private StatutContratService: StatutContratService,) {
    super(formBuild,)
  }

  initTypes(): void {
    this.StatutContratService.getStatutContrat().subscribe(response => {
      this.types = response;
    })
  }
}
