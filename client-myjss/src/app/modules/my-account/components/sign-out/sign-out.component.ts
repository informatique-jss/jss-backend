import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { GoogleAnalyticsService } from '../../../main/services/googleAnalytics.service';
import { LoginService } from '../../../profile/services/login.service';
import { QuotationService } from '../../services/quotation.service';

@Component({
  selector: 'app-sign-out',
  templateUrl: './sign-out.component.html',
  styleUrls: ['./sign-out.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SignOutComponent implements OnInit {

  constructor(private loginService: LoginService,
    private appService: AppService,
    private googleAnalyticsService: GoogleAnalyticsService,
    private quotationService: QuotationService) { }

  ngOnInit() {
    this.googleAnalyticsService.trackLoginLogout("logout", "sign-out", "my-account").subscribe();
    this.loginService.signOut().subscribe(response => {
      this.quotationService.cleanStorageData();
      this.appService.openRoute(undefined, '/', undefined);
    })
  }
}