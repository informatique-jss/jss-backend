import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Announcement } from '../model/Announcement';
import { CustomerOrder } from '../model/CustomerOrder';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService extends AppRestService<Announcement>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  previewPublicationReceipt(announcement: Announcement) {
    this.previewFileGet(new HttpParams().set("idAnnouncement", announcement.id + ""), "publication/receipt/download");
  }

  previewProofReading(announcement: Announcement) {
    this.previewFileGet(new HttpParams().set("idAnnouncement", announcement.id + ""), "proof/reading/download");
  }

  previewPublicationFlag(announcement: Announcement) {
    this.previewFileGet(new HttpParams().set("idAnnouncement", announcement.id + ""), "publication/flag/download");
  }

  generatePublicationReceiptMail(customerOrder: CustomerOrder, announcement: Announcement) {
    return this.getList(new HttpParams().set("idCustomerOrder", customerOrder.id).set("idAnnouncement", announcement.id), "mail/generate/publication/receipt", "Mail envoyé !", "Erreur lors de l'envoi du mail");
  }

  generatePublicationFlagMail(customerOrder: CustomerOrder) {
    return this.getList(new HttpParams().set("idCustomerOrder", customerOrder.id), "mail/generate/publication/flag", "Mail envoyé !", "Erreur lors de l'envoi du mail");
  }
}
