import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "src/app/services/appRest.service";
import { JssCategory } from "../model/JssCategory";

@Injectable({
  providedIn: 'root'
})
export class JssCategoryService extends AppRestService<JssCategory> {

  constructor(http: HttpClient) {
    super(http, "myjss/wordpress");
  }

  getJssCategories() {
    return this.getList(new HttpParams(), "jss-categories");
  }
}
