import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { AffaireService } from 'src/app/modules/quotation/services/affaire.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-siren',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteSirenComponent extends GenericAutocompleteComponent<Affaire, Affaire> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private affaireService: AffaireService,) {
    super(formBuild)
  }

  searchEntities(value: string): Observable<Affaire[]> {
    return this.affaireService.generateAffaireBySiren(value);
  }

  displayLabel(affaire: Affaire): string {
    if (!affaire)
      return "";
    if (!affaire.denomination && !affaire.siret)
      return affaire + "";
    return affaire.denomination + " - " + affaire.siret;
  }
}
