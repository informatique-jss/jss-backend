import { ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
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
  @ViewChild('affaireInput') affaireInput: ElementRef<HTMLInputElement> | undefined;
  constructor(private formBuild: UntypedFormBuilder, private indexEntityService: IndexEntityService,
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
    return (obj.affaire.firstname ? obj.affaire.firstname + " " : "") + (obj.affaire.lastname ? obj.affaire.lastname + " " : "") + (obj.affaire.denomination ? obj.affaire.denomination : "");
  }

  public displayAffaire(affaire: IndexEntity): string {
    if (affaire == null)
      return "";
    let obj = JSON.parse((affaire.text as string));
    return (obj.affaire.firstname != undefined ? obj.affaire.firstname + " " + obj.affaire.lastname : obj.affaire.denomination);
  }

  clearField(): void {
    this.model = undefined;
    this.modelChange.emit(this.model);
    this.onOptionSelected.emit(undefined);
    if (this.form)
      this.form.get(this.propertyName)?.setValue(null);
    this.affaireInput!.nativeElement.value = '';
  }
}
