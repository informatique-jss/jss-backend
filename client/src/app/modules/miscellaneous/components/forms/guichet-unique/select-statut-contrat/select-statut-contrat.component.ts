import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { StatutContratService } from 'src/app/modules/miscellaneous/services/guichet-unique/statut.contrat.service';
import { StatutContrat } from '../../../../../quotation/model/guichet-unique/referentials/StatutContrat';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-statut-contrat',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectStatutContratComponent extends GenericSelectComponent<StatutContrat> implements OnInit {

  types: StatutContrat[] = [] as Array<StatutContrat>;

  constructor(private formBuild: UntypedFormBuilder, private StatutContratService: StatutContratService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.StatutContratService.getStatutContrat().subscribe(response => {
      this.types = response;
    })
  }
}
