import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Announcement } from '../model/Announcement';
import { AnnouncementSearch } from '../model/AnnouncementSearch';
import { AssoAffaireOrder } from '../model/AssoAffaireOrder';
import { CustomerOrder } from '../model/CustomerOrder';
import { Provision } from '../model/Provision';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService extends AppRestService<Announcement>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  previewPublicationReceipt(announcement: Announcement, provision: Provision) {
    this.previewFileGet(new HttpParams().set("idAnnouncement", announcement.id + "").set("idProvision", provision.id + ""), "publication/receipt/download");
  }

  generateAndStorePublicationReceipt(announcement: Announcement, provision: Provision) {
    return this.get(new HttpParams().set("idAnnouncement", announcement.id + "").set("idProvision", provision.id + ""), "publication/receipt/store", "Justificatif de parution généré et stocké");
  }

  previewProofReading(announcement: Announcement, provision: Provision) {
    this.previewFileGet(new HttpParams().set("idAnnouncement", announcement.id + "").set("idProvision", provision.id + ""), "proof/reading/download");
  }

  previewPublicationFlag(announcement: Announcement, provision: Provision) {
    this.previewFileGet(new HttpParams().set("idAnnouncement", announcement.id + "").set("idProvision", provision.id + ""), "publication/flag/download");
  }

  generateAndStorePublicationFlag(announcement: Announcement, provision: Provision) {
    return this.get(new HttpParams().set("idAnnouncement", announcement.id + "").set("idProvision", provision.id + ""), "publication/flag/store", "Témoin de publication généré et stocké");
  }

  generatePublicationReceiptMail(customerOrder: CustomerOrder, announcement: Announcement) {
    return this.getList(new HttpParams().set("idCustomerOrder", customerOrder.id).set("idAnnouncement", announcement.id), "mail/generate/publication/receipt", "Mail envoyé !", "Erreur lors de l'envoi du mail");
  }

  generateAnnouncementRequestToConfrereMail(customerOrder: CustomerOrder, asso: AssoAffaireOrder, provision: Provision, announcement: Announcement) {
    return this.getList(new HttpParams().set("idCustomerOrder", customerOrder.id).set("idAnnouncement", announcement.id).set("idProvision", provision.id).set("idAssoAffaireOrder", asso.id), "mail/generate/confrere/request", "Mail envoyé !", "Erreur lors de l'envoi du mail");
  }

  generatePublicationFlagMail(customerOrder: CustomerOrder) {
    return this.getList(new HttpParams().set("idCustomerOrder", customerOrder.id), "mail/generate/publication/flag", "Mail envoyé !", "Erreur lors de l'envoi du mail");
  }

  getAnnouncements(announcementSearch: AnnouncementSearch) {
    return this.postList(new HttpParams(), "announcements/search", announcementSearch);
  }
}
