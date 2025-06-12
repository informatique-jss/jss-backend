import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../main/services/appRest.service";
import { Author } from "../model/Author";


@Injectable({
  providedIn: 'root'
})
export class AuthorService extends AppRestService<Author> {

  constructor(http: HttpClient) {
    super(http, "crm");
  }
  getFollowedAuthorForCurrentUser() {
    return this.getList(new HttpParams(), "preferences/followed/author");
  }
}
