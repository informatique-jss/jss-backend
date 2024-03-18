import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AttachmentMailRequest } from '../model/AttachmentMailRequest';
import { CustomerOrder } from '../model/CustomerOrder';
import { MissingAttachmentQuery } from '../model/MissingAttachmentQuery';
import { Provision } from '../model/Provision';

@Injectable({
  providedIn: 'root'
})
export class MissingAttachmentQueryService extends AppRestService<MissingAttachmentQuery>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  generateAttachmentTypeMail(query: MissingAttachmentQuery, customerOrder: CustomerOrder, provision: Provision) {
    return this.postItem(new HttpParams().set("idCustomerOrder", customerOrder.id).set("idProvision", provision.id), "mail/generate/attachment", query, "Mail envoyé", "Erreur lors de l'envoi du mail");
  }

  generateAttachmentsMail(query: AttachmentMailRequest) {
    return this.postItem(new HttpParams(), "mail/generate/attachments", query, "Mail envoyé", "Erreur lors de l'envoi du mail");
  }
}
