import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppService } from '../../../../libs/app.service';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { Responsable } from '../../../profile/model/Responsable';
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

  currentUser: Responsable | undefined;

  constructor(private appService: AppService,
    private loginService: LoginService,
    private router: Router
  ) { }

  capitalizeName = capitalizeName;

  ngOnInit() {
    this.loginService.getCurrentUser().subscribe(reponse => this.currentUser = reponse);
  }

  getCurrentRoute(): string {
    return this.router.url;
  }

}