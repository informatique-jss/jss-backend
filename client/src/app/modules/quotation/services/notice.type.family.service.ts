import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { NoticeTypeFamily } from '../../quotation/model/NoticeTypeFamily';

@Injectable({
  providedIn: 'root'
})
export class NoticeTypeFamilyService extends AppRestService<NoticeTypeFamily>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getNoticeTypeFamilies() {
    return this.getListCached(new HttpParams(), "notice-type-families");
  }

  addOrUpdateNoticeTypeFamily(noticeTypeFamily: NoticeTypeFamily) {
    this.clearListCache(new HttpParams(), "notice-type-families");
    return this.addOrUpdate(new HttpParams(), "notice-type-family", noticeTypeFamily, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
