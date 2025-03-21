import { Component, OnInit, QueryList, ViewChildren } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../../../environments/environment';
import { AppService } from '../../../../libs/app.service';
import { getTimeReading } from '../../../../libs/FormatHelper';
import { Author } from '../../model/Author';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';
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
  recentPosts: Post[] = [];;
  hotPosts: Post[] = [];;

  speechSynthesisUtterance: SpeechSynthesisUtterance | undefined;
  speechRate: number = 1;
  isPlaying: boolean | undefined;
  audioUrl: string | undefined;

  commments: Comment[] = [];

  @ViewChildren('sliderPage') sliderPage!: QueryList<any>;

  constructor(private activatedRoute: ActivatedRoute,
    private postService: PostService,
    private appService: AppService,
  ) { }

  getTimeReading = getTimeReading;

  ngOnInit() {
    this.slug = this.activatedRoute.snapshot.params['slug'];
    if (this.slug)
      this.postService.getPostBySlug(this.slug).subscribe(post => {
        this.post = post;
        if (this.post) {
          this.relatedPosts = this.post.relatedPosts;
        }
      })

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

  openAuthorPosts(author: Author, event: any) {
    this.appService.openRoute(event, "author/" + author.slug, undefined);
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
}
