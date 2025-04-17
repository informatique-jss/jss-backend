import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { JssCategory } from '../../model/JssCategory';
import { Post } from '../../model/Post';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'app-podcast-list',
  templateUrl: './podcast-list.component.html',
  styleUrls: ['./podcast-list.component.css'],
  standalone: false
})
export class PodcastListComponent implements OnInit {

  podcasts: Post[] = [];
  page: number = 0;
  displayLoadMoreButton: boolean = true;

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

  fetchNextPosts() {
    this.postService.getTopPostPodcast(this.page, 10).subscribe(pagedPosts => {
      if (pagedPosts.content && pagedPosts.content.length > 0) {
        this.podcasts.push(...pagedPosts.content);
      } else {
        this.displayLoadMoreButton = false;
      }
    });
  }

  openPodcast(post: Post, event: any) {
    this.appService.openRoute(event, "podcast/" + post.slug, undefined);
  }

  openAuthorPosts(author: Author, event: any) {
    this.appService.openRoute(event, "author/" + author.slug, undefined);
  }


  openCategoryPosts(category: JssCategory, event: any) {
    this.appService.openRoute(event, "category/" + category.slug, undefined);
  }
}
