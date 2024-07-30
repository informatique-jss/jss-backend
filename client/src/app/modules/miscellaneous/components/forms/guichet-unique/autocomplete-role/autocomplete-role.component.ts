import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RoleService } from 'src/app/modules/miscellaneous/services/guichet-unique/role.service';
import { Role } from '../../../../../quotation/model/guichet-unique/referentials/Role';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'autocomplete-role',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteRoleComponent extends GenericLocalAutocompleteComponent<Role> implements OnInit {

  types: Role[] = [] as Array<Role>;

  constructor(private formBuild: UntypedFormBuilder, private RoleService: RoleService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  filterEntities(types: Role[], value: string): Role[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.RoleService.getRole().subscribe(response => this.types = response);
  }

}
