import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Announcement } from '../model/Announcement';

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

  previewPublicationFlag(announcement: Announcement) {
    this.previewFileGet(new HttpParams().set("idAnnouncement", announcement.id + ""), "publication/flag/download");
  }
}
