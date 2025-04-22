import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../services/appRest.service';
import { Category } from '../model/Category';

@Injectable({
  providedIn: 'root'
})
export class CategoryService extends AppRestService<Category> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getAvailableCategories() {
    return this.getListCached(new HttpParams(), "categories");
  }

}
