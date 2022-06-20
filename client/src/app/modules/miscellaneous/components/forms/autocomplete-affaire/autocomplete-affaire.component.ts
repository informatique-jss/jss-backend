import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { AffaireService } from 'src/app/modules/quotation/services/affaire.service';
import { IndexEntityService } from 'src/app/routing/search/index.entity.service';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { AFFAIRE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-affaire',
  templateUrl: './autocomplete-affaire.component.html',
  styleUrls: ['./autocomplete-affaire.component.css']
})
export class AutocompleteAffaireComponent extends GenericAutocompleteComponent<IndexEntity, IndexEntity> implements OnInit {

  constructor(private formBuild: FormBuilder, private indexEntityService: IndexEntityService,
    private affaireService: AffaireService, private changeDetectorRef: ChangeDetectorRef) {
    super(formBuild, changeDetectorRef)
  }

  searchEntities(value: string): Observable<IndexEntity[]> {
    return this.indexEntityService.searchEntities(value);
  }

  mapResponse(response: IndexEntity[]): IndexEntity[] {
    this.filteredTypes = [] as Array<IndexEntity>;
    response.forEach(entity => {
      if (entity.entityType == AFFAIRE_ENTITY_TYPE.entityType) {
        this.filteredTypes?.push(entity);
      }
    })
    return this.filteredTypes;
  }

  getAffaireLabel(entity: IndexEntity): string {
    let obj = JSON.parse((entity.text as string));
    return (obj.firstname ? obj.firstname + " " : "") + (obj.lastname ? obj.lastname + " " : "") + (obj.denomination ? obj.denomination : "");
  }

  public displayAffaire(affaire: Affaire): string {
    return (affaire.firstname != undefined ? affaire.firstname + " " + affaire.lastname : affaire.denomination);
  }
}
