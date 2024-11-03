import { AfterViewInit, Component, OnInit, QueryList, ViewChildren } from '@angular/core';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { MyJssCategory } from '../../model/MyJssCategory';
import { Post } from '../../model/Post';
import { Serie } from '../../model/Serie';
import { MyJssCategoryService } from '../../services/myjss.category.service';
import { PostService } from '../../services/post.service';
import { SerieService } from '../../services/serie.service';

declare var tns: any;

@Component({
  selector: 'body-articles',
  templateUrl: './body-articles.component.html',
  styleUrls: ['./body-articles.component.css']
})
export class BodyArticlesComponent implements OnInit, AfterViewInit {

  posts: Post[] = [];
  interviews: Post[] = [];
  podcasts: Post[] = [];
  departmentPosts: Post[] = [];
  page: number = 0;
  categories: MyJssCategory[] = [];
  series: Serie[] = [];

  @ViewChildren('sliderInterviewPage') sliderInterviewPage!: QueryList<any>;
  @ViewChildren('sliderSeriesPage') sliderSeriesPage!: QueryList<any>;

  constructor(
    private postService: PostService,
    private myJssCategoryService: MyJssCategoryService,
    private serieService: SerieService,
    private appService: AppService,
  ) { }

  ngOnInit() {
    this.fetchNextPosts();
    this.myJssCategoryService.getAvailableMyJssCategories().subscribe(categories => {
      if (categories && categories.length > 0)
        this.categories.push(...categories.sort((a: MyJssCategory, b: MyJssCategory) => a.count - b.count));
    });
    this.serieService.getAvailableSeries().subscribe(series => {
      this.series.push(...series.sort((a: Serie, b: Serie) => b.serieOrder - a.serieOrder));
    })
    this.postService.getTopPostInterview(0).subscribe(interviews => {
      if (interviews && interviews.length > 0)
        this.interviews = interviews;
    })
    this.postService.getTopPostPodcast(0).subscribe(podcasts => {
      if (podcasts && podcasts.length > 0)
        this.podcasts = podcasts;
      const event = new Event("RefreshThemeFunctions");
      //if (document)
      // document.dispatchEvent(event);
    })
  }

  getNextPosts() {
    this.page++;
    this.fetchNextPosts();
  }

  fetchNextPosts() {
    this.postService.getTopPost(this.page).subscribe(posts => {
      if (posts && posts.length > 0) {
        this.posts.push(...posts);

        // Load department posts until 5 posts
        if (this.departmentPosts.length < 5)
          for (let departmentPost of this.posts) {
            if (departmentPost.departments != null && this.departmentPosts.length < 5
              && departmentPost.departments[0] && !isNaN(departmentPost.departments[0].code as any) && parseInt(departmentPost.departments[0].code) > 0) {
              this.departmentPosts.push(departmentPost);
            }
          }
      }
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

  openPodcastPosts(event: any) {
    this.appService.openRoute(event, "podcasts", undefined);
  }

  openInterviewPosts(event: any) {
    this.appService.openRoute(event, "interviews", undefined);
  }

  openSeriesPosts(event: any) {
    this.appService.openRoute(event, "series", undefined);
  }

  openSeriePosts(serie: Serie, event: any) {
    this.appService.openRoute(event, "serie/" + serie.slug, undefined);
  }

  openPodcastPost(podcast: Post, event: any) {
    this.appService.openRoute(event, "podcast/" + podcast.slug, undefined);
  }

  ngAfterViewInit() {
    this.sliderInterviewPage.changes.subscribe(t => {
      tns({
        container: '.slider-interview',
        controlsText: ['<i class="fas fa-chevron-left"></i>', '<i class="fas fa-chevron-right"></i>'],
        items: 3,
        nav: false,
        autoplayHoverPause: true,
        autoplayButtonOutput: false,
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
    this.sliderSeriesPage.changes.subscribe(t => {
      tns({
        container: '.slider-series',
        controlsText: ['<i class="fas fa-chevron-left"></i>', '<i class="fas fa-chevron-right"></i>'],
        items: 3,
        nav: false,
        autoplayHoverPause: true,
        autoplayButtonOutput: false,
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
}
