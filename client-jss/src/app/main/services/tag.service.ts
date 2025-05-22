import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../services/appRest.service';
import { Author } from '../model/Author';
import { JssCategory } from '../model/JssCategory';
import { PublishingDepartment } from '../model/PublishingDepartment';
import { Serie } from '../model/Serie';
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

  getAllTagsByTag(tag: Tag) {
    return this.getList(new HttpParams().set("tagSlug", tag.slug), "tags/all/tag");
  }

  getAllTagsByAuthor(author: Author) {
    return this.getList(new HttpParams().set("authorSlug", author.slug), "tags/all/author");
  }

  getAllTagsBySerie(serie: Serie) {
    return this.getList(new HttpParams().set("serieSlug", serie.slug), "tags/all/serie");
  }

  getAllTagsByPublishingDepartment(department: PublishingDepartment) {
    return this.getList(new HttpParams().set("departmentId", department.id), "tags/all/publishing-department");
  }

  getAllTendencyTags() {
    return this.getList(new HttpParams(), "tags/tendency");
  }
}
