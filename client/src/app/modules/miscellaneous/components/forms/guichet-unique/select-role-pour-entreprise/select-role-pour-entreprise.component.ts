import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RolePourEntrepriseService } from 'src/app/modules/miscellaneous/services/guichet-unique/role.pour.entreprise.service';
import { RolePourEntreprise } from '../../../../../quotation/model/guichet-unique/referentials/RolePourEntreprise';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-role-pour-entreprise',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectRolePourEntrepriseComponent extends GenericSelectComponent<RolePourEntreprise> implements OnInit {

  types: RolePourEntreprise[] = [] as Array<RolePourEntreprise>;

  constructor(private formBuild: UntypedFormBuilder, private RolePourEntrepriseService: RolePourEntrepriseService,) {
    super(formBuild)
  }

  initTypes(): void {
    this.RolePourEntrepriseService.getRolePourEntreprise().subscribe(response => {
      this.types = response;
    })
  }
}
