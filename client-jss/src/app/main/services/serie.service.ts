import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../services/appRest.service';
import { Serie } from '../model/Serie';

@Injectable({
  providedIn: 'root'
})
export class SerieService extends AppRestService<Serie> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getAvailableSeries() {
    return this.getListCached(new HttpParams(), "series");
  }


  getSerieBySlug(slug: string) {
    return this.get(new HttpParams().set("slug", slug), "serie/slug");
  }

}
