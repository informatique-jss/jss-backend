import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { ConstantService } from '../../../main/services/constant.service';
import { PlatformService } from '../../../main/services/platform.service';
import { DoubleButtonsComponent } from '../../../miscellaneous/components/double-buttons/double-buttons.component';
import { GenericSwiperComponent } from '../../../miscellaneous/components/generic-swiper/generic-swiper.component';
import { OurClientsComponent } from '../../../miscellaneous/components/our-clients/our-clients.component';
import { Post } from '../../../tools/model/Post';
import { PostService } from '../../../tools/services/post.service';
import { DescriptionMyAccountComponent } from '../description-my-account/description-my-account.component';
import { ExplainationVideoComponent } from '../explaination-video/explaination-video.component';

@Component({
  selector: 'domiciliation',
  templateUrl: './domiciliation.component.html',
  styleUrls: ['./domiciliation.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, DoubleButtonsComponent, ExplainationVideoComponent, DescriptionMyAccountComponent, GenericSwiperComponent, OurClientsComponent]
})
export class DomiciliationComponent implements OnInit {

  tendencyPosts: Post[] = [];

  constructor(private appService: AppService,
    private postService: PostService,
    private constantService: ConstantService,
    private platformService: PlatformService
  ) { }

  ngOnInit() {
    this.postService.getTopPostByMyJssCategory(0, this.constantService.getMyJssCategoryDomiciliation()).subscribe(response => {
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

  openPost(slug: string, event: any) {
    this.appService.openRoute(event, "post/" + slug, undefined);
  }

  openFormality(event: any) {
    this.appService.openRoute(event, "/services/formality", undefined);
  }

  openApostille(event: any) {
    this.appService.openRoute(event, "/services/apostille", undefined);
  }

  openAnnouncements(event: any) {
    this.appService.openRoute(event, "/services/announcement", undefined);
  }

  openDocument(event: any) {
    this.appService.openRoute(event, "/services/document", undefined);
  }
}
