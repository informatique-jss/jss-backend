import { AfterViewInit, Component, OnInit, QueryList, ViewChildren } from '@angular/core';
import { AppService } from '../../../services/app.service';
import { Post } from '../../model/Post';
import { PostService } from '../../services/post.service';

declare var tns: any;

@Component({
  selector: 'trend',
  templateUrl: './trend.component.html',
  styleUrls: ['./trend.component.css']
})
export class TrendComponent implements OnInit, AfterViewInit {

  posts: Post[] | undefined;
  @ViewChildren('sliderPage') sliderPage!: QueryList<any>;

  constructor(
    private postService: PostService,
    private appService: AppService
  ) { }

  ngOnInit() {
    this.postService.getPostsTendency().subscribe(response => {
      this.posts = response;

    });
  }

  getRawText(text: string): string {
    return text.replace(/<[^>]*>/g, '');
  }

  openPost(post: Post, event: any) {
    this.appService.openRoute(event, "post/" + post.slug, undefined);
  }

  ngAfterViewInit() {
    this.sliderPage.changes.subscribe(t => {
      tns({
        container: '.slider-trend',
        controlsText: [
          '<i class="fas fa-chevron-left"></i>',
          '<i class="fas fa-chevron-right"></i>'
        ],
        items: 1,
        nav: false,
        autoplayHoverPause: true,
        autoplayButtonOutput: false,
        autoplay: true,
        gutter: 0,
        controls: true,
      });

    })


  }
}

