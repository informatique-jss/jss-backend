import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { getTimeReading } from '../../../libs/FormatHelper';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { JssCategory } from '../../model/JssCategory';
import { Post } from '../../model/Post';
import { Serie } from '../../model/Serie';
import { PostService } from '../../services/post.service';
import { SerieService } from '../../services/serie.service';

@Component({
    selector: 'app-serie',
    templateUrl: './serie.component.html',
    styleUrls: ['./serie.component.css'],
    standalone: false
})
export class SerieComponent implements OnInit {

  posts: Post[] = [];
  code: string = "";
  slug: string = "";
  serie: Serie | undefined;

  getTimeReading = getTimeReading;

  constructor(
    private postService: PostService,
    private activatedRoute: ActivatedRoute,
    private appService: AppService,
    private serieService: SerieService
  ) { }

  ngOnInit() {
    this.slug = this.activatedRoute.snapshot.params['slug'];
    this.serieService.getSerieBySlug(this.slug).subscribe(response => this.serie = response);
    this.fetchNextPosts();
  }

  getNextPosts() {
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
    this.postService.getPostSerieBySlug(this.slug).subscribe(posts => {
      if (posts && posts.length > 0) {
        this.posts = posts;
        this.postService.completeMediaInPosts(posts);
      }
    });
  }
}
