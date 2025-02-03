import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AttachmentMailRequest } from '../model/AttachmentMailRequest';
import { MissingAttachmentQuery } from '../model/MissingAttachmentQuery';

@Injectable({
  providedIn: 'root'
})
export class MissingAttachmentQueryService extends AppRestService<MissingAttachmentQuery> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  generateMissingAttachmentMail(query: MissingAttachmentQuery, isWaitingForAttachmentToUpload: boolean) {
    return this.postItem(new HttpParams().set("isWaitingForAttachmentToUpload", isWaitingForAttachmentToUpload), "mail/generate/missing-attachment", query, "Mail envoyé", "Erreur lors de l'envoi du mail");
  }

  sendMissingAttachmentQueryImmediatly(query: MissingAttachmentQuery) {
    return this.get(new HttpParams().set("missingAttachmentQueryId", query.id!), "mail/generate/missing-attachment/reminder");
  }

  sendMissingAttachmentQueryWithUploadedFiles(query: MissingAttachmentQuery) {
    return this.get(new HttpParams().set("missingAttachmentQueryId", query.id!), "mail/generate/missing-attachment-upload/sender");
  }

  generateAttachmentsMail(query: AttachmentMailRequest) {
    return this.postItem(new HttpParams(), "mail/generate/attachments", query, "Mail envoyé", "Erreur lors de l'envoi du mail");
  }
}
