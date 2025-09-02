import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../services/appRest.service';
import { Author } from '../model/Author';
import { Category } from '../model/Category';
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

  getAllTagsByJssCategory(jssCategory: JssCategory, isDisplayNewPosts: boolean) {
    return this.getList(new HttpParams().set("jssCategoryId", jssCategory.id).set("isDisplayNewPosts", isDisplayNewPosts), "tags/all/jss-category");
  }

  getAllTagsByCategory(page: number, size: number, category: Category) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", size).set("categoryId", category.id), "tags/all/category");
  }

  getAllTagsByTag(tag: Tag, isDisplayNewPosts: boolean) {
    return this.getList(new HttpParams().set("tagSlug", tag.slug).set("isDisplayNewPosts", isDisplayNewPosts), "tags/all/tag");
  }

  getAllTagsByAuthor(author: Author, isDisplayNewPosts: boolean) {
    return this.getList(new HttpParams().set("authorSlug", author.slug).set("isDisplayNewPosts", isDisplayNewPosts), "tags/all/author");
  }

  getAllTagsByPremiumPosts() {
    return this.getList(new HttpParams(), "tags/all/premium");
  }

  getAllTagsBySerie(serie: Serie) {
    return this.getList(new HttpParams().set("serieSlug", serie.slug), "tags/all/serie");
  }

  getAllTagsByPublishingDepartment(department: PublishingDepartment) {
    return this.getList(new HttpParams().set("departmentCode", department.code), "tags/all/publishing-department");
  }

  getAllTendencyTags() {
    return this.getList(new HttpParams(), "tags/tendency");
  }

  getAllLastPostsTags() {
    return this.getList(new HttpParams(), "tags/last");
  }

  getAllMostSeenPostsTags() {
    return this.getList(new HttpParams(), "tags/most-seen");
  }
}
