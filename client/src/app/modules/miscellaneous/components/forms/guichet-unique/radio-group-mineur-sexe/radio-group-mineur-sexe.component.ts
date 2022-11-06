import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MineurSexeService } from 'src/app/modules/miscellaneous/services/guichet-unique/mineur.sexe.service';
import { MineurSexe } from 'src/app/modules/quotation/model/guichet-unique/referentials/MineurSexe';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-mineur-sexe',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupMineurSexeComponent extends GenericRadioGroupComponent<MineurSexe> implements OnInit {
  types: MineurSexe[] = [] as Array<MineurSexe>;

  constructor(
    private formBuild: UntypedFormBuilder, private MineurSexeService: MineurSexeService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.MineurSexeService.getMineurSexe().subscribe(response => { this.types = response })
  }
}
