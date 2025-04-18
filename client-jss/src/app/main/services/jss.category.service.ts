import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../services/appRest.service';
import { JssCategory } from '../model/JssCategory';

@Injectable({
  providedIn: 'root'
})
export class JssCategoryService extends AppRestService<JssCategory> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getAvailableJssCategories() {
    return this.getListCached(new HttpParams(), "jss-categories");
  }

  getJssCategoryBySlug(slug: number) {
    return this.get(new HttpParams().set("slug", slug), "jss-category/slug");
  }
}
