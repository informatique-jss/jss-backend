import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { TypeDocumentService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.document.service';
import { TypeDocument } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeDocument';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';
import { TYPE_DOCUMENT_ATTACHMENT_TYPE } from 'src/app/routing/search/search.component';
import { Attachment } from 'src/app/modules/miscellaneous/model/Attachment';
import { ConstantService } from '../../../../miscellaneous/services/constant.service';
import { AttachmentType } from 'src/app/modules/miscellaneous/model/AttachmentType';

@Component({
  selector: 'referential-type-document',
  templateUrl: './referential-type-document.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialTypeDocumentComponent extends GenericReferentialComponent<TypeDocument> implements OnInit {
  constructor(private typeDocumentService: TypeDocumentService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,
    private constantService: ConstantService) {
    super(formBuilder2, appService2);
  }

  TYPE_DOCUMENT_ATTACHMENT_TYPE = TYPE_DOCUMENT_ATTACHMENT_TYPE;
  attachmentTypeTemplate: AttachmentType = this.constantService.getAttachmentTypeTemplate();

  ngOnChanges(changes: SimpleChanges) {
    if (this.selectedEntity) {
      if (!this.selectedEntity.isToDownloadOnProvision)
        this.selectedEntity.isToDownloadOnProvision = false;
    }
  }

  getAddOrUpdateObservable(): Observable<TypeDocument> {
    return this.typeDocumentService.addOrUpdateTypeDocument(this.selectedEntity!);
  }
  getGetObservable(): Observable<TypeDocument[]> {
    return this.typeDocumentService.getTypeDocument();
  }

  updateAttachments(attachments: Attachment[]) {
    if (attachments && this.selectedEntity) {
      this.selectedEntity.attachments = attachments;
    }
  }
}
