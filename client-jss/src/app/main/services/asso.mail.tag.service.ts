import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../services/appRest.service";
import { AssoMailTag } from "../model/AssoMailTag";
import { Tag } from "../model/Tag";

@Injectable({
  providedIn: 'root'
})
export class AssoMailTagService extends AppRestService<AssoMailTag> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  followTag(tag: Tag) {
    return this.get(new HttpParams().set("idTag", tag.id), "tag/follow/add");
  }

  unfollowTag(tag: Tag) {
    return this.get(new HttpParams().set("idTag", tag.id), "tag/unfollow");
  }

  getAssoMailTag(tag: Tag) {
    return this.get(new HttpParams().set("idTag", tag.id), "tag/follow/get");
  }

  getFollowedTagByMail() {
    return this.getList(new HttpParams(), "preferences/followed/tag");
  }
}
