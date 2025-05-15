import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { environment } from '../../environments/environment';
import { Toast } from '../libs/toast/Toast';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  toasts: Toast[] = [];
  private toastSource = new BehaviorSubject<Toast[]>(this.toasts);

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

  displayToast(message: string, isError: boolean, title: string, delayInMili: number) {
    let newToast = { isError: isError, message: message, title: title } as Toast;
    this.toasts.push(newToast);
    this.toastSource.next(this.toasts);
    if (this.toasts.indexOf(newToast) >= 0)
      setTimeout(() => {
        this.toasts.splice(this.toasts.indexOf(newToast), 1);
        this.toastSource.next(this.toasts);
      }
        , delayInMili);
  }

  openMyJssRoute(event: any, route: string) {
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
