import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../services/appRest.service';
import { Announcement } from '../model/Announcement';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService extends AppRestService<Announcement> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getTopAnnouncement(page: number) {
    return this.getList(new HttpParams().set("page", page), "announcement/top");
  }

  getAnnouncement(id: number) {
    return this.get(new HttpParams().set("announcementId", id), "announcement/unique");
  }

  getTopAnnouncementSearch(page: number, denomination: string, siren: string, noticeSearch: string) {
    return this.getList(new HttpParams().set("page", page).set("denomination", denomination).set("noticeSearch", noticeSearch).set("siren", siren), "announcement/search");
  }
}
