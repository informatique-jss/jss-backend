import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';

@Component({
  selector: 'main-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {
  logoJss: string = '/assets/images/logo.png';

  constructor(
    private appService: AppService
  ) { }

  ngOnInit() {
  }

  openPage(page: string, event: any) {
    this.appService.openRoute(event, page + "/", undefined);
  }

  openContact(event: any) {
    this.appService.openRoute(event, "contact", undefined);
  }

  openJoinUs(event: any) {
    this.appService.openRoute(event, "join-us", undefined);
  }


  openPartners(event: any) {
    this.appService.openRoute(event, "partners", undefined);
  }

  openLegalMentions(event: any) {
    this.appService.openRoute(event, "legal-mentions", undefined);
  }

  openConfidentialityPolitic(event: any) {
    this.appService.openRoute(event, "confidentiality", undefined);
  }
}
