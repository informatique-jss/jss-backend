import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../services/appRest.service";
import { AssoMailAuthor } from '../model/AssoMailAuthor';
import { Author } from "../model/Author";

@Injectable({
  providedIn: 'root'
})
export class AssoMailAuthorService extends AppRestService<AssoMailAuthor> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  followAuthor(author: Author) {
    return this.get(new HttpParams().set("idAuthor", author.id), "author/follow/add");
  }

  unfollowAuthor(author: Author) {
    return this.get(new HttpParams().set("idAuthor", author.id), "author/unfollow");
  }

  getAssoMailAuthor(author: Author) {
    return this.get(new HttpParams().set("idAuthor", author.id), "author/follow/get");
  }

}
