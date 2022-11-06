import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { StatutPourLaFormaliteService } from 'src/app/modules/miscellaneous/services/guichet-unique/statut.pour.la.formalite.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { StatutPourLaFormalite } from '../../../../../quotation/model/guichet-unique/referentials/StatutPourLaFormalite';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-statut-pour-la-formalite',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectStatutPourLaFormaliteComponent extends GenericSelectComponent<StatutPourLaFormalite> implements OnInit {

  types: StatutPourLaFormalite[] = [] as Array<StatutPourLaFormalite>;

  constructor(private formBuild: UntypedFormBuilder, private StatutPourLaFormaliteService: StatutPourLaFormaliteService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.StatutPourLaFormaliteService.getStatutPourLaFormalite().subscribe(response => {
      this.types = response;
    })
  }
}
