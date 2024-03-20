import { Component, OnInit, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypeDocument } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeDocument';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { ConstantService } from '../../../services/constant.service';
import { TypeDocumentService } from '../../../services/guichet-unique/type.document.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-type-document',
  templateUrl: '../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../generic-local-autocomplete/generic-local-autocomplete.component.css'],
})
export class AutocompleteTypeDocumentComponent extends GenericLocalAutocompleteComponent<TypeDocument> implements OnInit {

  types: TypeDocument[] = [] as Array<TypeDocument>;

  constructor(private formBuild: UntypedFormBuilder, private typeDocumentService: TypeDocumentService,
    private constantService: ConstantService,
    private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
  }

  filterEntities(types: TypeDocument[], value: string): TypeDocument[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(typeDocument =>
      typeDocument.label != undefined && (typeDocument.label.toLowerCase().includes(filterValue)));
  }

  initTypes(): void {
    this.typeDocumentService.getTypeDocument().subscribe(response => {
      this.types = response;
    });
  }
}
