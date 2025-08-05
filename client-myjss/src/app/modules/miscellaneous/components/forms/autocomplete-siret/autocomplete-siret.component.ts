import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AutocompleteLibModule } from 'angular-ng-autocomplete';
import { Observable } from 'rxjs';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { ConstantService } from '../../../../main/services/constant.service';
import { Affaire } from '../../../../my-account/model/Affaire';
import { AffaireService } from '../../../../my-account/services/affaire.service';
import { PagedContent } from '../../../model/PagedContent';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-siret',
  templateUrl: './../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['./../generic-autocomplete/generic-autocomplete.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, AutocompleteLibModule]
})
export class AutocompleteSiretComponent extends GenericAutocompleteComponent<Affaire, Affaire> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder,
    private affaireService: AffaireService,
    private constantService: ConstantService) {
    super(formBuild)
  }

  override searchKeyword: string = 'sirenOrSiret';
  override debounceTime = 700;

  searchEntities(value: string): Observable<PagedContent<Affaire>> {
    return this.affaireService.getAffaireBySiret(value, this.page, this.pageSize);
  }

  override mapResponse(response: Affaire[]): Affaire[] {
    if (response)
      for (let affaire of response)
        affaire.sirenOrSiret = affaire.siret ? affaire.siret : affaire.siren;

    if (response && response.length == 1)
      this.optionSelected(response[0]);
    return response;
  }

  override displayLabel(object: Affaire): string {
    if (object) {
      let label = [];
      if (object.siret)
        label.push(object.siret)
      else if (object.siren)
        label.push(object.siren);
      if (object.denomination)
        label.push(object.denomination);
      if (object.firstname)
        label.push(object.firstname + ' ' + object.lastname)
      if (object.address)
        label.push(object.address)
      if (object.postalCode)
        label.push(object.postalCode)
      if (object.city)
        label.push(object.city.label)
      return label.join(' - ');
    }
    return "";
  }

}
