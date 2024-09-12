import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CodeRolePersonneQualifieeService } from 'src/app/modules/miscellaneous/services/guichet-unique/code.role.personne.qualifiee.service';
import { CodeRolePersonneQualifiee } from '../../../../../quotation/model/guichet-unique/referentials/CodeRolePersonneQualifiee';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-code-role-personne-qualifiee',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectCodeRolePersonneQualifieeComponent extends GenericSelectComponent<CodeRolePersonneQualifiee> implements OnInit {

  types: CodeRolePersonneQualifiee[] = [] as Array<CodeRolePersonneQualifiee>;

  constructor(private formBuild: UntypedFormBuilder, private CodeRolePersonneQualifieeService: CodeRolePersonneQualifieeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.CodeRolePersonneQualifieeService.getCodeRolePersonneQualifiee().subscribe(response => {
      this.types = response;
    })
  }
}
