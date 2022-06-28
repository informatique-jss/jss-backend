import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AttachmentType } from 'src/app/modules/miscellaneous/model/AttachmentType';
import { AttachmentTypeService } from 'src/app/modules/miscellaneous/services/attachment.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-attachment-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialAttachmentTypeComponent extends GenericReferentialComponent<AttachmentType> implements OnInit {
  constructor(private attachmentTypeService: AttachmentTypeService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<AttachmentType> {
    return this.attachmentTypeService.addOrUpdateAttachmentType(this.selectedEntity!);
  }
  getGetObservable(): Observable<AttachmentType[]> {
    return this.attachmentTypeService.getAttachmentTypes();
  }
}
