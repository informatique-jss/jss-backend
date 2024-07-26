import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { IndexEntityService } from 'src/app/routing/search/index.entity.service';
import { AFFAIRE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-affaire',
  templateUrl: './autocomplete-affaire.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css'],
})
export class AutocompleteAffaireComponent extends GenericAutocompleteComponent<IndexEntity, IndexEntity> implements OnInit {
  @ViewChild('affaireInput') affaireInput: ElementRef<HTMLInputElement> | undefined;

  constructor(private formBuild: UntypedFormBuilder, private indexEntityService: IndexEntityService,) {
    super(formBuild)
  }

  searchEntities(value: string): Observable<IndexEntity[]> {
    return this.indexEntityService.searchEntitiesByType(value, AFFAIRE_ENTITY_TYPE);
  }

  displayLabel(entity: IndexEntity): string {
    if (entity && entity.text) {
      let obj = JSON.parse((entity.text as string));
      return (obj.firstname ? obj.firstname + " " : " ") + (obj.lastname ? obj.lastname + " " : "") + (obj.denomination ? obj.denomination : "") + " - " + (obj.address ? obj.address : "") + " - " +
        (obj.postalCode ? obj.postalCode : "") + " - " + (obj.city && obj.city.label ? obj.city.label : "") + " - " + (obj.siren ? "SIREN : " + obj.siren : "") + " - " + (obj.siret ? "SIRET : " + obj.siret : "");
    }
    if ((entity as any).denomination || (entity as any).firstname) {
      return (entity as any).denomination ? (entity as any).denomination : ((entity as any).firstname + ' ' + (entity as any).lastname);
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
