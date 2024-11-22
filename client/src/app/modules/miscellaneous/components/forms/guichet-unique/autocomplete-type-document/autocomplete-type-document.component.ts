import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypeDocumentService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.document.service';
import { AppService } from 'src/app/services/app.service';
import { TypeDocument } from '../../../../../quotation/model/guichet-unique/referentials/TypeDocument';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-type-document',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteTypeDocumentComponent extends GenericLocalAutocompleteComponent<TypeDocument> implements OnInit {

  types: TypeDocument[] = [] as Array<TypeDocument>;
  @Input() onlyDisplayWithAttachmentType: boolean = false;

  constructor(private formBuild: UntypedFormBuilder, private TypeDocumentService: TypeDocumentService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  filterEntities(types: TypeDocument[], value: string): TypeDocument[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.TypeDocumentService.getTypeDocument().subscribe(response => {
      if (this.onlyDisplayWithAttachmentType) {
        for (let type of response)
          if (type.attachmentType)
            this.types.push(type);
      } else
        this.types = response
    });
  }

  displayLabel(object: any): string {
    if (object && object.customLabel)
      return object.customLabel;
    if (object && object.label)
      return object.label;
    if (typeof object === "string")
      return object;
    return "";
  }


}
