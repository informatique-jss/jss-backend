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
import { Post } from '../../../tools/model/Post';
import { PostService } from '../../../tools/services/post.service';
import { DescriptionMyAccountComponent } from '../description-my-account/description-my-account.component';
import { ExplainationVideoComponent } from '../explaination-video/explaination-video.component';

@Component({
  selector: 'document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, DoubleButtonsComponent, ExplainationVideoComponent, GenericSwiperComponent, OurClientsComponent, DescriptionMyAccountComponent]
})
export class DocumentComponent implements OnInit {

  tendencyPosts: Post[] = [];

  serviceFamilyGroupOther: ServiceFamilyGroup | undefined;
  quotationTypeOrder: QuotationType = QUOTATION_TYPE_ORDER;
  quotationTypeQuotation: QuotationType = QUOTATION_TYPE_QUOTATION;

  getRawTextFromHtml = getRawTextFromHtml;

  constructor(private appService: AppService,
    private postService: PostService,
    private constantService: ConstantService,
    private platformService: PlatformService,
    private titleService: Title, private meta: Meta,
  ) {
  }
  ngOnInit() {
    this.titleService.setTitle("Documents juridiques - MyJSS");
    this.meta.updateTag({ name: 'description', content: "Accédez à des modèles de documents juridiques rédigés par nos experts. MyJSS vous assure des statuts, PV d'AG et autres actes conformes, prêts à l'emploi." });
    this.serviceFamilyGroupOther = this.constantService.getServiceFamilyGroupOther();
    this.postService.getTopPostByMyJssCategory(0, this.constantService.getMyJssCategoryDocument()).subscribe(response => {
      if (response && response.content && response.content.length > 0) {
        this.tendencyPosts = response.content;
      }
    });
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
