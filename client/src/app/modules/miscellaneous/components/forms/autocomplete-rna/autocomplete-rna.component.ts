import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { AffaireService } from 'src/app/modules/quotation/services/affaire.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-rna',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteRnaComponent extends GenericAutocompleteComponent<Affaire, Affaire> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private affaireService: AffaireService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<Affaire[]> {
    this.expectedMinLengthInput = 14;
    return this.affaireService.generateAffaireByRna(value);
  }

  displayLabel(affaire: Affaire): string {
    if (!affaire)
      return "";
    if (!affaire.denomination && !affaire.rna)
      return affaire + "";
    return affaire.denomination + " - " + affaire.rna;
  }
}
