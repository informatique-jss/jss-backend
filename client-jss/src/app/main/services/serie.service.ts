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

  getSeries(page: number, size: number) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", size), "series");
  }

  getSerieBySlug(slug: string) {
    return this.get(new HttpParams().set("slug", slug), "serie/slug");
  }

}
