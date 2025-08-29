import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from '../../services/appRest.service';
import { Author } from '../model/Author';
import { Category } from '../model/Category';
import { JssCategory } from '../model/JssCategory';
import { Media } from '../model/Media';
import { PagedContent } from '../model/PagedContent';
import { Post } from '../model/Post';
import { PublishingDepartment } from '../model/PublishingDepartment';
import { ReadingFolder } from '../model/ReadingFolder';
import { Serie } from '../model/Serie';
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

  getLastPosts(page: number, pageSize: number, searchText: string) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", pageSize).set("searchText", searchText), "posts/jss/last");
  }

  getPostsTendency(page: number, size: number, searchText: string) {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (searchText)
      params = params.set('searchText', searchText);
    return this.getPagedList(params, "posts/jss/tendency");
  }

  getPostBySlug(slug: string) {
    return this.get(new HttpParams().set("slug", slug), "posts/slug");
  }

  getOfferedPostByToken(validationToken: string, mail: string) {
    return this.get(new HttpParams().set("validationToken", validationToken).set("mail", mail), "posts/slug/token");
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

  addBookmarkPost(post: Post, readingFolder?: ReadingFolder) {
    let params = new HttpParams()
      .set("idPost", post.id)
    if (readingFolder)
      params = params.set('idReadingFolder', readingFolder.id);
    return this.get(params, "post/bookmark/add");
  }

  deleteBookmarkPost(post: Post) {
    return this.get(new HttpParams().set("idPost", post.id), "post/bookmark/delete");
  }

  getTopPostByJssCategory(page: number, size: number, jssCategory: JssCategory) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", size).set("categoryId", jssCategory.id), "posts/top/jss-category");
  }

  getAllPostsByJssCategory(jssCategory: JssCategory, page: number, size: number, searchText: string, isDisplayNewPosts: boolean): Observable<PagedContent<Post>> {
    let params = new HttpParams()
      .set('categoryId', jssCategory.id.toString())
      .set('page', page.toString())
      .set('size', size.toString())
      .set('isDisplayNewPosts', isDisplayNewPosts);
    if (searchText)
      params = params.set('searchText', searchText);
    return this.getPagedList(params, "posts/all/jss-category", "", "");
  }

  getAllPostsByCategory(category: Category, page: number, size: number): Observable<PagedContent<Post>> {
    let params = new HttpParams()
      .set('categoryId', category.id.toString())
      .set('page', page.toString())
      .set('size', size.toString());
    return this.getPagedList(params, "posts/all/category", "", "");
  }

  getAllPostsByTag(tag: Tag, page: number, size: number, searchText: string, isDisplayNewPosts: boolean): Observable<PagedContent<Post>> {
    let params = new HttpParams()
      .set('tagSlug', tag.slug)
      .set('page', page.toString())
      .set('size', size.toString())
      .set('isDisplayNewPosts', isDisplayNewPosts);
    if (searchText)
      params = params.set('searchText', searchText);
    return this.getPagedList(params, "posts/all/tag", "", "");
  }

  getAllPostsByAuthor(author: Author, page: number, size: number, searchText: string, isDisplayNewPosts: boolean): Observable<PagedContent<Post>> {
    let params = new HttpParams()
      .set('authorSlug', author.slug)
      .set('page', page.toString())
      .set('size', size.toString())
      .set('isDisplayNewPosts', isDisplayNewPosts);
    if (searchText)
      params = params.set('searchText', searchText);
    return this.getPagedList(params, "posts/all/author", "", "");
  }

  getAllPostsBySerie(serie: Serie, page: number, size: number, searchText: string): Observable<PagedContent<Post>> {
    let params = new HttpParams()
      .set('serieSlug', serie.slug)
      .set('page', page.toString())
      .set('size', size.toString());
    if (searchText)
      params = params.set('searchText', searchText);
    return this.getPagedList(params, "posts/all/serie", "", "");
  }

  getAllPostsByPublishingDepartment(department: PublishingDepartment, page: number, size: number, searchText: string): Observable<PagedContent<Post>> {
    let params = new HttpParams()
      .set('departmentCode', department.code)
      .set('page', page.toString())
      .set('size', size.toString());
    if (searchText)
      params = params.set('searchText', searchText);
    return this.getPagedList(params, "posts/all/publishing-department", "", "");
  }

  getMostSeenPostByJssCategory(jssCategory: JssCategory, page: number, size: number): Observable<PagedContent<Post>> {
    let params = new HttpParams()
      .set('jssCategoryId', jssCategory.id.toString())
      .set('page', page.toString())
      .set('size', size.toString());
    return this.getPagedList(params, "posts/jss-category/most-seen", "", "");
  }

  getMostSeenPostByTag(tag: Tag, page: number, size: number): Observable<PagedContent<Post>> {
    let params = new HttpParams()
      .set('tagSlug', tag.slug)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.getPagedList(params, "posts/tag/most-seen", "", "");
  }

  getMostSeenPostByAuthor(author: Author, page: number, size: number): Observable<PagedContent<Post>> {
    let params = new HttpParams()
      .set('authorSlug', author.slug)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.getPagedList(params, "posts/author/most-seen", "", "");
  }

  getMostSeenPostBySerie(serie: Serie, page: number, size: number): Observable<PagedContent<Post>> {
    let params = new HttpParams()
      .set('serieSlug', serie.slug)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.getPagedList(params, "posts/serie/most-seen", "", "");
  }

  getMostSeenPostByPublishingDepartment(department: PublishingDepartment, page: number, size: number): Observable<PagedContent<Post>> {
    let params = new HttpParams()
      .set('departmentCode', department.code)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.getPagedList(params, "posts/publishing-department/most-seen", "", "");
  }

  getMostSeenPosts(page: number, size: number, searchText: string) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", size).set("searchText", searchText), "posts/most-seen");
  }

  getIleDeFranceTopPost(page: number, size: number) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", size), "posts/top/department/all");
  }

  getTopPostByDepartment(page: number, size: number, department: PublishingDepartment) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", size).set("departmentCode", department.code), "posts/top/department");
  }

  getTopPostPodcast(page: number, size: number) {
    return this.getPagedList(new HttpParams().set("page", page).set("size", size), "posts/top/podcast");
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

  getPostById(idPost: number) {
    return this.get(new HttpParams().set("idPost", idPost), "post/get");
  }
}
