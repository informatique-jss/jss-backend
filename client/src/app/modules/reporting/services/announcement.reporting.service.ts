import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AnnouncementReporting } from '../model/AnnouncementReporting';

@Injectable({
  providedIn: 'root'
})
export class AnnouncementReportingService extends AppRestService<AnnouncementReporting>{

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getAnnouncementReporting(columns: string[]) {
    return this.getList(new HttpParams().set("columns", columns.join(",")), "announcement");
  }

}
