import { HttpClient, HttpEvent, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from '../../../libs/appRest.service';
import { Attachment } from '../model/Attachment';
import { AttachmentType } from '../model/AttachmentType';
import { IAttachment } from '../model/IAttachment';
import { TypeDocument } from '../model/TypeDocument';

@Injectable({
  providedIn: 'root'
})
export class UploadAttachmentService extends AppRestService<IAttachment> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  uploadAttachment(file: File, entity: IAttachment, entityType: string, attachmentType: AttachmentType, filename: string, typeDocument: TypeDocument | null): Observable<HttpEvent<any>> {
    let formData = new FormData();
    formData.append("idEntity", entity.id + "");
    formData.append("entityType", entityType);
    formData.append("idAttachmentType", attachmentType.id + "");
    formData.append("filename", filename);
    if (typeDocument && typeDocument != null)
      formData.append("typeDocumentCode", typeDocument.code);
    return this.uploadPost('attachment/upload', file, formData);
  }

  downloadAttachment(attachment: Attachment) {
    this.downloadGet(new HttpParams().set("idAttachment", attachment.id + ""), "attachment/download");
  }

  disableAttachment(attachment: Attachment) {
    return this.get(new HttpParams().set("idAttachment", attachment.id + ""), "attachment/disabled");
  }
}
