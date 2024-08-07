import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SecondRoleEntrepriseService } from 'src/app/modules/miscellaneous/services/guichet-unique/second.role.entreprise.service';
import { SecondRoleEntreprise } from '../../../../../quotation/model/guichet-unique/referentials/SecondRoleEntreprise';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'autocomplete-second-role-entreprise',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteSecondRoleEntrepriseComponent extends GenericLocalAutocompleteComponent<SecondRoleEntreprise> implements OnInit {

  types: SecondRoleEntreprise[] = [] as Array<SecondRoleEntreprise>;

  constructor(private formBuild: UntypedFormBuilder, private SecondRoleEntrepriseService: SecondRoleEntrepriseService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  filterEntities(types: SecondRoleEntreprise[], value: string): SecondRoleEntreprise[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.SecondRoleEntrepriseService.getSecondRoleEntreprise().subscribe(response => this.types = response);
  }

}
