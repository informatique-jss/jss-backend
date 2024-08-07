import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { StatutPourLaFormaliteService } from 'src/app/modules/miscellaneous/services/guichet-unique/statut.pour.la.formalite.service';
import { StatutPourLaFormalite } from '../../../../../quotation/model/guichet-unique/referentials/StatutPourLaFormalite';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-statut-pour-la-formalite',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectStatutPourLaFormaliteComponent extends GenericSelectComponent<StatutPourLaFormalite> implements OnInit {

  types: StatutPourLaFormalite[] = [] as Array<StatutPourLaFormalite>;

  constructor(private formBuild: UntypedFormBuilder, private StatutPourLaFormaliteService: StatutPourLaFormaliteService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.StatutPourLaFormaliteService.getStatutPourLaFormalite().subscribe(response => {
      this.types = response;
    })
  }
}
