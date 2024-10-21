import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { initTooltips } from '../../../my-account/components/orders/orders.component';
import { AccountMenuItem, MAIN_ITEM_ACCOUNT, MAIN_ITEM_DASHBOARD } from '../../../my-account/model/AccountMenuItem';
import { Responsable } from '../../model/Responsable';
import { LoginService } from '../../services/login.service';

declare var bootstrap: any;

@Component({
  selector: 'top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css']
})
export class TopBarComponent implements OnInit {

  logoJss: string = '/assets/images/logo.png';
  currentUser: Responsable | undefined;


  MAIN_ITEM_ACCOUNT = MAIN_ITEM_ACCOUNT;
  MAIN_ITEM_DASHBOARD = MAIN_ITEM_DASHBOARD;
  menuItems: AccountMenuItem[] = this.appService.getAllAccountMenuItems();

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

}
