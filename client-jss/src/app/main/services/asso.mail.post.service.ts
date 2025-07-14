import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../services/appRest.service";
import { AssoMailPost } from "../model/AssoMailPost";

@Injectable({
  providedIn: 'root'
})
export class AssoMailPostService extends AppRestService<AssoMailPost> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getAssoMailPostsByMail() {
    return this.getList(new HttpParams(), "preferences/bookmark/all");
  }
}
