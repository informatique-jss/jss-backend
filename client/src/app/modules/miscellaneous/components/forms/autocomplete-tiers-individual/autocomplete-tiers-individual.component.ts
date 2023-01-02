import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { IndexEntityService } from '../../../../../routing/search/index.entity.service';
import { IndexEntity } from '../../../../../routing/search/IndexEntity';
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

  searchEntities(value: string): Observable<IndexEntity[]> {
    return this.indexEntityService.getIndividualTiersByKeyword(value);
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
