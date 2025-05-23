import { AfterViewInit, Component, OnInit, QueryList, ViewChildren } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../../environments/environment';
import { MY_JSS_SUBSCRIBE_ROUTE } from '../../../libs/Constants';
import { getTimeReading } from '../../../libs/FormatHelper';
import { AppService } from '../../../services/app.service';
import { PlatformService } from '../../../services/platform.service';
import { Author } from '../../model/Author';
import { JssCategory } from '../../model/JssCategory';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';
import { PostService } from '../../services/post.service';

declare var tns: any;

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.css'],
  standalone: false
})
export class PostComponent implements OnInit, AfterViewInit {

  slug: string | undefined;
  post: Post | undefined;
  nextPost: Post | undefined;
  previousPost: Post | undefined;

  speechSynthesisUtterance: SpeechSynthesisUtterance | undefined;
  speechRate: number = 1;
  isPlaying: boolean | undefined;
  audioUrl: string | undefined;

  @ViewChildren('sliderPage') sliderPage!: QueryList<any>;

  constructor(private activatedRoute: ActivatedRoute,
    private postService: PostService,
    private appService: AppService,
    private plateformDocument: PlatformService
  ) { }

  getTimeReading = getTimeReading;

  ngOnInit() {
    this.slug = this.activatedRoute.snapshot.params['slug'];
    if (this.slug)
      this.postService.getPostBySlug(this.slug).subscribe(post => {
        this.post = post;
        if (this.post) {
          this.postService.getNextArticle(this.post).subscribe(response => this.nextPost = response);
          this.postService.getPreviousArticle(this.post).subscribe(response => this.previousPost = response);
        }
      })
  }

  ngOnDestroy() {
    const win = this.plateformDocument.getNativeWindow();

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

  openPost(post: Post, event: any) {
    this.appService.openRoute(event, "post/" + post.slug, undefined);
  }

  openAuthorPosts(author: Author, event: any) {
    this.appService.openRoute(event, "author/" + author.slug, undefined);
  }

  openCategoryPosts(category: JssCategory, event: any) {
    this.appService.openRoute(event, "category/" + category.slug, undefined);
  }

  openTagPosts(tag: Tag, event: any) {
    this.appService.openRoute(event, "tag/" + tag.slug, undefined);
  }

  shareOnFacebook() {
    const win = this.plateformDocument.getNativeWindow();
    if (this.post && win) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      win.open("https://www.facebook.com/sharer/sharer.php?u=" + url, "_blank");
    }
  }

  shareOnLinkedin() {
    const win = this.plateformDocument.getNativeWindow();
    if (this.post && win) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      win.open("https://www.linkedin.com/shareArticle?mini=true&url=" + url + "&title=" + this.extractContent(this.post.titleText) + "&summary=" + this.extractContent(this.post.excerptText), "_blank");
    }
  }

  shareOnTwitter() {
    const win = this.plateformDocument.getNativeWindow();
    if (this.post && win) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      win.open("https://twitter.com/intent/tweet?text=" + this.extractContent(this.post.titleText) + "&url=" + url, "_blank");
    }
  }

  shareByMail() {
    const win = this.plateformDocument.getNativeWindow();
    if (this.post && win) {
      let url = environment.frontendUrl + "post/" + this.post.slug;
      win.open('mailto:?subject=Découvrez cet article intéressant sur JSS.FR&body=Bonjour,%0A%0AJe voulais vous partager cet article :%0A%0A' + this.extractContent(this.post.titleText) + '%0A' + url + '%0A%0ABonne lecture!', "_blank");
    }
  }

  extractContent(s: string) {
    const doc = this.plateformDocument.getNativeDocument();
    if (doc) {
      var span = doc.createElement('span');
      span.innerHTML = s;
      return span.textContent || span.innerText;
    }
    return '';
  };

  openSubscribe(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_SUBSCRIBE_ROUTE);
  }

  readArticle(): void {
    const win = this.plateformDocument.getNativeWindow();
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

  togglePlayPause(): void {
    const win = this.plateformDocument.getNativeWindow();
    if (win)
      if (this.isPlaying === undefined) {
        this.readArticle();
        this.isPlaying = true;
      } else if (this.isPlaying == false) {
        win.speechSynthesis.resume();
        this.isPlaying = true;
      } else {
        win.speechSynthesis.pause();
        this.isPlaying = false;
      }
  }

  updateSpeed(): void {
    const win = this.plateformDocument.getNativeWindow();
    if (this.speechSynthesisUtterance && win) {
      this.speechSynthesisUtterance.rate = this.speechRate;
      win.speechSynthesis.speak(this.speechSynthesisUtterance);
    }
  }
}
