import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RoleContratService } from 'src/app/modules/miscellaneous/services/guichet-unique/role.contrat.service';
import { RoleContrat } from 'src/app/modules/quotation/model/guichet-unique/referentials/RoleContrat';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-role-contrat',
  templateUrl: '../../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupRoleContratComponent extends GenericRadioGroupComponent<RoleContrat> implements OnInit {
  types: RoleContrat[] = [] as Array<RoleContrat>;

  constructor(
    private formBuild: UntypedFormBuilder, private RoleContratService: RoleContratService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.RoleContratService.getRoleContrat().subscribe(response => { this.types = response })
  }
}
