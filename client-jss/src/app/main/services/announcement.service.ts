import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from '../../services/appRest.service';
import { Announcement } from '../model/Announcement';
import { PagedContent } from '../model/PagedContent';

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

  getTopAnnouncementSearch(page: number, pageSize: number, searchText: string): Observable<PagedContent<Announcement>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', pageSize.toString());
    if (searchText)
      params = params.set('searchText', searchText);
    return this.getPagedList(params, "announcement/search", "", "");
  }

  downloadPublicationReceipt(announcement: Announcement) {
    this.downloadGet(new HttpParams().set("idAnnouncement", announcement.id + ""), "publication/flag/download");
  }
}
