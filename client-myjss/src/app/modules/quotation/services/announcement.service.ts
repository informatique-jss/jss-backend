import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { Announcement } from '../../my-account/model/Announcement';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementService extends AppRestService<Announcement> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getNoticeFromFile(file: File): Observable<string> {
    let formData = new FormData();
    formData.append("file", file);
    return this.postItemAsText(new HttpParams(), 'extract-text-from-file', formData);
  }
}
