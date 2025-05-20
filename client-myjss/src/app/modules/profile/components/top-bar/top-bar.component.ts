import { Component, ElementRef, HostListener, Input, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { AppService } from '../../../../libs/app.service';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { MenuItem } from '../../../general/model/MenuItem';
import { initTooltips } from '../../../my-account/components/orders/orders.component';
import { AccountMenuItem, MAIN_ITEM_ACCOUNT, MAIN_ITEM_DASHBOARD } from '../../../my-account/model/AccountMenuItem';
import { Responsable } from '../../model/Responsable';
import { LoginService } from '../../services/login.service';

declare var bootstrap: any;

@Component({
  selector: 'top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css'],
  standalone: false
})
export class TopBarComponent implements OnInit {

  @Input() isForQuotationNavbar: boolean = false;
  @ViewChild('navbarCollapseRef', { static: false }) navbarCollapse!: ElementRef;

  logoJss: string = '/assets/images/white-logo-myjss.svg';
  logoJssDark: string = '/assets/images/dark-logo-myjss.svg';
  paymentMethods: string = '/assets/images/payment-methods.png';
  map: string = '/assets/images/map.png';
  anonymousConnexion: string = '/assets/images/anonymous.svg';

  currentUser: Responsable | undefined;

  services: MenuItem[] = this.appService.getAllServicesMenuItems();
  companyItems: MenuItem[] = this.appService.getAllCompanyMenuItems();
  tools: MenuItem[] = this.appService.getAllToolsMenuItems();
  myAccountItems: AccountMenuItem[] = this.appService.getAllAccountMenuItems();

  MAIN_ITEM_ACCOUNT = MAIN_ITEM_ACCOUNT;
  MAIN_ITEM_DASHBOARD = MAIN_ITEM_DASHBOARD;

  isNavbarCollapsed: boolean = false;

  constructor(private loginService: LoginService,
    private appService: AppService,
    private router: Router,
    private eRef: ElementRef
  ) { }

  capitalizeName = capitalizeName;

  ngOnInit() {
    this.loginService.currentUserChangeMessage.subscribe(response => {
      if (!response)
        this.currentUser = undefined;
      else
        this.refreshCurrentUser()
      initTooltips('bottom');
    });
  }

  isDisplaySecondHeader() {
    let url: String = this.router.url;
    if (url)
      if (url.indexOf("quotation") >= 0 || url.indexOf("account") >= 0)
        return false;

    return !this.isNavbarCollapsed;
  }

  refreshCurrentUser() {
    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
    });
  }

  openProduct(event: any) {
    this.appService.openRoute(event, "product/" + "", undefined);
  }

  openOffers(event: any) {
    this.appService.openRoute(event, "offers/" + "", undefined);
  }

  openNewOrder(event: any) {
    this.appService.openRoute(event, "order/new/" + "", undefined);
  }

  navbarCollapsed() {
    this.isNavbarCollapsed = true;
  }

  navbarUncollapsed() {
    this.isNavbarCollapsed = false;
  }

  @HostListener('document:click', ['$event'])
  handleClickOutside(event: MouseEvent): void {
    const targetElement = event.target as HTMLElement;
    if (
      this.navbarCollapse &&
      this.navbarCollapse.nativeElement.getAttribute('aria-expanded') == 'true' &&
      !this.eRef.nativeElement.contains(targetElement)
    ) {
      this.navbarCollapse.nativeElement.click();
    }
  }

}
