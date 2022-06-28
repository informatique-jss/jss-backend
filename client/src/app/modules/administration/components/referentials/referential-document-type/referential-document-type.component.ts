import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { DocumentType } from 'src/app/modules/miscellaneous/model/DocumentType';
import { DocumentTypeService } from 'src/app/modules/miscellaneous/services/document.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-document-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialDocumentTypeComponent extends GenericReferentialComponent<DocumentType> implements OnInit {
  constructor(private documentTypeService: DocumentTypeService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<DocumentType> {
    return this.documentTypeService.addOrUpdateDocumentType(this.selectedEntity!);
  }
  getGetObservable(): Observable<DocumentType[]> {
    return this.documentTypeService.getDocumentTypes();
  }
}
