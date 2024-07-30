import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RoleEntrepriseService } from 'src/app/modules/miscellaneous/services/guichet-unique/role.entreprise.service';
import { RoleEntreprise } from '../../../../../quotation/model/guichet-unique/referentials/RoleEntreprise';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'autocomplete-role-entreprise',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteRoleEntrepriseComponent extends GenericLocalAutocompleteComponent<RoleEntreprise> implements OnInit {

  types: RoleEntreprise[] = [] as Array<RoleEntreprise>;

  constructor(private formBuild: UntypedFormBuilder, private RoleEntrepriseService: RoleEntrepriseService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  filterEntities(types: RoleEntreprise[], value: string): RoleEntreprise[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.RoleEntrepriseService.getRoleEntreprise().subscribe(response => this.types = response);
  }

}
