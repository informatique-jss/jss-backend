import { Component, OnInit } from '@angular/core';
import { COOKIE_KEY } from '../../../libs/Constants';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
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
  constructor(private gtm: GtmService) { }

  ngOnInit() {
    const consent = localStorage.getItem(COOKIE_KEY);
    if (consent === null) {
      // Never asked
      this.showBanner = true;
    } else if (consent === 'true') {
      this.accept();
    }
  }

  accept() {
    localStorage.setItem(COOKIE_KEY, 'true');
    this.showBanner = false;
    this.gtm.init();
  }

  reject() {
    localStorage.setItem(COOKIE_KEY, 'false');
    this.showBanner = false;
  }

}
