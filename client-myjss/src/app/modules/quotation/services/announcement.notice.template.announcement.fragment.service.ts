import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { AssoAnnouncementNoticeTemplateAnnouncementFragment } from '../model/AssoAnnouncementNoticeTemplateAnnouncementFragment';

@Injectable({
  providedIn: 'root'
})
export class AssoAnnouncementNoticeTemplateAnnouncementFragmentService extends AppRestService<AssoAnnouncementNoticeTemplateAnnouncementFragment> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAssoAnnouncementNoticeTemplateFragmentByNoticeTemplate(idNoticeTemplateAnnouncement: number) {
    return this.getList(new HttpParams().set("idNoticeTemplateAnnouncement", idNoticeTemplateAnnouncement), "asso-notice-template-fragment");
  }

}
