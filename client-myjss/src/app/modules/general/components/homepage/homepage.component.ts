import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { GenericSwiperComponent } from '../../../miscellaneous/components/generic-swiper/generic-swiper.component';
import { GenericTestimonialComponent } from '../../../miscellaneous/components/generic-testimonial/generic-testimonial.component';
import { OurClientsComponent } from '../../../miscellaneous/components/our-clients/our-clients.component';
import { Post } from '../../../tools/model/Post';
import { PostService } from '../../../tools/services/post.service';
import { NewsletterComponent } from '../newsletter/newsletter.component';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericSwiperComponent, GenericTestimonialComponent, NewsletterComponent, OurClientsComponent]
})
export class HomepageComponent implements OnInit {

  annoncesLegalesPicto: string = "assets/pictos/Annonces.svg"
  formalitesPicto: string = "assets/pictos/Formalités.svg"
  apostilleLegaPicto: string = "assets/pictos/Apostille&Légalisation.svg"
  domiciliationPicto: string = "assets/pictos/Domiciliation.svg"
  fournitureDocumentsPicto: string = "assets/pictos/Fourniture-documents.svg"
  logoJss: string = '/assets/images/white-logo.svg';
  videoParis: string = 'assets/videos/paris-home-video.webm'

  tendencyPosts: Post[] = [];


  constructor(
    private appService: AppService,
    private postService: PostService
  ) { }

  ngOnInit() {
    this.postService.getTendencyPosts().subscribe(response => {
      if (response && response.length > 0) {
        this.tendencyPosts = response;
      }
    });
  }

  openAnnouncements(event: any) {
    this.appService.openRoute(event, "/services/announcement", undefined);
  }

  openFormality(event: any) {
    this.appService.openRoute(event, "/services/formality", undefined);
  }

  openApostille(event: any) {
    this.appService.openRoute(event, "/services/apostille", undefined);
  }

  openDomiciliation(event: any) {
    this.appService.openRoute(event, "/services/domiciliation", undefined);
  }

  openDocument(event: any) {
    this.appService.openRoute(event, "/services/document", undefined);
  }

  openPost(slug: string, event: any) {
    this.appService.openRoute(event, "post/" + slug, undefined);
  }
}
