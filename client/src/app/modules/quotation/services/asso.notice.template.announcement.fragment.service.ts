import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { AssoAnnouncementNoticeTemplateAnnouncementFragment } from '../model/AssoAnnouncementNoticeTemplateAnnouncementFragment';

@Injectable({
  providedIn: 'root'
})
export class AssoNoticeTemplateAnnouncementFragmentService extends AppRestService<AssoAnnouncementNoticeTemplateAnnouncementFragment> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAssoAnnouncementNoticeTemplateFragmentByNoticeTemplate(idNoticeTemplateAnnouncement: number): Observable<AssoAnnouncementNoticeTemplateAnnouncementFragment[]> {
    return this.getList(new HttpParams().set("idNoticeTemplateAnnouncement", idNoticeTemplateAnnouncement), "asso-notice-template-fragment");
  }

  saveAssoAnnouncementNoticeTemplateFragment(assosAnnouncementNoticeTemplateFragments: AssoAnnouncementNoticeTemplateAnnouncementFragment[]) {
    return this.postList(new HttpParams(), "save/asso-notice-template-fragments", assosAnnouncementNoticeTemplateFragments);
  }
}
