import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { StatutFormaliteService } from 'src/app/modules/miscellaneous/services/guichet-unique/statut.formalite.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { StatutFormalite } from '../../../../../quotation/model/guichet-unique/referentials/StatutFormalite';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-statut-formalite',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectStatutFormaliteComponent extends GenericSelectComponent<StatutFormalite> implements OnInit {

  types: StatutFormalite[] = [] as Array<StatutFormalite>;

  constructor(private formBuild: UntypedFormBuilder, private StatutFormaliteService: StatutFormaliteService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.StatutFormaliteService.getStatutFormalite().subscribe(response => {
      this.types = response.sort((a, b) => { return a.label.localeCompare(b.label) });
    })
  }
}
