import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { Toast } from '../../../libs/toast/Toast';
import { MenuItem } from '../../general/model/MenuItem';
import { AccountMenuItem, MAIN_ITEM_ACCOUNT, MAIN_ITEM_DASHBOARD } from '../../my-account/model/AccountMenuItem';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  toasts: Toast[] = [];
  private toastSource = new BehaviorSubject<Toast[]>(this.toasts);
  private loadingSpinner = new BehaviorSubject<boolean>(false);
  readonly loadingSpinnerObservable = this.loadingSpinner.asObservable();

  constructor(
    private router: Router,
  ) { }

  showLoadingSpinner(): void {
    this.loadingSpinner.next(true);
  }

  hideLoadingSpinner(): void {
    this.loadingSpinner.next(false);
  }

  displayToast(message: string, isError: boolean, title: string, delayInMili: number) {
    let newToast = { isError: isError, message: message, title: title, delay: delayInMili } as Toast;
    this.toasts.push(newToast);
    this.toastSource.next(this.toasts);
    if (this.toasts.indexOf(newToast) >= 0)
      setTimeout(() => {
        this.toasts.splice(this.toasts.indexOf(newToast), 1);
        this.toastSource.next(this.toasts);
      }
        , delayInMili);
  }

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
      this.router.navigate(['/' + route])
      if (sameWindowEndFonction)
        sameWindowEndFonction();
    }
    return;
  }

  openJssRoute(event: any, route: string, inNewWindows = true) {
    window.open(environment.frontendJssUrl + route, inNewWindows ? "_blank" : "_self");
  }

  openOsirisV2Route(event: any, route: string, inNewWindows = true) {
    window.open(environment.frontendOsirisV2Url + route, inNewWindows ? "_blank" : "_self");
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

  getAllAccountMenuItems(): AccountMenuItem[] {
    let menuItem = [] as AccountMenuItem[];
    menuItem.push({ mainItem: MAIN_ITEM_DASHBOARD, label: "Vue d'ensemble", iconClass: "ai-chart", route: "/account/overview" } as AccountMenuItem);
    menuItem.push({ mainItem: MAIN_ITEM_DASHBOARD, label: "Devis", iconClass: "ai-slider", route: "/account/quotations" } as AccountMenuItem);
    menuItem.push({ mainItem: MAIN_ITEM_DASHBOARD, label: "Commandes", iconClass: "ai-cart", route: "/account/orders" } as AccountMenuItem);
    menuItem.push({ mainItem: MAIN_ITEM_DASHBOARD, label: "Relevé de compte", iconClass: "ai-wallet", route: "/account/closure" } as AccountMenuItem);
    menuItem.push({ mainItem: MAIN_ITEM_DASHBOARD, label: "Affaires", iconClass: "ai-briefcase", route: "/account/affaires" } as AccountMenuItem);
    menuItem.push({ mainItem: MAIN_ITEM_ACCOUNT, label: "Mon compte", iconClass: "ai-user-check", route: "/account/settings" } as AccountMenuItem);
    menuItem.push({ mainItem: MAIN_ITEM_ACCOUNT, label: "Contenu suivis", iconClass: "ai-bookmark", route: "/account/reading-folders" } as AccountMenuItem);
    menuItem.push({ mainItem: MAIN_ITEM_ACCOUNT, label: "Newsletter et alertes", iconClass: "ai-messages", route: "/account/communication-preference" } as AccountMenuItem);
    return menuItem;
  }

  getAllQuotationMenuItems(): MenuItem[] {
    let menuItem = [] as MenuItem[];
    menuItem.push({ label: "1. Identification de l'entreprise", route: "/quotation/identification" } as MenuItem);
    menuItem.push({ label: "2. Choix des services", route: "/quotation/services-selection" } as MenuItem);
    menuItem.push({ label: "3. Informations requises", route: "/quotation/required-information" } as MenuItem);
    menuItem.push({ label: "4. Récapitulatif", route: "/quotation/checkout" } as MenuItem);
    return menuItem;
  }

  getAllServicesMenuItems(): MenuItem[] {
    let menuItem = [] as MenuItem[];
    menuItem.push({ label: "Annonces légales", iconClass: "ai-user-check", route: "/services/announcement" } as MenuItem);
    menuItem.push({ label: "Formalités légales", iconClass: "ai-wallet", route: "/services/formality" } as MenuItem);
    menuItem.push({ label: "Apostilles-Légalisation", iconClass: "ai-chart", route: "/services/apostille" } as MenuItem);
    menuItem.push({ label: "Domiciliation", iconClass: "ai-slider", route: "/services/domiciliation" } as MenuItem);
    menuItem.push({ label: "Fourniture de documents", iconClass: "ai-cart", route: "/services/document" } as MenuItem);
    return menuItem;
  }

  getAllCompanyMenuItems(): MenuItem[] {
    let menuItem = [] as MenuItem[];
    menuItem.push({ label: "À propos", iconClass: "ai-user-check", route: "/company/about-us" } as MenuItem);
    menuItem.push({ label: "Notre histoire", iconClass: "ai-wallet", route: "/company/our-story" } as MenuItem);
    menuItem.push({ label: "Nos équipes", iconClass: "ai-chart", route: "/company/our-team" } as MenuItem);
    menuItem.push({ label: "Nous rejoindre", iconClass: "ai-slider", route: "/company/join-us" } as MenuItem);

    return menuItem;
  }

  getAllToolsMenuItems(): MenuItem[] {
    let menuItem = [] as MenuItem[];
    menuItem.push({ label: "Nos fiches pratiques", iconClass: "ai-wallet", route: "/tools/practical-sheets" } as MenuItem);
    menuItem.push({ label: "Pièces obligatoires", iconClass: "ai-user-check", route: "/tools/mandatory-documents" } as MenuItem);
    menuItem.push({ label: "Webinaires", iconClass: "ai-chart", route: "/tools/webinars" } as MenuItem);
    //menuItem.push({ label: "Nos exclus", iconClass: "ai-slider", route: "/tools/exclusives" } as MenuItem);

    return menuItem;
  }

}
