import { AfterViewInit, ChangeDetectorRef, Component, OnInit, QueryList, ViewChildren } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Meta, Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, Subscription } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { MY_JSS_SIGN_IN_ROUTE } from '../../../libs/Constants';
import { validateEmail } from '../../../libs/CustomFormsValidatorsHelper';
import { getTimeReading } from '../../../libs/FormatHelper';
import { LiteralDatePipe } from '../../../libs/LiteralDatePipe';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { TrustHtmlPipe } from '../../../libs/TrustHtmlPipe';
import { AppService } from '../../../services/app.service';
import { GtmService } from '../../../services/gtm.service';
import { CtaClickPayload, FormSubmitPayload, PageInfo } from '../../../services/GtmPayload';
import { PlatformService } from '../../../services/platform.service';
import { Comment } from '../../model/Comment';
import { Mail } from '../../model/Mail';
import { PagedContent } from '../../model/PagedContent';
import { Pagination } from '../../model/Pagination';
import { Post } from '../../model/Post';
import { Responsable } from '../../model/Responsable';
import { Serie } from '../../model/Serie';
import { ONE_POST_SUBSCRIPTION } from '../../model/Subscription';
import { AudioPlayerService } from '../../services/audio.player.service';
import { CommentService } from '../../services/comment.service';
import { LoginService } from '../../services/login.service';
import { PostService } from '../../services/post.service';
import { SerieService } from '../../services/serie.service';
import { SubscriptionService } from '../../services/subscription.service';
import { AvatarComponent } from '../avatar/avatar.component';
import { BookmarkComponent } from "../bookmark/bookmark.component";
import { GenericInputComponent } from '../generic-input/generic-input.component';
import { GenericTextareaComponent } from '../generic-textarea/generic-textarea.component';
import { NewsletterComponent } from "../newsletter/newsletter.component";

declare var tns: any;

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css'],
  imports: [SHARED_IMPORTS, TrustHtmlPipe, AvatarComponent, GenericInputComponent, GenericTextareaComponent, NewsletterComponent, BookmarkComponent, LiteralDatePipe],
  standalone: true
})
export class PostComponent implements OnInit, AfterViewInit {

  slug: string | undefined;
  validationToken: string | undefined;
  post: Post | undefined;
  nextPost: Post | undefined;
  previousPost: Post | undefined;
  commentsPagination: Pagination = {} as Pagination;
  seriePost: Serie | undefined;
  postsOfSerie: Post[] = []

  comments: Comment[] = [];
  newComment: Comment = {} as Comment;
  newCommentParent: Comment = {} as Comment;

  pageSize: number = 10; // computed

  mostSeenPostsByEntityType: Post[] = [] as Array<Post>;

  speechSynthesisUtterance: SpeechSynthesisUtterance | undefined;
  speechRate: number = 1;
  isPlaying: boolean | undefined;
  audioUrl: string | undefined;
  progress: number = 0;
  progressSubscription: Subscription = new Subscription;
  recipientMail: string | undefined;

  currentUser: Responsable | undefined;

  numberOfSharingPostRemaining: number = 0;

  @ViewChildren('sliderPage') sliderPage!: QueryList<any>;
  frontendMyJssUrl = environment.frontendMyJssUrl;
  ONE_POST_SUBSCRIPTION = ONE_POST_SUBSCRIPTION;
  MY_JSS_SIGN_IN_ROUTE = MY_JSS_SIGN_IN_ROUTE;

  constructor(private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private postService: PostService,
    private appService: AppService,
    private platformService: PlatformService,
    private audioService: AudioPlayerService,
    private serieService: SerieService,
    private commentService: CommentService,
    private subscriptionService: SubscriptionService,
    private loginService: LoginService,
    private modalService: NgbModal,
    private cdr: ChangeDetectorRef,
    private gtmService: GtmService,
    private titleService: Title, private meta: Meta,
  ) { }

  getTimeReading = getTimeReading;

  giftForm!: FormGroup;
  newCommentForm!: FormGroup;

  ngOnInit() {
    this.refresh();
    this.activatedRoute.paramMap.subscribe(params => {
      this.refresh();
    });
  }

  refresh() {
    this.titleService.setTitle("Tous nos articles - JSS");
    this.meta.updateTag({ name: 'description', content: "Retrouvez l'actualité juridique et économique. JSS analyse pour vous les dernières annonces, formalités et tendances locales." });

    this.loginService.getCurrentUser().subscribe(res => this.currentUser = res);
    this.newCommentForm = this.formBuilder.group({});
    this.giftForm = this.formBuilder.group({});

    this.activatedRoute.params.subscribe(() => {
      this.refreshPost();
    });

    this.refreshPost();

    this.progressSubscription = this.audioService.progressObservable.subscribe(item => {
      this.progress = item;
      this.cdr.detectChanges();
    });
  }

  trackCtaClickOfferPost() {
    if (this.post)
      this.gtmService.trackCtaClick(
        {
          cta: { type: 'link', label: "Offrir cet article", objectId: this.post.id },
          page: {
            type: 'main',
            name: this.post!.slug
          } as PageInfo
        } as CtaClickPayload
      );
  }

  trackFormReplyComment() {
    this.gtmService.trackFormSubmit(
      {
        form: { type: 'Publier un commentaire' },
        page: {
          type: 'main',
          name: this.post!.slug
        } as PageInfo
      } as FormSubmitPayload
    );
  }

  trackFormOfferPost() {
    this.gtmService.trackFormSubmit(
      {
        form: { type: 'Offrir cet article' },
        page: {
          type: 'main',
          name: this.post!.slug
        } as PageInfo
      } as FormSubmitPayload
    );
  }


  refreshPost() {
    this.postsOfSerie = [];
    this.seriePost = undefined;
    this.validationToken = this.activatedRoute.snapshot.params['token'];
    if (this.validationToken) {
      let mail = this.activatedRoute.snapshot.params['mail'];
      this.postService.getOfferedPostByToken(this.validationToken, mail).subscribe(post => {
        this.post = post;
        if (this.post) {
          this.fetchNextPrevArticleAndSerieAndComments(this.post);
          this.setMetaData();
        }
      });

    } else {
      this.slug = this.activatedRoute.snapshot.params['slug'];
      if (this.slug) {
        this.postService.getPostBySlug(this.slug).subscribe(post => {
          this.post = post;
          if (this.post) {
            this.fetchNextPrevArticleAndSerieAndComments(this.post);
            this.setMetaData();
          }
        })
      }
    }

    this.cancelReply()
    this.fetchMostSeenPosts();
  }

  setMetaData() {
    if (this.post) {
      this.meta.updateTag({ property: 'og:title', content: this.post.titleText });
      if (this.post.media)
        this.meta.updateTag({ property: 'og:image', content: this.post.media.urlFull });
      this.meta.updateTag({ property: 'og:description', content: this.post.excerptText });
      this.meta.updateTag({ property: 'og:url', content: environment.frontendUrl + "post/" + this.post.slug });
      this.meta.updateTag({ property: 'og:type', content: 'article' });
    }
  }

  private fetchNextPrevArticleAndSerieAndComments(post: Post) {
    if (post.postSerie && post.postSerie.length > 0) {
      this.seriePost = post.postSerie[0];
      this.postService.getAllPostsBySerie(this.seriePost, 0, 15, "").subscribe(res => {
        if (res && res.content.length > 0)
          this.postsOfSerie = res.content;
      });
    }
    if (post.seoTitle)
      this.titleService.setTitle(post.seoTitle);
    else
      this.titleService.setTitle(post.titleText + " - JSS");
    if (post.seoDescription)
      this.meta.updateTag({ name: 'description', content: this.getFirstSentenceFromHtml(post.seoDescription) });
    else if (post.excerptText)
      this.meta.updateTag({ name: 'description', content: this.getFirstSentenceFromHtml(post.excerptText) });
    this.postService.getNextArticle(post).subscribe(response => this.nextPost = response);
    this.postService.getPreviousArticle(post).subscribe(response => this.previousPost = response);
    this.fetchComments(0);
  }

  getFirstSentenceFromHtml(html: string): string {
    const plain = html.replace(/<[^>]+>/g, '');
    const match = plain.match(/(.*?[.?!])\s/);
    return match ? match[1].trim() : plain.trim();
  }

  ngOnDestroy() {
    const win = this.platformService.getNativeWindow();

    if (win)
      win.speechSynthesis.pause();
    this.isPlaying = undefined;
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

  dropdownOpen = false;

  toggleDropdown(event: Event): void {
    event.preventDefault();
    this.dropdownOpen = !this.dropdownOpen;
  }

  fetchMostSeenPosts() {
    this.getMostSeenPosts(0, 5).subscribe(data => {
      if (data)
        this.mostSeenPostsByEntityType = data.content;
    });
  }

  getMostSeenPosts(page: number, pageSize: number): Observable<PagedContent<Post>> {
    return this.postService.getMostSeenPosts(page, pageSize, "");
  }

  fetchComments(page: number) {
    if (this.post) {
      this.activatedRoute.fragment.subscribe(fragment => {
        if (fragment) this.pageSize = 1000000;
        this.commentService.getParentCommentsForPost(this.post!.id, page, this.pageSize).subscribe(data => {
          if (page == 0) {
            this.comments = data.content;
          } else {
            this.comments = this.comments.concat(data.content);
          }

          if (fragment && this.platformService.isBrowser()) {
            setTimeout(() => {
              document.getElementById(fragment)?.scrollIntoView({ behavior: "smooth", block: "start" });
            }, 0);
          }
          this.commentsPagination = data.page;
        });
      });
    }
  }

  cancelReply() {
    this.newComment = { mail: {} as Mail } as Comment;
    this.newCommentParent = {} as Comment;
  }

  postComment() {
    if (this.post && this.newComment.content.trim()) {
      if (this.currentUser) {
        if (this.currentUser.firstname)
          this.newComment.authorFirstName = this.currentUser.firstname;
        if (this.currentUser.lastname)
          this.newComment.authorLastName = this.currentUser.lastname;

        this.newComment.mail = this.currentUser.mail;
      }

      this.commentService.addOrUpdateComment(this.newComment, this.newCommentParent.id, this.post.id).subscribe(() => {
        this.trackFormReplyComment();
        this.fetchComments(0);
      });
    } else if (!this.newComment.content.trim())
      this.appService.displayToast("Vous ne pouvez pas publier un commentaire sans contenu", true, "Contenu vide", 5000);
    this.cancelReply();
  }

  replyComment(comment: Comment) {
    this.newComment = { mail: {} as Mail } as Comment;
    this.newCommentParent = comment;
    // Scroll to new comment form
    this.scrollToView("commentForm");
  }

  scrollToView(anchorId: string) {
    if (this.platformService.isServer())
      return;
    const element = this.platformService.getNativeDocument()!.getElementById(anchorId);
    if (element) {
      element.scrollIntoView({ behavior: "smooth", block: "nearest" });
    }
  }

  showMoreComments() {
    this.fetchComments(this.commentsPagination.pageNumber + 1);
  }

  showLessComments() {
    this.fetchComments(0);
  }

  getResponsableNames(comment: Comment): Responsable {
    return { firstname: comment?.authorFirstName || '', lastname: comment?.authorLastNameInitials || '' } as Responsable;
  }

  shareOnFacebook() {
    const win = this.platformService.getNativeWindow();
    if (this.post && win) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      win.open("https://www.facebook.com/sharer/sharer.php?u=" + url, "_blank");
    }
  }

  shareOnLinkedin() {
    const win = this.platformService.getNativeWindow();
    if (this.post && win) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      win.open(`https://www.linkedin.com/sharing/share-offsite/?url=${encodeURIComponent(url)}`, "_blank");
    }
  }

  shareOnBluesky() {
    const win = this.platformService.getNativeWindow();
    if (this.post && win) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      win.open("https://bsky.app/intent/compose?text=" + this.extractContent(this.post.titleText), "_blank");
    }
  }

  shareByMail() {
    const win = this.platformService.getNativeWindow();
    if (this.post && win) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      win.open('mailto:?subject=Découvrez cet article intéressant sur JSS.FR&body=Bonjour,%0A%0AJe voulais vous partager cet article :%0A%0A' + this.extractContent(this.post.titleText) + '%0A' + url + '%0A%0ABonne lecture!', "_blank");
    }
  }

  openOfferPostModal(content: any) {
    this.subscriptionService.getNumberOfRemainingPostsToShareForCurrentMonth().subscribe(res => {
      if (res != null) {
        this.trackCtaClickOfferPost();
        this.numberOfSharingPostRemaining = res;
        this.modalService.open(content, { centered: true, size: 'md' });
      } else {
        this.appService.displayToast("Vous n'êtes pas autorisé à partager des articles", true, "Partage impossible", 5000);
      }
    });
  }

  givePost(modalRef: any, post: Post) {
    if (!this.recipientMail || !validateEmail(this.recipientMail)) {
      this.appService.displayToast("Merci de renseigner une adresse mail valide", true, "Adresse mail invalide", 5000)

    } else {
      this.subscriptionService.givePost(post.id, this.recipientMail).subscribe(res => {
        if (res) {
          this.trackFormOfferPost();
          modalRef.close();
          this.giftForm.reset();
          this.appService.displayToast("L'article a bien été partagé à l'adresse mail indiquée !", false, "Article partagé", 5000)
        }
      });
    }
  }

  extractContent(s: string) {
    const doc = this.platformService.getNativeDocument();
    if (doc) {
      var span = doc.createElement('span');
      span.innerHTML = s;
      return span.textContent || span.innerText;
    }
    return '';
  };

  readArticle(): void {
    const win = this.platformService.getNativeWindow();
    if (this.post && this.post.contentText && win) {
      const articleText = this.extractContent(this.post.contentText);

      this.speechSynthesisUtterance = new SpeechSynthesisUtterance();
      this.speechSynthesisUtterance.text = articleText;
      this.speechSynthesisUtterance.lang = 'fr-FR';
      this.speechSynthesisUtterance.rate = this.speechRate;
      this.speechSynthesisUtterance.pitch = 1.4;

      win.speechSynthesis.speak(this.speechSynthesisUtterance);
    }
  }

  togglePlayPause(post: Post) {
    if (this.audioService.currentPost && this.audioService.currentPost.id == post.id && this.isPlaying) {
      this.audioService.togglePlayPause();
      this.isPlaying = this.audioService.getIsPlaying();
    } else {
      this.audioService.loadTrack(post.id);
      this.isPlaying = true;
    }
  }

  changeSpeechRate() {
    this.audioService.changeSpeechRate();
    this.speechRate = this.audioService.getSpeechRate();
  }


  updateSpeed(): void {
    const win = this.platformService.getNativeWindow();
    if (this.speechSynthesisUtterance && win) {
      this.speechSynthesisUtterance.rate = this.speechRate;
      win.speechSynthesis.speak(this.speechSynthesisUtterance);
    }
  }

  getIsPlaying(post: Post) {
    return this.audioService.isPlayingPost(post);
  }


  getDuration() {
    return this.audioService.getDuration();
  }

  getCurrentTime() {
    return this.audioService.getCurrentTime();
  }


  onSeek(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.audioService.seekTo(+value);
  }


}
