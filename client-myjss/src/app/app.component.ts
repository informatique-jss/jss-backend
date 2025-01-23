import { AfterViewInit, Component } from '@angular/core';
import { Router } from '@angular/router';
import { ConstantService } from './libs/constant.service';
import { Responsable } from './modules/profile/model/Responsable';
import { LoginService } from './modules/profile/services/login.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent implements AfterViewInit {
  title = 'myjss';
  currentUser: Responsable | undefined;

  constructor(private router: Router,
    private constantService: ConstantService,
    private loginService: LoginService,
  ) {
  }

  ngOnInit() {
    this.constantService.initConstant();
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
    });
  }

  ngAfterViewInit() {
    this.loadScript('../assets/js/theme.js');
  }

  isDisplayGreyBackground() {
    let url: String = this.router.url;
    if (url)
      if (url.indexOf("account") >= 0 && url.indexOf("signin") <= 0)
        return true;
    return false;
  }

  isDisplayHeader() {
    let url: String = this.router.url;
    if (url)
      if (url.indexOf("signin") >= 0)
        return false;
    return true;
  }

  isDisplayFooter() {
    let url: String = this.router.url;
    if (url)
      if (url.indexOf("signin") >= 0)
        return false;
    return true;
  }

  loadScript(url: string) {
    const body = <HTMLDivElement>document.body;
    const script = document.createElement('script');
    script.innerHTML = '';
    script.src = url;
    script.async = false;
    script.defer = true;
    body.appendChild(script);
  }
}
