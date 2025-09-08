import { Component, Input, OnInit } from '@angular/core';
import { environment } from '../../../../../environments/environment';
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

  logoJss: string = '/assets/img/others/myjss-logos/white-logo-myjss.svg';
  paymentMethods: string = '/assets/img/others/payment-methods.png';
  map: string = '/assets/img/others/map.png';

  @Input() isInNavbar: boolean = false;

  services!: MenuItem[];
  companyItems!: MenuItem[];
  tools!: MenuItem[];
  frontendJssUrl = environment.frontendJssUrl;


  constructor(
    private appService: AppService
  ) { }

  ngOnInit() {
    this.services = this.appService.getAllServicesMenuItems();
    this.companyItems = this.appService.getAllCompanyMenuItems();
    this.tools = this.appService.getAllToolsMenuItems();
  }
}
