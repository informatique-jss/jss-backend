import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from '../../services/appRest.service';
import { Author } from '../model/Author';
import { JssCategory } from '../model/JssCategory';
import { Media } from '../model/Media';
import { PagedContent } from '../model/PagedContent';
import { Post } from '../model/Post';
import { PublishingDepartment } from '../model/PublishingDepartment';
import { Tag } from '../model/Tag';


@Injectable({
  providedIn: 'root'
})
export class PostService extends AppRestService<Post> {

  private readonly defaultImage = '/assets/images/blog-img.jpg';
  private readonly authorImage = '/assets/images/author-img.jpg';

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getTopPost(page: number, pageSize: number) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", pageSize), "posts/jss/top");
  }

  getPostsTendency() {
    return this.getList(new HttpParams(), "posts/jss/tendency");
  }

  getPostBySlug(slug: string) {
    return this.get(new HttpParams().set("slug", slug), "posts/slug");
  }

  getPostSerieBySlug(slug: string) {
    return this.getList(new HttpParams().set("slug", slug), "posts/serie/slug");
  }

  completeMediaInPosts(posts: Post[]) {
    posts.forEach(post => {
      if (!post.media) {
        post.media = {} as Media;
        post.media.id = 0;
        post.media.date = new Date();
        post.media.media_type = 'image';
        post.media.alt_text = 'Logo JSS';
        post.media.file = 'JSS-logo.png';
        post.media.urlFull = 'assets/images/logo.png';
        post.media.urlLarge = 'assets/images/logo.png';
        post.media.urlMedium = 'assets/images/logo.png';
        post.media.urlMediumLarge = 'assets/images/logo.png';
        post.media.urlThumbnail = 'assets/images/logo.png';
        post.media.length = 0;
      }
    });
  }

  getTopPostByJssCategory(page: number, size: number, jssCategory: JssCategory) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", size).set("categoryId", jssCategory.id), "posts/top/jss-category");
  }

  getAllPostsByJssCategory(jssCategory: JssCategory, page: number, size: number, searchText: string): Observable<PagedContent<Post>> {
    let params = new HttpParams()
      .set('categoryId', jssCategory.id.toString())
      .set('page', page.toString())
      .set('size', size.toString());
    if (searchText)
      params = params.set('searchText', searchText);
    return this.getPagedList(params, "posts/all/jss-category", "", "");
  }

  getAllPostsByTag(tag: Tag, page: number, size: number, searchText: string): Observable<PagedContent<Post>> {
    let params = new HttpParams()
      .set('tagSlug', tag.slug)
      .set('page', page.toString())
      .set('size', size.toString());
    if (searchText)
      params = params.set('searchText', searchText);
    return this.getPagedList(params, "posts/all/tag", "", "");
  }

  getMostSeenPostByTag(tag: Tag, page: number, size: number): Observable<PagedContent<Post>> {
    let params = new HttpParams()
      .set('tagSlug', tag.slug)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.getPagedList(params, "posts/tag/most-seen", "", "");
  }

  getMostSeenPostByJssCategory(jssCategory: JssCategory, page: number, size: number): Observable<PagedContent<Post>> {
    let params = new HttpParams()
      .set('jssCategoryId', jssCategory.id.toString())
      .set('page', page.toString())
      .set('size', size.toString());
    return this.getPagedList(params, "posts/jss-category/most-seen", "", "");
  }

  getTopPostByTag(page: number, tag: Tag) {
    return this.getList(new HttpParams().set("page", page).set("tagId", tag.id), "posts/top/tag");
  }

  getIleDeFranceTopPost(page: number, size: number) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", size), "posts/top/department/all");
  }

  getTopPostByDepartment(page: number, size: number, department: PublishingDepartment) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", size).set("departmentId", department.id), "posts/top/department");
  }

  getTopPostByAuthor(page: number, author: Author) {
    return this.getList(new HttpParams().set("page", page).set("authorId", author.id), "posts/top/author");
  }

  getTopPostInterview(page: number) {
    return this.getList(new HttpParams().set("page", page), "posts/top/interview");
  }

  getTopPostPodcast(page: number, size: number) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", size), "posts/top/podcast");
  }

  getMostViewedPosts(page: number, size: number) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", size), "posts/most-seen");
  }

  getPinnedPosts(page: number, size: number) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", size), "posts/pinned");
  }

  getNextArticle(post: Post) {
    return this.get(new HttpParams().set("idPost", post.id), "post/next");
  }

  getPreviousArticle(post: Post) {
    return this.get(new HttpParams().set("idPost", post.id), "post/previous");
  }
}
