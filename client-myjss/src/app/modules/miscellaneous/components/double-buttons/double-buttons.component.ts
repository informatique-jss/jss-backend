import { Component, Input, OnInit } from '@angular/core';
import { environment } from '../../../../../environments/environment';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { GtmService } from '../../../main/services/gtm.service';
import { CtaClickPayload, PageInfo } from '../../../main/services/GtmPayload';

@Component({
  selector: 'double-buttons',
  templateUrl: './double-buttons.component.html',
  styleUrls: ['./double-buttons.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class DoubleButtonsComponent implements OnInit {
  @Input() orderActionLabel: string = "";
  @Input() orderActionRoute: string = "";
  @Input() quotationActionLabel: string = "";
  @Input() quotationActionRoute: string = "";
  @Input() linkLabel: string = "";
  @Input() linkRoute: string = "";
  @Input() linkRouteToJssMedia: boolean = false;
  @Input() isLightButtons: boolean = true;
  @Input() fromPageInfo: PageInfo = {} as PageInfo;

  frontendJssUrl = environment.frontendJssUrl;

  constructor(
    private appService: AppService,
    private gtmService: GtmService
  ) { }

  ngOnInit() {
  }

  trackCtaClickOrder() {
    this.gtmService.trackCtaClick(
      {
        cta: { type: 'order', label: this.orderActionLabel },
        page: this.fromPageInfo as PageInfo
      } as CtaClickPayload
    );
  }

  trackCtaClickQuotation() {
    this.gtmService.trackCtaClick(
      {
        cta: { type: 'quotation', label: this.quotationActionLabel },
        page: this.fromPageInfo as PageInfo
      } as CtaClickPayload
    );
  }

  trackCtaClickLink() {
    this.gtmService.trackCtaClick(
      {
        cta: { type: 'link', label: this.linkLabel },
        page: this.fromPageInfo as PageInfo
      } as CtaClickPayload
    );
  }
}
