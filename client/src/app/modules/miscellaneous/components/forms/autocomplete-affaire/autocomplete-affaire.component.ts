import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { AffaireService } from 'src/app/modules/quotation/services/affaire.service';
import { IndexEntityService } from 'src/app/routing/search/index.entity.service';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { AFFAIRE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-affaire',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css'],
})
export class AutocompleteAffaireComponent extends GenericAutocompleteComponent<IndexEntity, IndexEntity> implements OnInit {
  @ViewChild('affaireInput') affaireInput: ElementRef<HTMLInputElement> | undefined;

  constructor(private formBuild: UntypedFormBuilder, private indexEntityService: IndexEntityService,
    private affaireService: AffaireService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
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

  displayLabel(entity: IndexEntity): string {
    if (entity && entity.text) {
      let obj = JSON.parse((entity.text as string));
      return (obj.firstname ? obj.firstname + " " : "") + (obj.lastname ? obj.lastname + " " : "") + (obj.denomination ? obj.denomination : "");
    }
    return "";
  }

  displayAffaire(obj: Affaire): string {
    if (obj)
      return (obj.firstname ? obj.firstname + " " : "") + (obj.lastname ? obj.lastname + " " : "") + (obj.denomination ? obj.denomination : "");
    return "";
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
