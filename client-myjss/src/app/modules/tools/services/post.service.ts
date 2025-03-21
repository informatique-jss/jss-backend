import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
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

  getNextArticle(post: Post) {
    return this.get(new HttpParams().set("idPost", post.id), "post/next");
  }

  getPreviousArticle(post: Post) {
    return this.get(new HttpParams().set("idPost", post.id), "post/previous");
  }

  getTopPosts(page: number) {
    return this.getList(new HttpParams().set("page", page), "posts/top");
  }

  getPostsTendency() {
    return this.getList(new HttpParams(), "posts/tendency");
  }

}
