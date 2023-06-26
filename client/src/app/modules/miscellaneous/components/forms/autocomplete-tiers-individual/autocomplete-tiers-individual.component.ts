import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { IndexEntity } from '../../../../../routing/search/IndexEntity';
import { IndexEntityService } from '../../../../../routing/search/index.entity.service';
import { TIERS_ENTITY_TYPE } from '../../../../../routing/search/search.component';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-tiers-individual',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteTiersIndividualComponent extends GenericAutocompleteComponent<IndexEntity, IndexEntity> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private indexEntityService: IndexEntityService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  @Input() authorizeNonIndividualTiers: boolean = false;

  searchEntities(value: string): Observable<IndexEntity[]> {
    if (this.authorizeNonIndividualTiers)
      return this.indexEntityService.searchEntitiesByType(value, TIERS_ENTITY_TYPE);
    else
      return this.indexEntityService.getIndividualTiersByKeyword(value);
  }

  mapResponse(response: IndexEntity[]): IndexEntity[] {
    let out: IndexEntity[] = [];
    if (response)
      for (let u of response) {
        let text = JSON.parse(u.text);
        if (text.isIndividual || this.authorizeNonIndividualTiers)
          out.push(u);
      }
    return out;
  }

  displayLabel(tiers: IndexEntity): string {
    if (!tiers)
      return "";
    let text = JSON.parse(tiers.text);
    if (text.denomination)
      return text.denomination!;
    if (text.firstname)
      return text.firstname + " " + text.lastname;
    return "";
  }
}
