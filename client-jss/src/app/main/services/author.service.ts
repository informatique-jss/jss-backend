import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../services/appRest.service';
import { Author } from '../model/Author';

@Injectable({
  providedIn: 'root'
})
export class AuthorService extends AppRestService<Author> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getAuthorBySlug(slug: string) {
    return this.get(new HttpParams().set("slug", slug), "author/slug");
  }

}
