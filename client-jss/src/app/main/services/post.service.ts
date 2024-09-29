import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../services/appRest.service';
import { MyJssCategory } from '../model/MyJssCategory';
import { Post } from '../model/Post';

@Injectable({
  providedIn: 'root'
})
export class PostService extends AppRestService<Post>{

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getTopPost(page: number) {
    return this.getListCached(new HttpParams().set("page", page), "posts/top");
  }

  getTopPostByMyJssCategory(page: number, myJssCategory: MyJssCategory) {
    return this.getListCached(new HttpParams().set("page", page).set("categoryId", myJssCategory.id), "posts/top/myjss-category");
  }

  getTopPostInterview(page: number) {
    return this.getListCached(new HttpParams().set("page", page), "posts/top/interview");
  }

  getTopPostPodcast(page: number) {
    return this.getListCached(new HttpParams().set("page", page), "posts/top/podcast");
  }

}
