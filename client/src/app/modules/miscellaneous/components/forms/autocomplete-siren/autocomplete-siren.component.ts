import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Siren } from 'src/app/modules/quotation/model/Siren';
import { SirenService } from 'src/app/modules/quotation/services/siren.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-siren',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteSirenComponent extends GenericAutocompleteComponent<Siren, Siren> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private sirenService: SirenService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<Siren[]> {
    this.expectedMinLengthInput = 9;
    return this.sirenService.getSiren(value);
  }

  displayLabel(siren: Siren): string {
    if (!siren)
      return "";
    if (!siren.uniteLegale)
      return siren + "";
    return siren.uniteLegale.siren;
  }
}
