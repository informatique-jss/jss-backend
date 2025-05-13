import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AppService {


  constructor(
    private router: Router,
  ) { }

  /**
   * Open given route taking account of user action to do it (simple click, ctrl + click, middle click)
   * @param event : click event given by angular
   * @param route  : route to open
   * @param sameWindowEndFonction : function to execute at the end of open with simple click
   * @returns
   */
  openRoute(event: any, route: string, sameWindowEndFonction: any) {
    if (event && (event.ctrlKey || event.button && event.button == "1")) {
      window.open(location.origin + "/" + route, "_blank");
    } else {
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
        this.router.navigate(['/' + route])
      });
      if (sameWindowEndFonction)
        sameWindowEndFonction();
    }
    return;
  }

  openMyJssRoute(event: any, route: string) {
    console.log(environment.frontendMyJssUrl + route);
    window.open(environment.frontendMyJssUrl + route);
  }

  openLinkedinJssPage() {
    window.open("https://www.linkedin.com/company/journal-special-des-societes/_blank");
  }

  openInstagramJssPage() {
    window.open("https://www.instagram.com/journalspecialdessocietes/_blank");
  }

  openFacebookJssPage() {
    window.open("https://www.facebook.com/Journal.Special.des.Societes/_blank");
  }

}
