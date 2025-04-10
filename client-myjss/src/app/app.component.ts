import { AfterViewInit, Component } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';
import { ConstantService } from './libs/constant.service';
import { ThemeService } from './libs/theme.service';
import { Responsable } from './modules/profile/model/Responsable';
import { LoginService } from './modules/profile/services/login.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  standalone: false
})

export class AppComponent implements AfterViewInit {
  title = 'myjss';
  currentUser: Responsable | undefined;

  constructor(private router: Router,
    private constantService: ConstantService,
    private loginService: LoginService,
    private themeService: ThemeService,
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
    this.router.events.pipe(
      filter((event): event is NavigationEnd => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      const url = event.urlAfterRedirects;
      this.themeService.updateThemeFromUrl(url);
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
