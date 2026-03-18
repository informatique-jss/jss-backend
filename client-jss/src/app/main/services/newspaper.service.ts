import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from '../../services/appRest.service';
import { Newspaper } from '../model/Newspaper';

@Injectable({
  providedIn: 'root'
})
export class NewspaperService extends AppRestService<Newspaper> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getNewspapersForYear(year: number) {
    return this.getList(new HttpParams().set("year", year), "newspapers/year");
  }

  getPdfForUser(newspaperId: number) {
    this.downloadGet(new HttpParams().set("newspaperId", newspaperId), "newspaper/download");
  }

  canSeeAllNewspapersOfKiosk(): Observable<boolean> {
    return this.get(new HttpParams(), "newspapers/can-see-all-newpapers") as any as Observable<boolean>;
  }

  getSeeableNewspapersForCurrentUser() {
    return this.getList(new HttpParams(), "newspapers/list-seeable-newpapers") as any as Observable<number[]>;
  }
}
