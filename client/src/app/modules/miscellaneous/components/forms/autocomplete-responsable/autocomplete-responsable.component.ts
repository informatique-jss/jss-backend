import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { IndexEntity } from '../../../../../routing/search/IndexEntity';
import { IndexEntityService } from '../../../../../routing/search/index.entity.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-responsable',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteResponsableComponent extends GenericAutocompleteComponent<IndexEntity, IndexEntity> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private indexEntityService: IndexEntityService,) {
    super(formBuild)
  }

  @Input() onlyActive: boolean = true;

  searchEntities(value: string): Observable<IndexEntity[]> {
    return this.indexEntityService.getResponsableByKeyword(value, this.onlyActive);
  }

  displayLabel(responsable: IndexEntity): string {
    if (!responsable)
      return "";
    if (responsable.text) {
      let text = JSON.parse(responsable.text);
      let label = text.firstname + " " + text.lastname;
      if (text.tiers && text.tiers.denomination)
        label += " (" + text.tiers.denomination + " / " + text.tiers.id + ")";
      else if (text.tiers && text.tiers.firstname)
        label += " (" + text.tiers.firstname + " " + text.tiers.lastname + " / " + text.tiers.id + ")";
      return label;
    }
    return (responsable as any).firstname + " " + (responsable as any).lastname;
  }
}
