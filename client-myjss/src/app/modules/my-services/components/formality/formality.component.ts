import { Component, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { getRawTextFromHtml } from '../../../../libs/FormatHelper';
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
  selector: 'formality',
  templateUrl: './formality.component.html',
  styleUrls: ['./formality.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, DoubleButtonsComponent, GenericSwiperComponent, ExplainationVideoComponent, DescriptionMyAccountComponent, OurClientsComponent]
})
export class FormalityComponent implements OnInit {
  tendencyPosts: Post[] = [] as Array<Post>;
  myJssCategoryFormality!: MyJssCategory;
  carouselFormalityPosts: Post[] = [];
  serviceFamilyGroupFormality: ServiceFamilyGroup | undefined;
  quotationTypeOrder: QuotationType = QUOTATION_TYPE_ORDER;
  quotationTypeQuotation: QuotationType = QUOTATION_TYPE_QUOTATION;

  getRawTextFromHtml = getRawTextFromHtml;

  constructor(private appService: AppService,
    private constantService: ConstantService,
    private postService: PostService,
    private platformService: PlatformService,
    private titleService: Title, private meta: Meta,
  ) { }

  ngOnInit() {
    this.titleService.setTitle("Formalités légales - MyJSS");
    this.meta.updateTag({ name: 'description', content: "Confiez l'ensemble de vos formalités légales à MyJSS. De la création à la dissolution, nos juristes experts vous assurent une gestion simple, rapide et sécurisée." });
    this.serviceFamilyGroupFormality = this.constantService.getServiceFamilyGroupFormality();
    this.myJssCategoryFormality = this.constantService.getMyJssCategoryFormality();
    this.postService.getPinnedPostByMyJssCategory(0, this.myJssCategoryFormality).subscribe(posts => {
      if (posts && posts.content)
        this.carouselFormalityPosts.push(...posts.content);
    });
    this.postService.getTopPostByMyJssCategory(0, this.constantService.getMyJssCategoryFormality()).subscribe(response => {
      if (response && response.content && response.content.length > 0) {
        this.tendencyPosts = response.content;
      }
    });
  }

  ngAfterViewInit(): void {
    if (this.platformService.getNativeDocument())
      import('jarallax').then(module => {
        module.jarallax(this.platformService.getNativeDocument()!.querySelectorAll('.jarallax'), {
          speed: 0.7
        });
      });
  }

  openRoute(route: string) {
    this.appService.openRoute(undefined, route, undefined);
  }
}
