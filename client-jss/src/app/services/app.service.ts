import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { environment } from '../../environments/environment';
import { Toast } from '../libs/toast/Toast';
import { AccountMenuItem, MAIN_ITEM_ACCOUNT, MAIN_ITEM_DASHBOARD } from '../main/model/AccountMenuItem';
import { PlatformService } from './platform.service';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  toasts: Toast[] = [];
  private toastSource = new BehaviorSubject<Toast[]>(this.toasts);

  constructor(
    private router: Router, private platformService: PlatformService
  ) { }

  /**
   * Open given route taking account of user action to do it (simple click, ctrl + click, middle click)
   * @param event : click event given by angular
   * @param route  : route to open
   * @param sameWindowEndFonction : function to execute at the end of open with simple click
   * @returns
   */
  openRoute(event: any, route: string, sameWindowEndFonction: any) {
    const win = this.platformService.getNativeWindow();
    if (event && (event.ctrlKey || event.button && event.button == "1") && win) {
      win.open(location.origin + "/" + route, "_blank");
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

  openMyJssRoute(event: any, route: string, inNewWindows = true) {
    const win = this.platformService.getNativeWindow();
    if (win)
      win.open(environment.frontendMyJssUrl + route, inNewWindows ? "_blank" : "_self");
  }

  openLinkedinJssPage() {
    const win = this.platformService.getNativeWindow();
    if (win)
      win.open("https://www.linkedin.com/company/journal-special-des-societes/", "_blank");
  }

  openInstagramJssPage() {
    const win = this.platformService.getNativeWindow();
    if (win)
      win.open("https://www.instagram.com/journalspecialdessocietes/", "_blank");
  }

  openFacebookJssPage() {
    const win = this.platformService.getNativeWindow();
    if (win)
      win.open("https://www.facebook.com/Journal.Special.des.Societes/", "_blank");
  }

  getAllAccountMenuItems(): AccountMenuItem[] {
    let menuItem = [] as AccountMenuItem[];
    menuItem.push({ mainItem: MAIN_ITEM_ACCOUNT, label: "Mon compte", iconClass: "ai-user-check", route: "/account/settings" } as AccountMenuItem);
    menuItem.push({ mainItem: MAIN_ITEM_ACCOUNT, label: "Relevé de compte", iconClass: "ai-wallet", route: "/account/closure" } as AccountMenuItem);
    menuItem.push({ mainItem: MAIN_ITEM_ACCOUNT, label: "Préférences de communication", iconClass: "ai-messages", route: "/account/communication-preference" } as AccountMenuItem);
    menuItem.push({ mainItem: MAIN_ITEM_DASHBOARD, label: "Tableau de bord", iconClass: "ai-chart", route: "/account/overview" } as AccountMenuItem);
    menuItem.push({ mainItem: MAIN_ITEM_DASHBOARD, label: "Devis", iconClass: "ai-slider", route: "/account/quotations" } as AccountMenuItem);
    menuItem.push({ mainItem: MAIN_ITEM_DASHBOARD, label: "Commandes", iconClass: "ai-cart", route: "/account/orders" } as AccountMenuItem);
    menuItem.push({ mainItem: MAIN_ITEM_DASHBOARD, label: "Affaires", iconClass: "ai-briefcase", route: "/account/affaires" } as AccountMenuItem);

    return menuItem;
  }

}
