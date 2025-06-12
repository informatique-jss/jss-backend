import { AfterViewInit, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../../../environments/environment';
import { getTimeReading } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { TrustHtmlPipe } from '../../../../libs/TrustHtmlPipe';
import { NewsletterComponent } from '../../../general/components/newsletter/newsletter.component';
import { Mail } from '../../../general/model/Mail';
import { AppService } from '../../../main/services/app.service';
import { ConstantService } from '../../../main/services/constant.service';
import { PlatformService } from '../../../main/services/platform.service';
import { AvatarComponent } from '../../../miscellaneous/components/avatar/avatar.component';
import { DoubleButtonsComponent } from '../../../miscellaneous/components/double-buttons/double-buttons.component';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { GenericTextareaComponent } from '../../../miscellaneous/components/forms/generic-textarea/generic-textarea.component';
import { GenericSwiperComponent } from '../../../miscellaneous/components/generic-swiper/generic-swiper.component';
import { Pagination } from '../../../miscellaneous/model/Pagination';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { Comment } from '../../model/Comment';
import { MyJssCategory } from '../../model/MyJssCategory';
import { Post } from '../../model/Post';
import { CommentService } from '../../services/comment.service';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS,
    TrustHtmlPipe,
    DoubleButtonsComponent,
    AvatarComponent,
    GenericInputComponent,
    GenericTextareaComponent,
    GenericSwiperComponent,
    NewsletterComponent]
})
export class PostComponent implements OnInit, AfterViewInit {

  currentUser: Responsable | undefined;

  slug: string | undefined;
  post: Post | undefined;
  relatedPosts: Post[] = [];
  recentPosts: Post[] = [];
  hotPosts: Post[] = [];

  speechSynthesisUtterance: SpeechSynthesisUtterance | undefined;
  speechRate: number = 1;
  isPlaying: boolean | undefined;
  audioUrl: string | undefined;

  comments: Comment[] = [];
  newComment: Comment = {} as Comment;
  newCommentParent: Comment = {} as Comment;
  createCommentForm!: FormGroup;
  commentsPagination: Pagination = {} as Pagination;
  pageSize: number = 10; // computed

  myJssCategoryFormality!: MyJssCategory;
  myJssCategoryAnnouncement!: MyJssCategory;
  myJssCategoryApostille!: MyJssCategory;
  myJssCategoryDocument!: MyJssCategory;
  myJssCategoryDomiciliation!: MyJssCategory;

  constructor(
    private activatedRoute: ActivatedRoute,
    private postService: PostService,
    private commentService: CommentService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private constantService: ConstantService,
    private platformService: PlatformService,
    private loginService: LoginService
  ) { }

  getTimeReading = getTimeReading;

  ngOnInit() {
    this.myJssCategoryFormality = this.constantService.getMyJssCategoryFormality();
    this.myJssCategoryAnnouncement = this.constantService.getMyJssCategoryAnnouncement();
    this.myJssCategoryApostille = this.constantService.getMyJssCategoryApostille();
    this.myJssCategoryDocument = this.constantService.getMyJssCategoryDocument();
    this.myJssCategoryDomiciliation = this.constantService.getMyJssCategoryDomiciliation();

    this.createCommentForm = this.formBuilder.group({});

    this.slug = this.activatedRoute.snapshot.params['slug'];

    this.loginService.getCurrentUser().subscribe(res => this.currentUser = res);
    if (this.slug) {
      this.postService.getPostBySlug(this.slug).subscribe(post => {
        this.post = post;
        this.fetchComments(0);
      })
      this.cancelReply()
    }

    this.postService.getMostSeenPosts().subscribe(posts => {
      this.hotPosts = posts;
    });

    this.postService.getTopPosts(0).subscribe(posts => {
      this.recentPosts = posts;
    });

  }

  ngOnDestroy() {
    if (this.platformService.isBrowser())
      window.speechSynthesis.pause();
    this.isPlaying = undefined;
  }

  ngAfterViewInit() {
  }

  openPost(post: Post, event: any) {
    this.appService.openRoute(event, "post/" + post.slug, undefined);
  }

  shareOnFacebook() {
    if (this.post && this.platformService.isBrowser()) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      window.open("https://www.facebook.com/sharer/sharer.php?u=" + url, "_blank");
    }
  }

  shareOnLinkedin() {
    if (this.post && this.platformService.isBrowser()) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      window.open("https://www.linkedin.com/shareArticle?mini=true&url=" + url + "&title=" + this.extractContent(this.post.titleText) + "&summary=" + this.extractContent(this.post.excerptText), "_blank");
    }
  }

  shareOnTwitter() {
    if (this.post && this.platformService.isBrowser()) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      window.open("https://twitter.com/intent/tweet?text=" + this.extractContent(this.post.titleText) + "&url=" + url, "_blank");
    }
  }

  shareOnInstagram() {
    if (this.post && this.platformService.isBrowser()) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      let message = encodeURIComponent(`Découvrez cet article : ${this.extractContent(this.post.titleText)}\n\n🔗 ${url}`);

      // Open Instagram with a copiable message
      window.open(`https://www.instagram.com/?text=${message}`, "_blank");

      // Copies link on clipboard for the user
      navigator.clipboard.writeText(url).then(() => {
        alert("Le lien de l'article a été copié ! Vous pouvez le coller sur Instagram.");
      }).catch(err => {
        console.error("Erreur lors de la copie du lien :", err);
      });
    }
  }

  shareByMail() {
    if (this.post && this.platformService.isBrowser()) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      window.open('mailto:?subject=Découvrez cet article intéressant sur JSS.FR&body=Bonjour,%0A%0AJe voulais vous partager cet article :%0A%0A' + this.extractContent(this.post.titleText) + '%0A' + url + '%0A%0ABonne lecture!', "_blank");
    }
  }

  extractContent(s: string) {
    if (this.platformService.isServer())
      return;
    var span = this.platformService.getNativeDocument()!.createElement('span');
    span.innerHTML = s;
    return span.textContent || span.innerText;
  };

  openSubscribe(event: any) {
    this.appService.openRoute(event, "order/new/subscribe/", undefined);
  }

  readArticle(): void {
    if (this.post && this.post.contentText && this.platformService.isBrowser()) {
      const articleText = this.extractContent(this.post.contentText)!;

      this.speechSynthesisUtterance = new SpeechSynthesisUtterance();
      this.speechSynthesisUtterance.text = articleText;
      this.speechSynthesisUtterance.lang = 'fr-FR';
      this.speechSynthesisUtterance.rate = this.speechRate;
      this.speechSynthesisUtterance.pitch = 1.4;

      window.speechSynthesis.speak(this.speechSynthesisUtterance);
    }
  }

  togglePlayPause(): void {
    if (this.isPlaying === undefined) {
      this.readArticle();
      this.isPlaying = true;
    } else if (this.isPlaying == false && this.platformService.isBrowser()) {
      window.speechSynthesis.resume();
      this.isPlaying = true;
    } else if (this.platformService.isBrowser()) {
      window.speechSynthesis.pause();
      this.isPlaying = false;
    }
  }

  updateSpeed(): void {
    if (this.speechSynthesisUtterance && this.platformService.isBrowser()) {
      this.speechSynthesisUtterance.rate = this.speechRate;
      window.speechSynthesis.speak(this.speechSynthesisUtterance);
    }
  }

  getResponsableNames(comment: Comment): Responsable {
    return { firstname: comment?.authorFirstName || '', lastname: comment?.authorLastNameInitials || '' } as Responsable;
  }

  postComment() {
    if (this.post) {
      if (this.currentUser) {
        if (this.currentUser.firstname)
          this.newComment.authorFirstName = this.currentUser.firstname;
        if (this.currentUser.lastname)
          this.newComment.authorLastName = this.currentUser.lastname;

        this.newComment.mail = this.currentUser.mail;
      }

      this.commentService.addOrUpdateComment(this.newComment, this.newCommentParent.id, this.post.id).subscribe(() => {
        this.fetchComments(0);
      });
    }
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

  cancelReply() {
    this.newComment = { mail: {} as Mail } as Comment;
    this.newCommentParent = {} as Comment;
  }

  fetchComments(page: number) {
    if (this.post) {
      this.relatedPosts = this.post.relatedPosts;
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

  showMoreComments() {
    this.fetchComments(this.commentsPagination.pageNumber + 1);
  }

  showLessComments() {
    this.fetchComments(0);
  }

  isPostContainsMyJssCategory(category: MyJssCategory) {
    if (this.post && this.post.myJssCategories)
      for (let cat of this.post.myJssCategories)
        if (cat.id == category.id)
          return true;
    return false;
  }
}
