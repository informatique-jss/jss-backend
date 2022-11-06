import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ActiviteReguliereService } from 'src/app/modules/miscellaneous/services/guichet-unique/activite.reguliere.service';
import { ActiviteReguliere } from 'src/app/modules/quotation/model/guichet-unique/referentials/ActiviteReguliere';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-activite-reguliere',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupActiviteReguliereComponent extends GenericRadioGroupComponent<ActiviteReguliere> implements OnInit {
  types: ActiviteReguliere[] = [] as Array<ActiviteReguliere>;

  constructor(
    private formBuild: UntypedFormBuilder, private ActiviteReguliereService: ActiviteReguliereService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.ActiviteReguliereService.getActiviteReguliere().subscribe(response => { this.types = response })
  }
}
