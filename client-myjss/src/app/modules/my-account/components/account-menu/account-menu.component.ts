import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';
import { IOsirisUser } from '../../../profile/model/IOsirisUser';
import { LoginService } from '../../../profile/services/login.service';
import { AccountMenuItem, MAIN_ITEM_ACCOUNT, MAIN_ITEM_DASHBOARD } from '../../model/AccountMenuItem';

@Component({
  selector: 'account-menu',
  templateUrl: './account-menu.component.html',
  styleUrls: ['./account-menu.component.css']
})
export class AccountMenuComponent implements OnInit {

  menuItems: AccountMenuItem[] = this.appService.getAllAccountMenuItems();

  MAIN_ITEM_ACCOUNT = MAIN_ITEM_ACCOUNT;
  MAIN_ITEM_DASHBOARD = MAIN_ITEM_DASHBOARD;

  currentUser: IOsirisUser | undefined;

  constructor(private appService: AppService,
    private loginService: LoginService,
  ) { }

  ngOnInit() {
    this.loginService.getCurrentUser().subscribe(reponse => this.currentUser = reponse);
  }

}
