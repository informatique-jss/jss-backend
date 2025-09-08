import { Component, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
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

  annoncesLegalesPicto: string = "assets/img/icons/Annonces.svg"
  formalitesPicto: string = "assets/img/icons/Formalite.svg"
  apostilleLegaPicto: string = "assets/img/icons/Apostille.svg"
  domiciliationPicto: string = "assets/img/icons/Domiciliation.svg"
  fournitureDocumentsPicto: string = "assets/img/icons/Fourniture_documents.svg"
  logoJss: string = '/assets/img/others/myjss-logos/white-logo.svg';
  videoParis: string = 'assets/videos/paris-home-video.webm'

  tendencyPosts: Post[] = [];


  constructor(
    private appService: AppService,
    private postService: PostService,
    private titleService: Title, private meta: Meta,
  ) { }

  ngOnInit() {
    this.titleService.setTitle("Annonces légales - Formalités légales - MyJSS");
    this.meta.updateTag({ name: 'description', content: "MyJSS est votre partenaire unique pour vos annonces légales et formalités légales. Profitez d'une plateforme intuitive et de l'expertise de nos juristes dédiés." });
    this.postService.getTendencyPosts().subscribe(response => {
      if (response && response.length > 0) {
        this.tendencyPosts = response;
      }
    });
  }
}
