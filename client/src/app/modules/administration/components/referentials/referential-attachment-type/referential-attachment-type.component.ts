import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Attachment } from 'src/app/modules/miscellaneous/model/Attachment';
import { AttachmentType } from 'src/app/modules/miscellaneous/model/AttachmentType';
import { AttachmentTypeService } from 'src/app/modules/miscellaneous/services/attachment.type.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { ATTACHMENT_TYPE_ATTACHMENT_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-attachment-type',
  templateUrl: './referential-attachment-type.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialAttachmentTypeComponent extends GenericReferentialComponent<AttachmentType> implements OnInit {

  ATTACHMENT_TYPE_ATTACHMENT_TYPE = ATTACHMENT_TYPE_ATTACHMENT_TYPE;
  attachmentTypeTemplate: AttachmentType = this.constantService.getAttachmentTypeTemplate();

  constructor(private attachmentTypeService: AttachmentTypeService,
    private constantService: ConstantService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.selectedEntity) {
      if (!this.selectedEntity.isToSentOnFinalizationMail)
        this.selectedEntity.isToSentOnFinalizationMail = false;
      if (!this.selectedEntity.isToSentOnUpload)
        this.selectedEntity.isToSentOnUpload = false;
      if (!this.selectedEntity.isHiddenFromUser)
        this.selectedEntity.isHiddenFromUser = false;
    }
  }

  getAddOrUpdateObservable(): Observable<AttachmentType> {
    return this.attachmentTypeService.addOrUpdateAttachmentType(this.selectedEntity!);
  }
  getGetObservable(): Observable<AttachmentType[]> {
    return this.attachmentTypeService.getAttachmentTypes();
  }

  updateAttachments(attachments: Attachment[]) {
    if (attachments && this.selectedEntity) {
      this.selectedEntity.attachments = attachments;
    }
  }
}
