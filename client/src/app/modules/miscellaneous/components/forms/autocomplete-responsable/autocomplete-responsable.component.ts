import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { IndexEntityService } from '../../../../../routing/search/index.entity.service';
import { IndexEntity } from '../../../../../routing/search/IndexEntity';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-responsable',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteResponsableComponent extends GenericAutocompleteComponent<IndexEntity, IndexEntity> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private indexEntityService: IndexEntityService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<IndexEntity[]> {
    return this.indexEntityService.getResponsableByKeyword(value);
  }

  displayLabel(responsable: IndexEntity): string {
    if (!responsable || !responsable.text)
      return "";
    let text = JSON.parse(responsable.text);
    let label = text.firstname + " " + text.lastname;
    if (text.tiers && text.tiers.denomination)
      label += " (" + text.tiers.denomination + ")";
    else if (text.tiers && text.tiers.firstname)
      label += " (" + text.tiers.firstname + " " + text.tiers.lastname + ")";
    return label;
  }
}
