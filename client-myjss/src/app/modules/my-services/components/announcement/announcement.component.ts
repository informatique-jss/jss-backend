import { Component, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { ConstantService } from '../../../main/services/constant.service';
import { PlatformService } from '../../../main/services/platform.service';
import { DoubleButtonsComponent } from '../../../miscellaneous/components/double-buttons/double-buttons.component';
import { GenericSwiperComponent } from '../../../miscellaneous/components/generic-swiper/generic-swiper.component';
import { OurClientsComponent } from '../../../miscellaneous/components/our-clients/our-clients.component';
import { QUOTATION_TYPE_ORDER, QUOTATION_TYPE_QUOTATION, QuotationType } from '../../../quotation/model/QuotationType';
import { ServiceFamilyGroup } from '../../../quotation/model/ServiceFamilyGroup';
import { MyJssCategory } from '../../../tools/model/MyJssCategory';
import { Post } from '../../../tools/model/Post';
import { PostService } from '../../../tools/services/post.service';
import { DescriptionMyAccountComponent } from '../description-my-account/description-my-account.component';
import { ExplainationVideoComponent } from '../explaination-video/explaination-video.component';

@Component({
  selector: 'announcement',
  templateUrl: './announcement.component.html',
  styleUrls: ['./announcement.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, DoubleButtonsComponent, ExplainationVideoComponent, DescriptionMyAccountComponent, GenericSwiperComponent, OurClientsComponent]
})
export class AnnouncementComponent implements OnInit {
  myJssCategoryAnnouncement!: MyJssCategory;
  carouselAnnouncementPosts: Post[] = [];
  tendencyPosts: Post[] = [];
  serviceFamilyGroupAnnouncement: ServiceFamilyGroup | undefined;
  quotationTypeOrder: QuotationType = QUOTATION_TYPE_ORDER;
  quotationTypeQuotation: QuotationType = QUOTATION_TYPE_QUOTATION;

  constructor(private appService: AppService,
    private constantService: ConstantService,
    private postService: PostService,
    private platformService: PlatformService,
    private titleService: Title, private meta: Meta,
  ) { }

  ngOnInit() {
    this.titleService.setTitle("Publication annonces légales - MyJSS");
    this.meta.updateTag({ name: 'description', content: "Publiez votre annonce légale en quelques clics avec MyJSS. Obtenez une attestation de parution immédiate au meilleur prix et un accompagnement d'experts dédiés." });
    this.serviceFamilyGroupAnnouncement = this.constantService.getServiceFamilyGroupAnnouncement();
    this.myJssCategoryAnnouncement = this.constantService.getMyJssCategoryAnnouncement();
    this.postService.getTopPostByMyJssCategory(0, this.constantService.getMyJssCategoryAnnouncement()).subscribe(response => {
      if (response && response.content && response.content.length > 0) {
        this.tendencyPosts = response.content;
      }
    });

    this.postService.getPinnedPostByMyJssCategory(0, this.myJssCategoryAnnouncement).subscribe(posts => {
      if (posts && posts.content)
        this.carouselAnnouncementPosts = posts.content;
    });
  }

  ngAfterViewInit(): void {
    if (this.platformService.getNativeDocument())
      import('jarallax').then(module => {
        module.jarallax(this.platformService.getNativeDocument()!.querySelectorAll('.jarallax'), {
          speed: 0.8
        });
      });
  }

  openRoute(route: string) {
    this.appService.openRoute(undefined, route, undefined);
  }

}
