import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';
import { LoginService } from '../../../profile/services/login.service';

@Component({
    selector: 'app-sign-out',
    templateUrl: './sign-out.component.html',
    styleUrls: ['./sign-out.component.css'],
    standalone: false
})
export class SignOutComponent implements OnInit {

  constructor(private loginService: LoginService, private appService: AppService) { }

  ngOnInit() {
    this.loginService.signOut().subscribe(response => {
      this.appService.openRoute(undefined, '/', undefined);
    })
  }

}
