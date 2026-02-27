

import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { Notice } from '../../tools/model/Notice';

@Injectable({
  providedIn: 'root'
})
export class NoticeService extends AppRestService<Notice> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getNoticeFromFile(file: File) {
    let formData = new FormData();
    formData.append("file", file);
    return this.postItem(new HttpParams(), 'extract-text-from-file', formData);
  }
}
