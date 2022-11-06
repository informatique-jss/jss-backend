import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypeDocumentService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.document.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { TypeDocument } from '../../../../../quotation/model/guichet-unique/referentials/TypeDocument';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-type-document',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteTypeDocumentComponent extends GenericLocalAutocompleteComponent<TypeDocument> implements OnInit {

  types: TypeDocument[] = [] as Array<TypeDocument>;

  constructor(private formBuild: UntypedFormBuilder, private TypeDocumentService: TypeDocumentService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  filterEntities(types: TypeDocument[], value: string): TypeDocument[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.TypeDocumentService.getTypeDocument().subscribe(response => this.types = response);
  }

}
