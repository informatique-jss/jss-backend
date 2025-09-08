import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { GtmService } from '../../../main/services/gtm.service';

@Component({
  selector: 'cookie-consent',
  templateUrl: './cookie-consent.component.html',
  styleUrls: ['./cookie-consent.component.css'],
  standalone: true,
  imports: SHARED_IMPORTS
})
export class CookieConsentComponent implements OnInit {

  showBanner = false;
  COOKIE_KEY = "consent-cookie";

  constructor(private gtm: GtmService) { }

  ngOnInit() {
    const consent = localStorage.getItem(this.COOKIE_KEY);
    if (consent === null) {
      // Never asked
      this.showBanner = true;
    } else if (consent === 'true') {
      this.accept();
    }
  }

  accept() {
    localStorage.setItem(this.COOKIE_KEY, 'true');
    this.showBanner = false;
    this.gtm.init();
  }

  reject() {
    localStorage.setItem(this.COOKIE_KEY, 'false');
    this.showBanner = false;
  }

}
