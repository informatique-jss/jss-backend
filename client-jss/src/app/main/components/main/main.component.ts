import { Component, OnInit } from '@angular/core';
import { validateEmail } from '../../../libs/CustomFormsValidatorsHelper';
import { AppService } from '../../../services/app.service';
import { ConstantService } from '../../../services/constant.service';
import { Author } from '../../model/Author';
import { JssCategory } from '../../model/JssCategory';
import { Post } from '../../model/Post';
import { Serie } from '../../model/Serie';
import { Tag } from '../../model/Tag';
import { CommunicationPreferencesService } from '../../services/communication.preference.service';
import { JssCategoryService } from '../../services/jss.category.service';
import { PostService } from '../../services/post.service';
import { SerieService } from '../../services/serie.service';

declare var tns: any;

@Component({
  selector: 'main-page',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
  standalone: false
})
export class MainComponent implements OnInit {

  lastPosts: Post[] = [];
  pinnedPosts: Post[] = [];
  ileDeFrancePosts: Post[] = [];
  mostViewedPosts: Post[] = [];
  justiceTopPosts: Post[] = [];
  lawTopPosts: Post[] = [];
  economyTopPosts: Post[] = [];
  podcasts: Post[] = [];
  categories: JssCategory[] = [];
  series: Serie[] = [];
  tagTendencies: Tag[] = [];

  mail: string = '';

  constantJustice: JssCategory | undefined;
  constantLaw: JssCategory | undefined;
  constantEconomics: JssCategory | undefined;

  constructor(
    private postService: PostService,
    private jssCategoryService: JssCategoryService,
    private serieService: SerieService,
    private appService: AppService,
    private communicationPreferenceService: CommunicationPreferencesService,
    private constantService: ConstantService
  ) { }


  ngOnInit() {
    this.constantJustice = this.constantService.getJssCategoryEconomics();
    this.constantLaw = this.constantService.getJssCategoryEconomics();
    this.constantEconomics = this.constantService.getJssCategoryEconomics();

    // Fetch top posts
    this.postService.getTopPost(0, 10).subscribe(pagedPosts => {
      if (pagedPosts.content) {
        this.lastPosts = pagedPosts.content;
      }
    })

    //Fetch Ile de France posts
    this.postService.getIleDeFranceTopPost(0, 8).subscribe(pagedPosts => {
      if (pagedPosts.content) {
        this.ileDeFrancePosts = pagedPosts.content;
      }
    })

    //Fetch most viewed posts
    this.postService.getMostViewedPosts(0, 5).subscribe(pagedPosts => {
      if (pagedPosts.content) {
        this.mostViewedPosts = pagedPosts.content;
      }
    })

    // Fetch series
    this.serieService.getSeries(0, 6).subscribe(pagedSeries => {
      if (pagedSeries.content) {
        this.series = pagedSeries.content;
      }
    })

    // Fetch categories and posts by category
    this.jssCategoryService.getAvailableJssCategories().subscribe(categories => {
      if (categories) {
        this.categories = categories.sort((a: JssCategory, b: JssCategory) => a.count - b.count);
        this.fillPostsForCategories();
      }
    });

    // Fetch pinned (or sticky) posts
    this.postService.getPinnedPosts(0, 3).subscribe(pagedPosts => {
      if (pagedPosts.content) {
        this.pinnedPosts = pagedPosts.content;
      }
    })

    this.postService.getTopPostPodcast(0, 3).subscribe(pagedPodcasts => {
      if (pagedPodcasts.content)
        this.podcasts = pagedPodcasts.content;
    })
  }

  openPost(post: Post, event: any) {
    this.appService.openRoute(event, "post/" + post.slug, undefined);
  }

  openAuthorPosts(author: Author, event: any) {
    this.appService.openRoute(event, "author/" + author.slug, undefined);
  }

  openCategoryPosts(category: JssCategory, event: any) {
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
    this.appService.openRoute(event, "post/serie/" + serie.slug, undefined);
  }

  openPodcastPost(podcast: Post, event: any) {
    this.appService.openRoute(event, "podcast/" + podcast.slug, undefined);
  }

  openPinnedPosts(event: any) {
    this.appService.openRoute(event, "pined/", undefined);
  }

  registerEmail(mailToRegister: string) {
    // Email verifications
    if (!mailToRegister) {
      // this.appService.displayToast("Merci de renseigner une adresse e-mail.", true, "Une erreur s’est produite...", 3000);
      return;
    }

    if (!validateEmail(mailToRegister)) {
      // this.appService.displayToast("Impossible de finaliser votre inscription. Vérifiez votre adresse e-mail et réessayez.", true, "Une erreur s’est produite...", 3000);
      return;
    }
    this.communicationPreferenceService.subscribeToNewspaperNewsletter(mailToRegister).subscribe();
  }

  fillPostsForCategories() {
    if (this.constantJustice) {
      this.postService.getTopPostByJssCategory(0, 3, this.constantJustice).subscribe(pagedPosts => {
        if (pagedPosts.content)
          this.justiceTopPosts = pagedPosts.content;
      })
    }

    if (this.constantLaw) {
      this.postService.getTopPostByJssCategory(0, 3, this.constantLaw).subscribe(pagedPosts => {
        if (pagedPosts.content)
          this.lawTopPosts = pagedPosts.content;
      })
    }

    if (this.constantEconomics) {
      this.postService.getTopPostByJssCategory(0, 3, this.constantEconomics).subscribe(pagedPosts => {
        if (pagedPosts.content)
          this.economyTopPosts = pagedPosts.content;
      })
    }
  }

  followPost(postToFollow: Post, event: MouseEvent) {
    //TODO
  }

  unfollowPost(postToFollow: Post, event: MouseEvent) {
    //TODO
  }

  followSerie(serieToFollow: Serie, event: MouseEvent) {
    //TODO
  }

  unfollowSerie(serieToFollow: Serie, event: MouseEvent) {
    //TODO
  }
}
