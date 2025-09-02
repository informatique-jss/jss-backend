import { Component, OnInit } from '@angular/core';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { MY_JSS_HOME_ROUTE } from '../../../libs/Constants';
import { validateEmail } from '../../../libs/CustomFormsValidatorsHelper';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { ConstantService } from '../../../services/constant.service';
import { Author } from '../../model/Author';
import { JssCategory } from '../../model/JssCategory';
import { Post } from '../../model/Post';
import { PublishingDepartment } from '../../model/PublishingDepartment';
import { Responsable } from '../../model/Responsable';
import { Serie } from '../../model/Serie';
import { Tag } from '../../model/Tag';
import { AudioPlayerService } from '../../services/audio.player.service';
import { CommunicationPreferencesService } from '../../services/communication.preference.service';
import { LoginService } from '../../services/login.service';
import { PostService } from '../../services/post.service';
import { SerieService } from '../../services/serie.service';
import { TagService } from '../../services/tag.service';
import { BookmarkComponent } from "../bookmark/bookmark.component";

declare var tns: any;

@Component({
  selector: 'main-page',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, NgbTooltipModule, BookmarkComponent]
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
  series: Serie[] = [];
  tagTendencies: Tag[] = [];
  mail: string = '';

  firstCategory!: JssCategory;
  secondCategory!: JssCategory;
  thirdCategory!: JssCategory;
  idf!: PublishingDepartment;

  currentUser: Responsable | undefined;

  constructor(
    private postService: PostService,
    private serieService: SerieService,
    private loginService: LoginService,
    private appService: AppService,
    private communicationPreferenceService: CommunicationPreferencesService,
    private constantService: ConstantService,
    private tagService: TagService,
    private audioService: AudioPlayerService
  ) { }

  ngOnInit() {
    this.firstCategory = this.constantService.getJssCategoryHomepageFirstHighlighted();
    this.secondCategory = this.constantService.getJssCategoryHomepageSecondHighlighted();
    this.thirdCategory = this.constantService.getJssCategoryHomepageThirdHighlighted();
    this.idf = this.constantService.getPublishingDepartmentIdf();

    this.loginService.getCurrentUser().subscribe(user => {
      if (user)
        this.currentUser = user;
    })
    // Fetch top posts
    this.postService.getLastPosts(0, 10, "").subscribe(pagedPosts => {
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

    // Fetch posts by category
    this.fillPostsForCategories();
    //Fetch most viewed posts
    this.postService.getMostSeenPosts(0, 5, "").subscribe(pagedPosts => {
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

    // Fetch pinned (or sticky) posts
    this.postService.getPinnedPosts(0, 3).subscribe(pagedPosts => {
      if (pagedPosts.content) {
        this.pinnedPosts = pagedPosts.content;
      }
    })

    // Fetch podcasts
    this.postService.getTopPostPodcast(0, 3).subscribe(pagedPodcasts => {
      if (pagedPodcasts.content)
        this.podcasts = pagedPodcasts.content;
    })

    this.tagService.getAllTendencyTags().subscribe(reponse => {
      this.tagTendencies = reponse;
    })
  }

  openPost(post: Post, event: any) {
    this.appService.openRoute(event, "post/" + post.slug, undefined);
  }

  openAuthorPosts(author: Author, event: any) {
    this.appService.openRoute(event, "post/author/" + author.slug, undefined);
  }

  openCategoryPosts(category: JssCategory, event: any) {
    this.appService.openRoute(event, "post/category/" + category.slug, undefined);
  }

  openPodcastPosts(event: any) {
    this.appService.openRoute(event, "podcasts", undefined);
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

  openMyJss(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_HOME_ROUTE);
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
    if (this.firstCategory) {
      this.postService.getTopPostByJssCategory(0, 3, this.firstCategory).subscribe(pagedPosts => {
        if (pagedPosts.content)
          this.justiceTopPosts = pagedPosts.content;
      })
    }

    if (this.secondCategory) {
      this.postService.getTopPostByJssCategory(0, 3, this.secondCategory).subscribe(pagedPosts => {
        if (pagedPosts.content)
          this.lawTopPosts = pagedPosts.content;
      })
    }

    if (this.thirdCategory) {
      this.postService.getTopPostByJssCategory(0, 3, this.thirdCategory).subscribe(pagedPosts => {
        if (pagedPosts.content)
          this.economyTopPosts = pagedPosts.content;
      })
    }
  }

  followSerie(serieToFollow: Serie, event: MouseEvent) {
    //TODO
  }

  unfollowSerie(serieToFollow: Serie, event: MouseEvent) {
    //TODO
  }

  // Audio methods
  togglePlayPodcast(post: Post) {
    if (this.audioService.currentPost && this.audioService.currentPost.id == post.id) {
      this.audioService.togglePlayPause();
    } else {
      this.audioService.loadTrack(post.id);
    }
  }

  // Audio getters
  isPlayingPodcast(post: Post) {
    return this.audioService.isPlayingPost(post);
  }
}
