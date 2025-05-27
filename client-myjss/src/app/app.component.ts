import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { SHARED_IMPORTS } from './libs/SharedImports';
import { ConstantService } from './modules/main/services/constant.service';
import { GtmService } from './modules/main/services/gtm.service';
import { PlatformService } from './modules/main/services/platform.service';
import { ThemeService } from './modules/main/services/theme.service';
import { Responsable } from './modules/profile/model/Responsable';
import { LoginService } from './modules/profile/services/login.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,
    SHARED_IMPORTS,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  standalone: true
})
export class AppComponent {
  title = 'myjss';
  currentUser: Responsable | undefined;

  constructor(private router: Router,
    private constantService: ConstantService,
    private loginService: LoginService,
    private themeService: ThemeService,
    private gtm: GtmService,
    private platformService: PlatformService
  ) {
  }

  ngOnInit() {
    this.constantService.initConstant();

    //Init Google tag manager if in browser
    this.gtm.init();

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
      if (this.platformService.isBrowser())
        this.themeService.updateThemeFromUrl(url);
    });
  }

  refreshCurrentUser() {
    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
    });
  }

  isDisplayGreyBackground() {
    let url: String = this.router.url;
    if (url) {
      if (url.indexOf("account") >= 0 && url.indexOf("signin") <= 0)
        return true;
      if (url.indexOf("quotation") > 0)
        return true
    }
    return false;
  }

  isDisplayLightHeader() {
    let url: String = this.router.url;
    if (url) {
      if (url.indexOf("account") >= 0 && url.indexOf("signin") <= 0)
        return true;
      if (url.indexOf("quotation") >= 0)
        return true;
    }
    return false;
  }

  isDisplayFooter() {
    let url: String = this.router.url;
    if (url) {
      if (url.indexOf("signin") >= 0)
        return false;
      if (url.indexOf("quotation") >= 0)
        return false;
    }
    return true;
  }

}
