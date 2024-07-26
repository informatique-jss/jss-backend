import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { DocumentType } from '../../../model/DocumentType';
import { DocumentTypeService } from '../../../services/document.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-document-type',
  templateUrl: './select-document-type.component.html',
  styleUrls: ['./select-document-type.component.css']
})
export class SelectDocumentTypeComponent extends GenericSelectComponent<DocumentType> implements OnInit {

  types: DocumentType[] = [] as Array<DocumentType>;

  constructor(private formBuild: UntypedFormBuilder, private documentTypeService: DocumentTypeService,) {
    super(formBuild)
  }

  initTypes(): void {
    this.documentTypeService.getDocumentTypes().subscribe(response => {
      this.types = response;
    })
  }
}
