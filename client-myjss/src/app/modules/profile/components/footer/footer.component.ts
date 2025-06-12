import { Component, Input, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { MenuItem } from '../../../general/model/MenuItem';
import { AppService } from '../../../main/services/app.service';

@Component({
  selector: 'main-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class FooterComponent implements OnInit {

  logoJss: string = '/assets/images/white-logo-myjss.svg';
  paymentMethods: string = '/assets/images/payment-methods.png';
  map: string = '/assets/images/map.png';

  @Input() isInNavbar: boolean = false;

  services!: MenuItem[];
  companyItems!: MenuItem[];
  tools!: MenuItem[];


  constructor(
    private appService: AppService
  ) { }

  ngOnInit() {
    this.services = this.appService.getAllServicesMenuItems();
    this.companyItems = this.appService.getAllCompanyMenuItems();
    this.tools = this.appService.getAllToolsMenuItems();
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

  openLinkedin() {
    this.appService.openLinkedinJssPage();
  }

  openInstagram() {
    this.appService.openInstagramJssPage();
  }

  openFacebook() {
    this.appService.openFacebookJssPage();
  }

  openJssRoute(event: any) {
    this.appService.openJssRoute(event, "");
  }
}
