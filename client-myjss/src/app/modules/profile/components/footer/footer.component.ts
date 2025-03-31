import { Component, Input, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';
import { MenuItem } from '../../../general/model/MenuItem';

@Component({
    selector: 'main-footer',
    templateUrl: './footer.component.html',
    styleUrls: ['./footer.component.css'],
    standalone: false
})
export class FooterComponent implements OnInit {
  logoJss: string = '/assets/images/white-logo-myjss.svg';
  paymentMethods: string = '/assets/images/payment-methods.png';
  map: string = '/assets/images/map.png';

  @Input() isInNavbar: boolean = false;

  services: MenuItem[] = this.appService.getAllServicesMenuItems();
  companyItems: MenuItem[] = this.appService.getAllCompanyMenuItems();
  tools: MenuItem[] = this.appService.getAllToolsMenuItems();


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

  openNewOrder(event: any) {
  }

  openConfidentialityPolitic(event: any) {
    this.appService.openRoute(event, "confidentiality", undefined);
  }
}
