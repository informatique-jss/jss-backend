import { AfterViewInit, Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../../../environments/environment';
import { AppService } from '../../../../libs/app.service';
import { getTimeReading } from '../../../../libs/FormatHelper';
import { PagedContent } from '../../../miscellaneous/model/PagedContent';
import { Pagination } from '../../../miscellaneous/model/Pagination';
import { Mail } from '../../../profile/model/Mail';
import { Responsable } from '../../../profile/model/Responsable';
import { Comment } from '../../model/Comment';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';
import { CommentService } from '../../services/comment.service';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css'],
})
export class PostComponent implements OnInit, AfterViewInit {

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
  createCommentForm = this.formBuilder.group({});
  commentsPagination: Pagination = {} as Pagination;
  shownElements: number = 10; // computed

  targetCommentId: string | null = null;

  @ViewChildren('sliderPage') sliderPage!: QueryList<any>;
  @ViewChild('step2') step2: ElementRef | undefined;

  constructor(private activatedRoute: ActivatedRoute,
    private postService: PostService,
    private commentService: CommentService,
    private formBuilder: FormBuilder,
    private appService: AppService,
  ) { }

  getTimeReading = getTimeReading;

  ngOnInit() {
    this.activatedRoute.fragment.subscribe(fragment => {
      if (fragment) {
        this.targetCommentId = fragment;
        setTimeout(this.scrollToComment, 10);
      }
    });

    this.slug = this.activatedRoute.snapshot.params['slug'];

    if (this.slug) {
      this.postService.getPostBySlug(this.slug).subscribe(post => {
        this.post = post;
        this.fetchComments(0);
      })
      this.cancelReply()
    }

    this.postService.getPostsTendency().subscribe(posts => {
      this.hotPosts = posts;
    });

    this.postService.getTopPosts(0).subscribe(posts => {
      this.recentPosts = posts;
    });

  }

  ngOnDestroy() {
    window.speechSynthesis.pause();
    this.isPlaying = undefined;
  }

  ngAfterViewInit() {

  }

  openPost(post: Post, event: any) {
    this.appService.openRoute(event, "post/" + post.slug, undefined);
  }

  openTagPosts(tag: Tag, event: any) {
    this.appService.openRoute(event, "tag/" + tag.slug, undefined);
  }

  shareOnFacebook() {
    if (this.post) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      window.open("https://www.facebook.com/sharer/sharer.php?u=" + url, "_blank");
    }
  }

  shareOnLinkedin() {
    if (this.post) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      window.open("https://www.linkedin.com/shareArticle?mini=true&url=" + url + "&title=" + this.extractContent(this.post.titleText) + "&summary=" + this.extractContent(this.post.excerptText), "_blank");
    }
  }

  shareOnTwitter() {
    if (this.post) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      window.open("https://twitter.com/intent/tweet?text=" + this.extractContent(this.post.titleText) + "&url=" + url, "_blank");
    }
  }

  shareOnInstagram() {
    if (this.post) {
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
    if (this.post) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      window.open('mailto:?subject=Découvrez cet article intéressant sur JSS.FR&body=Bonjour,%0A%0AJe voulais vous partager cet article :%0A%0A' + this.extractContent(this.post.titleText) + '%0A' + url + '%0A%0ABonne lecture!', "_blank");
    }
  }

  extractContent(s: string) {
    var span = document.createElement('span');
    span.innerHTML = s;
    return span.textContent || span.innerText;
  };

  openSubscribe(event: any) {
    this.appService.openRoute(event, "order/new/subscribe/", undefined);
  }

  readArticle(): void {
    if (this.post && this.post.contentText) {
      const articleText = this.extractContent(this.post.contentText);

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
    } else if (this.isPlaying == false) {
      window.speechSynthesis.resume();
      this.isPlaying = true;
    } else {
      window.speechSynthesis.pause();
      this.isPlaying = false;
    }
  }

  updateSpeed(): void {
    if (this.speechSynthesisUtterance) {
      this.speechSynthesisUtterance.rate = this.speechRate;
      window.speechSynthesis.speak(this.speechSynthesisUtterance);
    }
  }

  getResponsableNames(comment: Comment): Responsable {
    return { firstname: comment?.authorFirstName || '', lastname: comment?.authorLastNameInitials || '' } as Responsable;
  }

  postComment() {
    if (this.post) {
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
    const element = document.getElementById("commentForm");
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
      // TODO : this code is to do the scrolling to the comment in the page when comming from osiris
      //  if (this.targetCommentId) {
      //   this.commentService.getParentCommentsForPost(this.post.id, page, 1000000).subscribe(data => {
      //     this.comments = data.content;
      //     this.updatePagination(data, page);
      //   });
      // } else {
      this.commentService.getParentCommentsForPost(this.post.id, page, 10).subscribe(data => {
        if (page == 0) {
          this.comments = data.content;
        } else {
          this.comments = this.comments.concat(data.content);
        }
        this.commentsPagination = data.page;
        this.shownElements = this.computeShownElements(page);
      });
      // }
    }
  }

  showMoreComments() {
    this.fetchComments(this.commentsPagination.pageNumber + 1);
  }

  showLessComments() {
    this.fetchComments(0);
  }

  // Scrolls to the selected
  scrollToComment() {
    if (this.targetCommentId) {
      const targetElement = document.getElementById(this.targetCommentId);
      if (targetElement) {
        targetElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
    }
  }


  private updatePagination(data: PagedContent<Comment>, page: number) {
    this.commentsPagination = data.page;
    this.shownElements = this.computeShownElements(page);
  }

  computeShownElements(page: number) {
    page++; // the current page is indexed from 0 as an array, whereas the totalPages is the result of the .lenght array
    if (page == this.commentsPagination.totalPages) {
      return ((this.commentsPagination.totalPages - 1) * this.commentsPagination.pageSize + this.commentsPagination.numberOfElements);
    } else {
      return page * this.commentsPagination.pageSize;
    }
  }
}
