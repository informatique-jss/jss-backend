import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { MenuItem } from '../../../general/model/MenuItem';
import { initTooltips } from '../../../my-account/components/orders/orders.component';
import { Responsable } from '../../model/Responsable';
import { LoginService } from '../../services/login.service';

declare var bootstrap: any;

@Component({
  selector: 'top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css']
})
export class TopBarComponent implements OnInit {

  logoJss: string = '/assets/images/white-logo-myjss.svg';
  paymentMethods: string = '/assets/images/payment-methods.png';
  map: string = '/assets/images/map.png';
  anonymousConnexion: string = '/assets/images/anonymous.svg';

  currentUser: Responsable | undefined;

  services: MenuItem[] = this.appService.getAllServicesMenuItems();
  companyItems: MenuItem[] = this.appService.getAllCompanyMenuItems();
  tools: MenuItem[] = this.appService.getAllToolsMenuItems();

  isNavbarCollapsed: boolean = false;

  constructor(private loginService: LoginService,
    private appService: AppService,
  ) { }

  capitalizeName = capitalizeName;

  ngOnInit() {
    this.loginService.currentUserChangeMessage.subscribe(response => {
      if (!response)
        this.currentUser = undefined;
      else
        this.refreshCurrentUser()
      initTooltips('bottom');
    });
  }

  refreshCurrentUser() {
    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
    });
  }

  openProduct(event: any) {
    this.appService.openRoute(event, "product/" + "", undefined);
  }

  openOffers(event: any) {
    this.appService.openRoute(event, "offers/" + "", undefined);
  }

  openNewOrder(event: any) {
    this.appService.openRoute(event, "order/new/" + "", undefined);
  }

  navbarCollapsed() {
    this.isNavbarCollapsed = true;
  }

  navbarUncollapsed() {
    this.isNavbarCollapsed = false;
  }

}
