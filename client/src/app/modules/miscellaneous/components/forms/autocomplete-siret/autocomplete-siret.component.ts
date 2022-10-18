import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Siret } from 'src/app/modules/quotation/model/Siret';
import { SiretService } from 'src/app/modules/quotation/services/siret.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-siret',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteSiretComponent extends GenericAutocompleteComponent<Siret, Siret> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private siretService: SiretService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<Siret[]> {
    this.expectedMinLengthInput = 14;
    return this.siretService.getSiret(value);
  }

  displayLabel(siret: Siret): string {
    if (!siret)
      return "";
    if (!siret.etablissement)
      return siret + "";
    return siret.etablissement.siret;
  }
}
