import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { TypeDocument } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeDocument';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { TypeDocumentService } from '../../../services/guichet-unique/type.document.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-type-document',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html']
})

export class SelectTypeDocumentComponent extends GenericSelectComponent<TypeDocument> implements OnInit {

  types: TypeDocument[] = [] as Array<TypeDocument>;

  constructor(private formBuild: UntypedFormBuilder,
    private typeDocumentService: TypeDocumentService,
    private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.typeDocumentService.getTypeDocument().subscribe(response => {
      this.types = response;
    });
  }
}
