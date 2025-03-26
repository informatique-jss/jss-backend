import { Component, OnInit, QueryList, ViewChildren } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../../../environments/environment';
import { AppService } from '../../../../libs/app.service';
import { getTimeReading } from '../../../../libs/FormatHelper';
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
export class PostComponent implements OnInit {

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
  createCommentForm = this.formBuilder.group({});
  totalElements: number = 0;
  totalPages: number = 0;
  currentPage: number = 0;
  pageSize: number = 10;
  numberOfElements: number = 10;
  shownElements: number = 10; // computed

  @ViewChildren('sliderPage') sliderPage!: QueryList<any>;

  constructor(private activatedRoute: ActivatedRoute,
    private postService: PostService,
    private commentService: CommentService,
    private formBuilder: FormBuilder,
    private appService: AppService,
  ) { }

  getTimeReading = getTimeReading;

  ngOnInit() {

    this.fetchComments(0);

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

  getResponsable(comment: Comment): Responsable {
    return { firstname: comment?.authorFirstName || '', lastname: comment?.authorLastName || '' } as Responsable;
  }

  postComment() {
    this.commentService.addOrUpdateComment(this.newComment).subscribe(() => {
      this.fetchComments(0);
    });
  }

  replyComment(comment: Comment) {
    this.newComment.parentComment = comment;

    // Scroll to new comment form
    const element = document.getElementById("commentForm");
    if (element) {
      element.scrollIntoView({ behavior: "smooth", block: "nearest" });
    }
  }

  cancelReply() {
    this.newComment = {} as Comment;
  }

  fetchComments(page: number) {
    this.slug = this.activatedRoute.snapshot.params['slug'];

    if (this.slug) {
      this.postService.getPostBySlug(this.slug).subscribe(post => {
        this.post = post;
        if (this.post) {
          this.relatedPosts = this.post.relatedPosts;
          this.newComment.post = post;
          this.commentService.getParentCommentsForPost(this.post.id, page, 10).subscribe(data => {
            if (page == 0) {
              this.comments = data.content;
            } else {
              this.comments = this.comments.concat(data.content);
            }
            this.totalElements = data.totalElements;
            this.totalPages = data.totalPages;
            this.numberOfElements = data.numberOfElements;
            this.currentPage = data.number;
            this.shownElements = this.computeShownElements(page);
          })
        }
      })
      this.newComment = { isDeleted: false, mail: {} as Mail } as Comment;
    }
  }

  showMoreComments() {
    this.fetchComments(this.currentPage + 1);
  }

  showLessComments() {
    this.fetchComments(0);
  }

  computeShownElements(page: number) {
    page++; // the current page is indexed from 0 as an array, whereas the totalPages is the result of the .lenght array
    if (page == this.totalPages) {
      return ((this.totalPages - 1) * this.pageSize + this.numberOfElements);
    } else {
      return page * this.pageSize;
    }
  }
}
