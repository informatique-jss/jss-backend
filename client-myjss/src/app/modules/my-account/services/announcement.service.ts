import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { Announcement } from '../model/Announcement';
import { Provision } from '../model/Provision';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService extends AppRestService<Announcement> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getAnnouncement(id: number) {
    return this.get(new HttpParams().set("announcementId", id), "announcement/unique");
  }

  downloadPublicationFlag(announcement: Announcement) {
    this.downloadGet(new HttpParams().set("idAnnouncement", announcement.id + ""), "publication/flag/download");
  }

  downloadPublicationReceipt(announcement: Announcement, provision: Provision) {
    this.downloadGet(new HttpParams().set("idAnnouncement", announcement.id + "").set("idProvision", provision.id + ""), "publication/receipt/download");
  }
}
