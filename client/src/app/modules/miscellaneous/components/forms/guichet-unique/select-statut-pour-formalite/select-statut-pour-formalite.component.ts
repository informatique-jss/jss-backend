import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { StatutPourFormaliteService } from 'src/app/modules/miscellaneous/services/guichet-unique/statut.pour.formalite.service';
import { StatutPourFormalite } from '../../../../../quotation/model/guichet-unique/referentials/StatutPourFormalite';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-statut-pour-formalite',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectStatutPourFormaliteComponent extends GenericSelectComponent<StatutPourFormalite> implements OnInit {

  types: StatutPourFormalite[] = [] as Array<StatutPourFormalite>;

  constructor(private formBuild: UntypedFormBuilder, private StatutPourFormaliteService: StatutPourFormaliteService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.StatutPourFormaliteService.getStatutPourFormalite().subscribe(response => {
      this.types = response;
    })
  }
}
