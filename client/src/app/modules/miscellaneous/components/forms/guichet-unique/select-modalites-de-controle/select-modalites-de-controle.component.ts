import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ModalitesDeControleService } from 'src/app/modules/miscellaneous/services/guichet-unique/modalites.de.controle.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { ModalitesDeControle } from '../../../../../quotation/model/guichet-unique/referentials/ModalitesDeControle';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-modalites-de-controle',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectModalitesDeControleComponent extends GenericSelectComponent<ModalitesDeControle> implements OnInit {

  types: ModalitesDeControle[] = [] as Array<ModalitesDeControle>;

  constructor(private formBuild: UntypedFormBuilder, private ModalitesDeControleService: ModalitesDeControleService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.ModalitesDeControleService.getModalitesDeControle().subscribe(response => {
      this.types = response;
    })
  }
}
