import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AnnouncementStatus } from '../../quotation/model/AnnouncementStatus';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementStatusService extends AppRestService<AnnouncementStatus>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAnnouncementStatus() {
    return this.getList(new HttpParams(), "announcement-status");
  }

}
