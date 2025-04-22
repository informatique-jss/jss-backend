import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../../libs/appRest.service";
import { MyJssCategory } from '../model/MyJssCategory';

@Injectable({
  providedIn: 'root'
})
export class MyJssCategoryService extends AppRestService<MyJssCategory> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getMyJssCategories() {
    return this.getList(new HttpParams(), "myjss-categories");
  }

}
