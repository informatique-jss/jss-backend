import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { CookieService } from '../../../services/cookie.service';
import { GtmService } from '../../../services/gtm.service';

@Component({
  selector: 'cookie-consent',
  templateUrl: './cookie-consent.component.html',
  styleUrls: ['./cookie-consent.component.css'],
  standalone: true,
  imports: SHARED_IMPORTS
})
export class CookieConsentComponent implements OnInit {

  showBanner = false;

  constructor(private gtm: GtmService,
    private cookieService: CookieService
  ) { }

  ngOnInit() {
    const consent = this.cookieService.getConsent();
    if (consent === null) {
      this.showBanner = true;
    } else if (consent === true) {
      this.accept();
    } else if (consent === false) {
      this.reject();
    }
  }

  accept() {
    this.cookieService.setConsent(true);
    this.showBanner = false;
    this.gtm.init();
  }

  reject() {
    this.cookieService.setConsent(false);
    this.showBanner = false;
    this.gtm.init();
  }
}
