import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../services/appRest.service';
import { MyJssCategory } from '../model/MyJssCategory';

@Injectable({
  providedIn: 'root'
})
export class MyJssCategoryService extends AppRestService<MyJssCategory>{

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getAvailableMyJssCategories() {
    return this.getListCached(new HttpParams(), "myjss-categories");
  }

}