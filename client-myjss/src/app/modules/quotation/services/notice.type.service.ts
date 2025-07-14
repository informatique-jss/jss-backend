import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { NoticeType } from '../model/NoticeType';

@Injectable({
  providedIn: 'root'
})
export class NoticeTypeService extends AppRestService<NoticeType> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getNoticeTypes() {
    return this.getListCached(new HttpParams(), "notice-types");
  }
}
