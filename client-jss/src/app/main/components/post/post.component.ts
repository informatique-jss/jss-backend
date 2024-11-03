import { AfterViewInit, Component, OnInit, QueryList, ViewChildren } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { getTimeReading } from '../../../libs/FormatHelper';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { MyJssCategory } from '../../model/MyJssCategory';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';
import { PostService } from '../../services/post.service';

declare var tns: any;

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css']
})
export class PostComponent implements OnInit, AfterViewInit {

  slug: string | undefined;
  post: Post | undefined;

  @ViewChildren('sliderPage') sliderPage!: QueryList<any>;

  constructor(private activatedRoute: ActivatedRoute,
    private postService: PostService,
    private appService: AppService,
  ) { }

  getTimeReading = getTimeReading;

  ngOnInit() {
    this.slug = this.activatedRoute.snapshot.params['slug'];
    if (this.slug)
      this.postService.getPostBySlug(this.slug).subscribe(post => {
        this.post = post;
      })
  }

  ngAfterViewInit() {
    this.sliderPage.changes.subscribe(t => {
      tns({
        container: '.slider',
        items: 3,
        nav: false,
        autoplayHoverPause: true,
        autoplay: true,
        gutter: 24,
        controls: true,
        responsive: {
          1: {
            items: 1
          },
          767: {
            items: 2
          },
          991: {
            items: 3
          },
          1200: {
            items: 4
          }
        }
      });
    })
  }

  openPost(post: Post, event: any) {
    this.appService.openRoute(event, "post/" + post.slug, undefined);
  }

  openAuthorPosts(author: Author, event: any) {
    this.appService.openRoute(event, "author/" + author.slug, undefined);
  }

  openCategoryPosts(category: MyJssCategory, event: any) {
    this.appService.openRoute(event, "category/" + category.slug, undefined);
  }

  openTagPosts(tag: Tag, event: any) {
    this.appService.openRoute(event, "tag/" + tag.slug, undefined);
  }

}
