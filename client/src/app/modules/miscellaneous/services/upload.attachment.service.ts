import { HttpClient, HttpEvent, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/appRest.service';
import { Attachment } from '../model/Attachment';
import { AttachmentType } from '../model/AttachmentType';
import { IAttachment } from '../model/IAttachment';

@Injectable({
  providedIn: 'root'
})
export class UploadAttachmentService extends AppRestService<IAttachment>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  uploadAttachment(file: File, entity: IAttachment, entityType: string, attachmentType: AttachmentType, filename: string, replaceExistingAttachementType: boolean): Observable<HttpEvent<any>> {
    let formData = new FormData();
    formData.append("idEntity", entity.id + "");
    formData.append("entityType", entityType);
    formData.append("idAttachmentType", attachmentType.id + "");
    formData.append("filename", filename);
    formData.append("replaceExistingAttachementType", replaceExistingAttachementType ? "true" : "false");
    return this.uploadPost('attachment/upload', file, formData);
  }

  previewAttachment(attachment: Attachment) {
    this.previewFileGet(new HttpParams().set("idAttachment", attachment.id + ""), "attachment/download");
  }

  downloadAttachment(attachment: Attachment) {
    this.downloadGet(new HttpParams().set("idAttachment", attachment.id + ""), "attachment/download");
  }
}
