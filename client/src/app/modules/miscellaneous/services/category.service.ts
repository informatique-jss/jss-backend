import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Category } from '../model/Category';

@Injectable({
  providedIn: 'root'
})
export class CategoryService extends AppRestService<Category> {

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getCategories() {
    return this.getList(new HttpParams(), "categories");
  }

}
