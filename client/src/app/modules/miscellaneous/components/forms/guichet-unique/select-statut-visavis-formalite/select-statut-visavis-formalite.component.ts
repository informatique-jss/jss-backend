import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { StatutVisAVisFormaliteService } from 'src/app/modules/miscellaneous/services/guichet-unique/statut.visavis.formalite.service';
import { StatutVisAVisFormalite } from '../../../../../quotation/model/guichet-unique/referentials/StatutVisAVisFormalite';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-statut-visavis-formalite',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectStatutVisAVisFormaliteComponent extends GenericSelectComponent<StatutVisAVisFormalite> implements OnInit {

  types: StatutVisAVisFormalite[] = [] as Array<StatutVisAVisFormalite>;

  constructor(private formBuild: UntypedFormBuilder, private StatutVisAVisFormaliteService: StatutVisAVisFormaliteService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.StatutVisAVisFormaliteService.getStatutVisAVisFormalite().subscribe(response => {
      this.types = response;
    })
  }
}
