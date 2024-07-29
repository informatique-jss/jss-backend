import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RoleConjointService } from 'src/app/modules/miscellaneous/services/guichet-unique/role.conjoint.service';
import { RoleConjoint } from '../../../../../quotation/model/guichet-unique/referentials/RoleConjoint';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-role-conjoint',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectRoleConjointComponent extends GenericSelectComponent<RoleConjoint> implements OnInit {

  types: RoleConjoint[] = [] as Array<RoleConjoint>;

  constructor(private formBuild: UntypedFormBuilder, private RoleConjointService: RoleConjointService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.RoleConjointService.getRoleConjoint().subscribe(response => {
      this.types = response;
    })
  }
}
