import { Component, ElementRef, HostListener, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbCollapseModule, NgbDropdownModule, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { MenuItem } from '../../../general/model/MenuItem';
import { AppService } from '../../../main/services/app.service';
import { AvatarComponent } from '../../../miscellaneous/components/avatar/avatar.component';
import { AccountMenuItem, MAIN_ITEM_ACCOUNT, MAIN_ITEM_DASHBOARD } from '../../../my-account/model/AccountMenuItem';
import { Responsable } from '../../model/Responsable';
import { LoginService } from '../../services/login.service';
import { SearchComponent } from '../search/search.component';

declare var bootstrap: any;

@Component({
  selector: 'top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, AvatarComponent, NgbDropdownModule, NgbCollapseModule]
})
export class TopBarComponent implements OnInit {

  @Input() isForQuotationNavbar: boolean = false;

  logoJss: string = '/assets/images/white-logo-myjss.svg';
  logoJssDark: string = '/assets/images/dark-logo-myjss.svg';
  paymentMethods: string = '/assets/images/payment-methods.png';
  map: string = '/assets/images/map.png';
  anonymousConnexion: string = '/assets/images/anonymous.svg';

  currentUser: Responsable | undefined;

  searchModalInstance: any | undefined;

  actualUrl!: string;
  services!: MenuItem[];
  companyItems!: MenuItem[];
  tools!: MenuItem[];
  myAccountItems!: AccountMenuItem[];

  MAIN_ITEM_ACCOUNT = MAIN_ITEM_ACCOUNT;
  MAIN_ITEM_DASHBOARD = MAIN_ITEM_DASHBOARD;

  isNavbarCollapsed: boolean = true;

  constructor(private loginService: LoginService,
    private appService: AppService,
    private router: Router,
    private eRef: ElementRef,
    private modalService: NgbModal
  ) { }

  capitalizeName = capitalizeName;

  ngOnInit() {
    this.actualUrl = this.router.url;
    this.services = this.appService.getAllServicesMenuItems();
    this.companyItems = this.appService.getAllCompanyMenuItems();
    this.tools = this.appService.getAllToolsMenuItems();
    this.myAccountItems = this.appService.getAllAccountMenuItems();
    this.loginService.currentUserChangeMessage.subscribe(response => {
      if (!response)
        this.currentUser = undefined;
      else
        this.refreshCurrentUser()
    });
  }

  isDisplaySecondHeader() {
    let url: String = this.router.url;
    if (url)
      if (url.indexOf("quotation") >= 0 || url.indexOf("account") >= 0)
        return false;

    return this.isNavbarCollapsed;
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
    this.attempToCloseNavbar(false, event);
  }

  attempToCloseNavbar(force: boolean = true, event: MouseEvent | any) {
    if (force ||
      !this.isNavbarCollapsed &&
      !this.eRef.nativeElement.contains(event.target as HTMLElement)
    ) {
      this.isNavbarCollapsed = true;
    }
  }

  displaySearchModal() {
    if (this.searchModalInstance) {
      return;
    }

    this.searchModalInstance = this.modalService.open(SearchComponent, {
      size: 'lg',
    });

    this.searchModalInstance.result.finally(() => {
      this.searchModalInstance = undefined;
    });
  }

  openJssRoute(event: any) {
    this.appService.openJssRoute(event, "");
  }
}
