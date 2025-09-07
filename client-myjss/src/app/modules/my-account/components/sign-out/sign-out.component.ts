import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { GtmService } from '../../../main/services/gtm.service';
import { LogPayload, PageInfo } from '../../../main/services/GtmPayload';
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
    private gtmService: GtmService,
    private quotationService: QuotationService) { }

  ngOnInit() {
    this.trackLog();
    this.loginService.signOut().subscribe(response => {
      this.quotationService.cleanStorageData();
      this.appService.openRoute(undefined, '/', undefined);
    })
  }

  trackLog() {
    this.gtmService.trackLoginLogout(
      {
        type: 'logout',
        page: {
          type: 'my-account',
          name: 'sign-in'
        } as PageInfo
      } as LogPayload
    );
  }
}
