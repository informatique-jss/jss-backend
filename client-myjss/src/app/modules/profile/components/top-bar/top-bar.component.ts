import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';
import { AccountMenuItem, MAIN_ITEM_ACCOUNT, MAIN_ITEM_DASHBOARD } from '../../../my-account/model/AccountMenuItem';
import { IOsirisUser } from '../../model/IOsirisUser';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css']
})
export class TopBarComponent implements OnInit {

  logoJss: string = '/assets/images/logo.png';
  currentUser: IOsirisUser | undefined;


  MAIN_ITEM_ACCOUNT = MAIN_ITEM_ACCOUNT;
  MAIN_ITEM_DASHBOARD = MAIN_ITEM_DASHBOARD;
  menuItems: AccountMenuItem[] = this.appService.getAllAccountMenuItems();

  constructor(private loginService: LoginService,
    private appService: AppService,
  ) { }

  ngOnInit() {
    this.loginService.currentUserChangeMessage.subscribe(response => {
      if (!response)
        this.currentUser = undefined;
      else
        this.refreshCurrentUser()
    });
  }

  refreshCurrentUser() {
    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
      console.log(this.currentUser);
    });
  }

}
