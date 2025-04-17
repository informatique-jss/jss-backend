import { Component, OnInit } from '@angular/core';
import { CommunicationPreferenceService } from '../../../../../../client/src/app/modules/crm/services/communication.preference.service';
import { validateEmail } from '../../../libs/CustomFormsValidatorsHelper';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { JssCategory } from '../../model/JssCategory';
import { Post } from '../../model/Post';
import { Serie } from '../../model/Serie';
import { Tag } from '../../model/Tag';
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
  pinedPosts: Post[] = []; // TODO later
  ileDeFrancePosts: Post[] = [];
  mostViewedPosts: Post[] = [];
  justiceTopPosts: Post[] = [];
  lawTopPosts: Post[] = [];
  economyTopPosts: Post[] = [];
  podcasts: Post[] = []; // TODO
  categories: JssCategory[] = [];
  series: Serie[] = [];
  tagTendencies: Tag[] = [];

  mail: string = '';

  constructor(
    private postService: PostService,
    private jssCategoryService: JssCategoryService,
    private serieService: SerieService,
    private appService: AppService,
    private communicationPreferenceService: CommunicationPreferenceService
  ) { }

  ngOnInit() {
    // Fetch top posts
    this.postService.getTopPost(0, 10).subscribe(pagedPosts => {
      if (pagedPosts.content && pagedPosts.content.length > 0) {
        this.lastPosts.push(...pagedPosts.content);
      }
    })

    //Fetch Ile de France posts
    this.postService.getIleDeFranceTopPost(0, 8).subscribe(pagedPosts => {
      if (pagedPosts.content && pagedPosts.content.length > 0) {
        this.ileDeFrancePosts.push(...pagedPosts.content);
      }
    })

    //Fetch most viewed posts
    this.postService.getMostViewedPosts(0, 5).subscribe(pagedPosts => {
      if (pagedPosts.content && pagedPosts.content.length > 0) {
        this.mostViewedPosts.push(...pagedPosts.content);
      }
    })

    // Fetch series
    this.serieService.getAvailableSeries().subscribe(series => {
      this.series.push(...series.sort((a: Serie, b: Serie) => b.serieOrder - a.serieOrder));
    })

    // Fetch categories
    this.jssCategoryService.getAvailableJssCategories().subscribe(categories => {
      if (categories && categories.length > 0) {
        this.categories.push(...categories.sort((a: JssCategory, b: JssCategory) => a.count - b.count));
        this.fillPostsForCategories();
      }
    });

    // TODO : to delete and fill as expected :
    this.pinedPosts = this.lastPosts;

    // TODO
    this.postService.getTopPostPodcast(0, 3).subscribe(pagedPodcasts => {
      if (pagedPodcasts.content && pagedPodcasts.content.length > 0)
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
    this.appService.openRoute(event, "serie/" + serie.slug, undefined);
  }

  openPodcastPost(podcast: Post, event: any) {
    this.appService.openRoute(event, "podcast/" + podcast.slug, undefined);
  }

  openPinedPosts(event: any) {
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

    this.communicationPreferenceService.subscribeToCorporateNewsletter(mailToRegister).subscribe();
  }

  getCategoryByName(name: string): JssCategory | undefined {
    return this.categories.find(c => c.name.toLowerCase() === name.toLowerCase());
  }

  fillPostsForCategories() {
    if (this.getCategoryByName("justice")) {
      this.postService.getTopPostByJssCategory(0, 3, this.getCategoryByName("justice")!).subscribe(pagedPosts => {
        if (pagedPosts.content && pagedPosts.content.length > 0)
          this.justiceTopPosts = pagedPosts.content;
      })
    }
    if (this.getCategoryByName("droit")) {
      this.postService.getTopPostByJssCategory(0, 3, this.getCategoryByName("droit")!).subscribe(pagedPosts => {
        if (pagedPosts.content && pagedPosts.content.length > 0)
          this.lawTopPosts = pagedPosts.content;
      })
    }
    if (this.getCategoryByName("économie")) {
      this.postService.getTopPostByJssCategory(0, 3, this.getCategoryByName("économie")!).subscribe(pagedPosts => {
        if (pagedPosts.content && pagedPosts.content.length > 0)
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