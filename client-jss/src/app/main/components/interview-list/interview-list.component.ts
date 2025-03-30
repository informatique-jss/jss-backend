import { Component, OnInit } from '@angular/core';
import { getTimeReading } from '../../../libs/FormatHelper';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { JssCategory } from '../../model/JssCategory';
import { Post } from '../../model/Post';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'app-interview-list',
  templateUrl: './interview-list.component.html',
  styleUrls: ['./interview-list.component.css']
})
export class InterviewListComponent implements OnInit {

  page: number = 0;
  posts: Post[] = [];
  code: string = "";
  displayLoadMoreButton: boolean = true;

  getTimeReading = getTimeReading;

  constructor(
    private postService: PostService,
    private appService: AppService
  ) { }

  ngOnInit() {
    this.fetchNextPosts();
  }

  getNextPosts() {
    this.page++;
    this.fetchNextPosts();
  }

  openCategoryPosts(category: JssCategory, event: any) {
    this.appService.openRoute(event, "category/" + category.slug, undefined);
  }

  openPost(post: Post, event: any) {
    this.appService.openRoute(event, "post/" + post.slug, undefined);
  }

  openAuthorPosts(author: Author, event: any) {
    this.appService.openRoute(event, "author/" + author.slug, undefined);
  }

  fetchNextPosts() {
    this.postService.getTopPostInterview(this.page).subscribe(posts => {
      if (posts && posts.length > 0) {
        this.posts.push(...posts);
      } else {
        this.displayLoadMoreButton = false;
      }
    });
  }
}
