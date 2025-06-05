import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../main/services/appRest.service";
import { AssoMailJssCategory } from "../model/AssoMailJssCategory";
import { JssCategory } from "../model/JssCategory";

@Injectable({
  providedIn: 'root'
})
export class AssoMailJssCategoryService extends AppRestService<AssoMailJssCategory> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  followJssCategory(jssCategory: JssCategory) {
    return this.get(new HttpParams().set("idJssCategory", jssCategory.id), "jss-category/follow/add");
  }

  unfollowJssCategory(jssCategory: JssCategory) {
    return this.get(new HttpParams().set("idJssCategory", jssCategory.id), "jss-category/unfollow");
  }

}
