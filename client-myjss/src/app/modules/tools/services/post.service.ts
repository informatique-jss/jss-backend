import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { Category } from '../model/Category';
import { Media } from '../model/Media';
import { MyJssCategory } from '../model/MyJssCategory';
import { Post } from '../model/Post';

@Injectable({
  providedIn: 'root'
})
export class PostService extends AppRestService<Post> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
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

  getBookmarkPostsByMailAndReadingFolders(idReadingFolder: number, page: number, size: number) {
    return this.getPagedList(new HttpParams().set("idReadingFolder", idReadingFolder).set("page", page).set("size", size), "post/bookmark/all");
  }

  addBookmarkPost(post: Post, idReadingFolder: number): Observable<boolean> {
    return this.get(new HttpParams().set("idPost", post.id).set("idReadingFolder", idReadingFolder), "post/bookmark/add") as any as Observable<boolean>;
  }

  deleteBookmarkPost(post: Post) {
    return this.get(new HttpParams().set("idPost", post.id), "post/bookmark/delete") as any as Observable<boolean>;
  }

  getFirstPostsByMyJssCategory(searchText: string, myJssCategory: MyJssCategory | undefined) {
    let httpParams = new HttpParams();
    if (searchText)
      httpParams = httpParams.set("searchText", searchText);
    if (myJssCategory && myJssCategory.id)
      httpParams = httpParams.set("myJssCategoryId", myJssCategory.id);
    return this.getList(httpParams, "posts/first-myjss-category");
  }

  searchPostsByMyJssCategory(searchText: string, myJssCategory: MyJssCategory | undefined, page: number, size: number) {
    let httpParams = new HttpParams();
    if (searchText)
      httpParams = httpParams.set("searchText", searchText);
    if (myJssCategory && myJssCategory.id)
      httpParams = httpParams.set("myJssCategoryId", myJssCategory.id);

    httpParams = httpParams.set("page", page).set("size", size);
    return this.getPagedList(httpParams, "search/myjss-category");
  }

  searchMyJssPostsByCategory(searchText: string, category: Category, page: number, size: number) {
    let httpParams = new HttpParams();
    if (searchText)
      httpParams = httpParams.set("searchText", searchText);

    httpParams = httpParams.set("categoryId", category.id).set("page", page).set("size", size);
    return this.getPagedList(httpParams, "search/posts/category");
  }

  searchPostsByMyJssCategoryAndCategory(searchText: string, myJssCategory: MyJssCategory | undefined, category: Category | undefined, page: number, size: number) {
    let httpParams = new HttpParams();
    if (searchText)
      httpParams = httpParams.set("searchText", searchText);
    if (myJssCategory && myJssCategory.id)
      httpParams = httpParams.set("myJssCategoryId", myJssCategory.id);
    if (category && category.id)
      httpParams = httpParams.set("categoryId", category.id);

    httpParams = httpParams.set("page", page).set("size", size);
    return this.getPagedList(httpParams, "search/categories");
  }

  getPostsByMyJssCategory(myJssCategory: MyJssCategory, page: number, size: number) {
    return this.getPagedList(new HttpParams().set("myJssCategoryId", myJssCategory.id ? myJssCategory.id : "null").set("page", page).set("size", size), "posts/myjss-category");
  }

  getTopPostByMyJssCategory(page: number, myJssCategory: MyJssCategory) {
    return this.getPagedList(new HttpParams().set("page", page).set("myJssCategoryId", myJssCategory.id ? myJssCategory.id : "null"), "posts/top/myjss-category");
  }

  getPinnedPostByMyJssCategory(page: number, myJssCategory: MyJssCategory) {
    return this.getPagedList(new HttpParams().set("page", page).set("myJssCategoryId", myJssCategory.id!), "posts/myjss/pinned");
  }

  getNextArticle(post: Post) {
    return this.get(new HttpParams().set("idPost", post.id), "post/next");
  }

  getPreviousArticle(post: Post) {
    return this.get(new HttpParams().set("idPost", post.id), "post/previous");
  }

  getTopPosts(page: number) {
    return this.getList(new HttpParams().set("page", page), "posts/myjss/top");
  }

  getTendencyPosts() {
    return this.getList(new HttpParams(), "posts/myjss/tendency");
  }
  getMostSeenPosts() {
    return this.getList(new HttpParams(), "posts/myjss/most-seen");
  }

}
