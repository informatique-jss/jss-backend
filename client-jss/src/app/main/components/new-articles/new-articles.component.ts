import { Component, OnInit } from '@angular/core';
import { getTimeReading } from '../../../libs/FormatHelper';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { JssCategory } from '../../model/JssCategory';
import { Post } from '../../model/Post';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'new-articles',
  templateUrl: './new-articles.component.html',
  styleUrls: ['./new-articles.component.css']
})
export class NewArticlesComponent implements OnInit {

  posts: Post[] = [];

  constructor(
    private postService: PostService,
    private appService: AppService,
  ) { }

  ngOnInit() {
    this.postService.getTopPost(0).subscribe(posts => {
      this.posts = posts;
    })
  }

  getTimeReading = getTimeReading;

  openPost(post: Post, event: any) {
    this.appService.openRoute(event, "post/" + post.slug, undefined);
  }

  openAuthorPosts(author: Author, event: any) {
    this.appService.openRoute(event, "author/" + author.slug, undefined);
  }

  openCategoryPosts(category: JssCategory, event: any) {
    this.appService.openRoute(event, "category/" + category.slug, undefined);
  }

}
