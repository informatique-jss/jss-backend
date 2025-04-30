import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Category } from '../../../../../../client/src/app/modules/miscellaneous/model/Category';
import { AppRestService } from '../../../libs/appRest.service';
import { Media } from '../model/Media';
import { MyJssCategory } from '../model/MyJssCategory';
import { Post } from '../model/Post';
import { Tag } from '../model/Tag';

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

  getFirstPostsByMyJssCategory(searchText: string, myJssCategory: MyJssCategory | undefined) {
    let httpParams = new HttpParams();
    if (searchText)
      httpParams = httpParams.set("searchText", searchText);
    if (myJssCategory && myJssCategory.id)
      httpParams = httpParams.set("myJssCategoryId", myJssCategory.id);
    return this.getList(httpParams, "posts/first-myjss-category");
  }

  searchPostsByMyJssCategory(searchText: string, myJssCategory: MyJssCategory, page: number, size: number) {
    let httpParams = new HttpParams();
    if (searchText)
      httpParams = httpParams.set("searchText", searchText);
    if (myJssCategory && myJssCategory.id)
      httpParams = httpParams.set("myJssCategoryId", myJssCategory.id);

    httpParams.set("page", page).set("size", size);
    return this.getPagedList(httpParams, "search/myjss-category");
  }

  searchMyJssPostsByCategory(searchText: string, category: Category, page: number, size: number) {
    let httpParams = new HttpParams();
    if (searchText)
      httpParams = httpParams.set("searchText", searchText);

    httpParams = httpParams.set("categoryId", category.id);
    httpParams.set("page", page).set("size", size);
    return this.getPagedList(httpParams, "search/posts/category");
  }

  getPostsByMyJssCategory(myJssCategory: MyJssCategory, page: number, size: number) {
    return this.getPagedList(new HttpParams().set("myJssCategoryId", myJssCategory.id ? myJssCategory.id : "null").set("page", page).set("size", size), "posts/myjss-category");
  }

  getTopPostByMyJssCategory(page: number, myJssCategory: MyJssCategory) {
    return this.getPagedList(new HttpParams().set("page", page).set("myJssCategoryId", myJssCategory.id ? myJssCategory.id : "null"), "posts/top/myjss-category");
  }

  getTopPostByTag(page: number, tag: Tag) {
    return this.getList(new HttpParams().set("page", page).set("tagId", tag.id), "posts/top/tag");
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
