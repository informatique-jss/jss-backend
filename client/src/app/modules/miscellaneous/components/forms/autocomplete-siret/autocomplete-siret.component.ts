import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { AffaireService } from 'src/app/modules/quotation/services/affaire.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'autocomplete-siret',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteSiretComponent extends GenericAutocompleteComponent<Affaire, Affaire> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private affaireService: AffaireService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  searchEntities(value: string): Observable<Affaire[]> {
    return this.affaireService.generateAffaireBySiret(value);
  }

  displayLabel(affaire: Affaire): string {
    if (!affaire)
      return "";
    if (!affaire.denomination && !affaire.siret)
      return affaire + "";
    return affaire.denomination + " - " + affaire.siret;
  }
}
