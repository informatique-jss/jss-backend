import { HttpClient, HttpEvent, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { instanceOfIAttachmentCode } from 'src/app/libs/TypeHelper';
import { AppRestService } from 'src/app/services/appRest.service';
import { TypeDocument } from '../../quotation/model/guichet-unique/referentials/TypeDocument';
import { Attachment } from '../model/Attachment';
import { AttachmentType } from '../model/AttachmentType';
import { IAttachment } from '../model/IAttachment';
import { IAttachmentCode } from '../model/IAttachmentCode';

@Injectable({
  providedIn: 'root'
})
export class UploadAttachmentService extends AppRestService<IAttachment> {

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  uploadAttachment(file: File, entity: IAttachment | IAttachmentCode, entityType: string, attachmentType: AttachmentType, filename: string,
    replaceExistingAttachementType: boolean, pageSelection: string | null, typeDocument: TypeDocument | null): Observable<HttpEvent<any>> {
    let formData = new FormData();
    if (instanceOfIAttachmentCode(entity)) {
      formData.append("codeEntity", entity.code + "");
    } else {
      formData.append("idEntity", entity.id + "");
    }
    formData.append("entityType", entityType);
    formData.append("idAttachmentType", attachmentType.id + "");
    formData.append("filename", filename);
    formData.append("pageSelection", pageSelection + "");
    if (typeDocument && typeDocument != null)
      formData.append("typeDocumentCode", typeDocument.code);
    formData.append("replaceExistingAttachementType", replaceExistingAttachementType ? "true" : "false");
    return this.uploadPost('attachment/upload', file, formData);
  }

  previewAttachment(attachment: Attachment) {
    this.previewFileGet(new HttpParams().set("idAttachment", attachment.id + ""), "attachment/download");
  }

  previewAttachmentUrl(attachment: Attachment): any {
    return this.previewFileUrl(new HttpParams().set("idAttachment", attachment.id + ""), "attachment/download");
  }

  disableAttachment(attachment: Attachment) {
    return this.get(new HttpParams().set("idAttachment", attachment.id + ""), "attachment/disabled");
  }

  downloadAttachment(attachment: Attachment) {
    this.downloadGet(new HttpParams().set("idAttachment", attachment.id + ""), "attachment/download", (attachment && attachment.uploadedFile && attachment.uploadedFile.filename) ? attachment.uploadedFile.filename : attachment.id + "");
  }

  downloadInvoiceAttachmentsAsZip(attachmentIds: number[]) {
    let params = new HttpParams();
    attachmentIds.forEach(id => {
      params = params.append('ids', id.toString());
    });

    this.downloadGet(params, "attachment/download-all", "attachments.zip");
  }
}
