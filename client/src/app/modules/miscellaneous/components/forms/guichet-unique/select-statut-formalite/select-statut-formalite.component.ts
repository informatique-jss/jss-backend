import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { StatutFormaliteService } from 'src/app/modules/miscellaneous/services/guichet-unique/statut.formalite.service';
import { StatutFormalite } from '../../../../../quotation/model/guichet-unique/referentials/StatutFormalite';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-statut-formalite',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectStatutFormaliteComponent extends GenericSelectComponent<StatutFormalite> implements OnInit {

  types: StatutFormalite[] = [] as Array<StatutFormalite>;

  constructor(private formBuild: UntypedFormBuilder, private StatutFormaliteService: StatutFormaliteService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.StatutFormaliteService.getStatutFormalite().subscribe(response => {
      this.types = response.sort((a, b) => { return a.label.localeCompare(b.label) });
    })
  }
}
