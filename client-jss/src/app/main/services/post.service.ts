import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../services/appRest.service';
import { Author } from '../model/Author';
import { Media } from '../model/Media';
import { MyJssCategory } from '../model/MyJssCategory';
import { Post } from '../model/Post';
import { PublishingDepartment } from '../model/PublishingDepartment';
import { Tag } from '../model/Tag';

@Injectable({
  providedIn: 'root'
})
export class PostService extends AppRestService<Post> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getTopPost(page: number) {
    return this.getList(new HttpParams().set("page", page), "posts/top");
  }

  getPostsTendency() {
    return this.getList(new HttpParams(), "posts/tendency");
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

  getTopPostByMyJssCategory(page: number, myJssCategory: MyJssCategory) {
    return this.getList(new HttpParams().set("page", page).set("categoryId", myJssCategory.id), "posts/top/myjss-category");
  }

  getTopPostByTag(page: number, tag: Tag) {
    return this.getList(new HttpParams().set("page", page).set("tagId", tag.id), "posts/top/tag");
  }

  getTopPostByDepartment(page: number, department: PublishingDepartment) {
    return this.getList(new HttpParams().set("page", page).set("departmentId", department.id), "posts/top/department");
  }

  getTopPostByAuthor(page: number, author: Author) {
    return this.getList(new HttpParams().set("page", page).set("authorId", author.id), "posts/top/author");
  }

  getTopPostInterview(page: number) {
    return this.getList(new HttpParams().set("page", page), "posts/top/interview");
  }

  getTopPostPodcast(page: number) {
    return this.getList(new HttpParams().set("page", page), "posts/top/podcast");
  }

  getNextArticle(post: Post) {
    return this.get(new HttpParams().set("idPost", post.id), "post/next");
  }

  getPreviousArticle(post: Post) {
    return this.get(new HttpParams().set("idPost", post.id), "post/previous");
  }

}
