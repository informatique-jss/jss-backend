import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Rna } from 'src/app/modules/quotation/model/Rna';
import { RnaService } from 'src/app/modules/quotation/services/rna.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-rna',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteRnaComponent extends GenericAutocompleteComponent<Rna, Rna> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private rnaService: RnaService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<Rna[]> {
    this.expectedMinLengthInput = 10;
    return this.rnaService.getRna(value);
  }

  displayLabel(rna: Rna): string {
    if (!rna)
      return "";
    if (!rna.association)
      return rna + "";
    return rna.association.id_association;
  }
}
