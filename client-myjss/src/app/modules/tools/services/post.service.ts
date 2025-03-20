import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { Author } from '../model/Author';
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

  getFirstPostsByMyJssCategory(searchText: string, myJssCategory: MyJssCategory) {
    let httpParams = new HttpParams();
    if (searchText)
      httpParams = httpParams.set("searchText", searchText);
    if (myJssCategory && myJssCategory.id)
      httpParams = httpParams.set("myJssCategoryId", myJssCategory.id);
    return this.getList(httpParams, "posts/first-myjss-category");
  }

  searchPostsByMyJssCategory(searchText: string, myJssCategory: MyJssCategory) {
    let httpParams = new HttpParams();
    if (searchText)
      httpParams = httpParams.set("searchText", searchText);
    if (myJssCategory && myJssCategory.id)
      httpParams = httpParams.set("myJssCategoryId", myJssCategory.id);
    return this.getList(httpParams, "search/myjss-category");
  }

  getPostsByMyJssCategory(searchText: string, myJssCategory: MyJssCategory) {
    if (searchText != undefined && searchText != null && searchText.length > 2)
      return this.getList(new HttpParams().set("myJssCategoryId", myJssCategory.id).set("searchText", searchText), "posts/myjss-category");
    return this.getList(new HttpParams().set("myJssCategoryId", myJssCategory.id), "posts/myjss-category");
  }

  getTopPostByTag(page: number, tag: Tag) {
    return this.getList(new HttpParams().set("page", page).set("tagId", tag.id), "posts/top/tag");
  }


  getTopPostByAuthor(page: number, author: Author) {
    return this.getList(new HttpParams().set("page", page).set("authorId", author.id), "posts/top/author");
  }

}
