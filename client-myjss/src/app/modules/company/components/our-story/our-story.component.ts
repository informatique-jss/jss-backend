import { AfterViewInit, Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { MenuItem } from '../../../general/model/MenuItem';
import { AppService } from '../../../main/services/app.service';
import { PlatformService } from '../../../main/services/platform.service';
import { OurStorySwiperComponent } from '../our-story-swiper/our-story-swiper.component';

@Component({
  selector: 'our-story',
  templateUrl: './our-story.component.html',
  styleUrls: ['./our-story.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, OurStorySwiperComponent]
})
export class OurStoryComponent implements OnInit, AfterViewInit {

  companyItems!: MenuItem[];

  constructor(
    private appService: AppService,
    private platformService: PlatformService
  ) { }

  ngOnInit(): void {
    this.companyItems = this.appService.getAllCompanyMenuItems();
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
