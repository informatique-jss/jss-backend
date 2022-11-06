import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ConditionVersementTVAService } from 'src/app/modules/miscellaneous/services/guichet-unique/condition.versement.tva.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { ConditionVersementTVA } from '../../../../../quotation/model/guichet-unique/referentials/ConditionVersementTVA';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-condition-versement-tva',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectConditionVersementTVAComponent extends GenericSelectComponent<ConditionVersementTVA> implements OnInit {

  types: ConditionVersementTVA[] = [] as Array<ConditionVersementTVA>;

  constructor(private formBuild: UntypedFormBuilder, private ConditionVersementTVAService: ConditionVersementTVAService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.ConditionVersementTVAService.getConditionVersementTVA().subscribe(response => {
      this.types = response;
    })
  }
}
