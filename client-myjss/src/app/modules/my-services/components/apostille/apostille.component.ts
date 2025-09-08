import { Component, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { ConstantService } from '../../../main/services/constant.service';
import { GtmService } from '../../../main/services/gtm.service';
import { CtaClickPayload, PageInfo } from '../../../main/services/GtmPayload';
import { PlatformService } from '../../../main/services/platform.service';
import { DoubleButtonsComponent } from '../../../miscellaneous/components/double-buttons/double-buttons.component';
import { GenericSwiperComponent } from '../../../miscellaneous/components/generic-swiper/generic-swiper.component';
import { OurClientsComponent } from '../../../miscellaneous/components/our-clients/our-clients.component';
import { QUOTATION_TYPE_ORDER, QUOTATION_TYPE_QUOTATION, QuotationType } from '../../../quotation/model/QuotationType';
import { ServiceFamilyGroup } from '../../../quotation/model/ServiceFamilyGroup';
import { Post } from '../../../tools/model/Post';
import { PostService } from '../../../tools/services/post.service';
import { DescriptionMyAccountComponent } from '../description-my-account/description-my-account.component';
import { ExplainationVideoComponent } from '../explaination-video/explaination-video.component';

@Component({
  selector: 'apostille',
  templateUrl: './apostille.component.html',
  styleUrls: ['./apostille.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, DoubleButtonsComponent, ExplainationVideoComponent, GenericSwiperComponent, OurClientsComponent, DescriptionMyAccountComponent]
})
export class ApostilleComponent implements OnInit {
  tendencyPosts: Post[] = [];

  serviceFamilyGroupOther: ServiceFamilyGroup | undefined;
  quotationTypeOrder: QuotationType = QUOTATION_TYPE_ORDER;
  quotationTypeQuotation: QuotationType = QUOTATION_TYPE_QUOTATION;
  serviceFamilyGroupFormality: ServiceFamilyGroup | undefined;

  constructor(private appService: AppService,
    private postService: PostService,
    private constantService: ConstantService,
    private platformService: PlatformService,
    private gtmService: GtmService,
    private titleService: Title, private meta: Meta,
  ) {
  }
  ngOnInit() {
    this.meta.updateTag({ name: 'description', content: "MyJSS sécurise vos démarches à l'international. Confiez-nous vos besoins d'apostilles et de légalisation de documents pour une conformité garantie et des délais optimisés." });
    this.titleService.setTitle("Apostilles et légalisation - MyJSS");
    this.serviceFamilyGroupOther = this.constantService.getServiceFamilyGroupOther();
    this.serviceFamilyGroupFormality = this.constantService.getServiceFamilyGroupFormality();
    this.postService.getTopPostByMyJssCategory(0, this.constantService.getMyJssCategoryApostille()).subscribe(response => {
      if (response && response.content && response.content.length > 0) {
        this.tendencyPosts = response.content;
      }
    });
  }

  trackCtaClickOrder() {
    this.gtmService.trackCtaClick(
      {
        cta: { type: 'order', label: 'Commandez un Kbis' },
        page: {
          type: 'services',
          name: 'apostille'
        } as PageInfo
      } as CtaClickPayload
    );
  }

  ngAfterViewInit(): void {
    if (this.platformService.getNativeDocument())
      import('jarallax').then(module => {
        module.jarallax(this.platformService.getNativeDocument()!.querySelectorAll('.jarallax'), {
          speed: 0.5
        });
      });
  }

}
