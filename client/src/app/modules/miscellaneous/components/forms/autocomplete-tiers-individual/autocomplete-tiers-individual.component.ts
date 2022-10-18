import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { TiersService } from 'src/app/modules/tiers/services/tiers.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-tiers-individual',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteTiersIndividualComponent extends GenericAutocompleteComponent<Tiers, Tiers> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private tiersService: TiersService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<Tiers[]> {
    this.expectedMinLengthInput = 14;
    return this.tiersService.getIndividualTiersByKeyword(value);
  }

  displayLabel(tiers: Tiers): string {
    if (!tiers)
      return "";
    if (tiers.denomination)
      return tiers.denomination!;
    return tiers.firstname + " " + tiers.lastname;
  }
}
