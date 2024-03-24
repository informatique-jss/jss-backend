import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AttachmentMailRequest } from '../model/AttachmentMailRequest';
import { MissingAttachmentQuery } from '../model/MissingAttachmentQuery';

@Injectable({
  providedIn: 'root'
})
export class MissingAttachmentQueryService extends AppRestService<MissingAttachmentQuery>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  generateMissingAttachmentMail(query: MissingAttachmentQuery) {
    return this.postItem(new HttpParams(), "mail/generate/missing-attachment", query, "Mail envoyé", "Erreur lors de l'envoi du mail");
  }

  generateAttachmentsMail(query: AttachmentMailRequest) {
    return this.postItem(new HttpParams(), "mail/generate/attachments", query, "Mail envoyé", "Erreur lors de l'envoi du mail");
  }
}
