import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { ReportingWidget } from '../model/ReportingWidget';

@Injectable({
  providedIn: 'root'
})
export class ReportingWidgetService extends AppRestService<ReportingWidget> {

  constructor(http: HttpClient) {
    super(http, "reporting");
  }

  getReportingWidgetPayload(id: number) {
    return this.get(new HttpParams().set("id", id), "reporting-widget/payload") as any as Observable<any>;
  }

}
