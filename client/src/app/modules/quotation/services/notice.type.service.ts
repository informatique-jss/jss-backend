import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { NoticeType } from '../../quotation/model/NoticeType';

@Injectable({
  providedIn: 'root'
})
export class NoticeTypeService extends AppRestService<NoticeType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getNoticeTypes() {
    return this.getListCached(new HttpParams(), "notice-types");
  }

  addOrUpdateNoticeType(noticeType: NoticeType) {
    this.clearListCache(new HttpParams(), "notice-types");
    return this.addOrUpdate(new HttpParams(), "notice-type", noticeType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
