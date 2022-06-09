import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { NoticeTypeFamily } from '../../quotation/model/NoticeTypeFamily';

@Injectable({
  providedIn: 'root'
})
export class NoticeTypeFamilyService extends AppRestService<NoticeTypeFamily>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getNoticeTypeFamilies() {
    return this.getList(new HttpParams(), "notice-type-families");
  }

}
