import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { NoticeTypeFamily } from '../model/NoticeTypeFamily';

@Injectable({
  providedIn: 'root'
})
export class NoticeTypeFamilyService extends AppRestService<NoticeTypeFamily> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getNoticeTypeFamilies() {
    return this.getListCached(new HttpParams(), "notice-type-families");
  }

}
