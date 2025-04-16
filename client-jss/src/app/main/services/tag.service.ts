import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../services/appRest.service';
import { JssCategory } from '../model/JssCategory';
import { Tag } from '../model/Tag';

@Injectable({
  providedIn: 'root'
})
export class TagService extends AppRestService<Tag> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getAvailableTags() {
    return this.getListCached(new HttpParams(), "tags");
  }

  getTagBySlug(slug: string) {
    return this.get(new HttpParams().set("slug", slug), "tag/slug");
  }

  getAllTagsByJssCategory(jssCategory: JssCategory) {
    return this.getList(new HttpParams().set("jssCategoryId", jssCategory.id), "tags/all/jss-category");
  }

}
