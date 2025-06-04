import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../main/services/appRest.service";
import { Tag } from "../model/Tag";

@Injectable({
  providedIn: 'root'
})
export class TagService extends AppRestService<Tag> {

  constructor(http: HttpClient) {
    super(http, "crm");
  }
  getFollowedTagForCurrentUser() {
    return this.getList(new HttpParams(), "preferences/followed/tag");
  }
}
