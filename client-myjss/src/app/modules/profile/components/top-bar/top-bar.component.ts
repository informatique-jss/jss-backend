import { ChangeDetectorRef, Component, ElementRef, HostListener, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbCollapseModule, NgbDropdownModule, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { MenuItem } from '../../../general/model/MenuItem';
import { AppService } from '../../../main/services/app.service';
import { AvatarComponent } from '../../../miscellaneous/components/avatar/avatar.component';
import { AccountMenuItem, MAIN_ITEM_ACCOUNT, MAIN_ITEM_DASHBOARD } from '../../../my-account/model/AccountMenuItem';
import { CustomerOrderService } from '../../../my-account/services/customer.order.service';
import { QuotationService } from '../../../my-account/services/quotation.service';
import { Responsable } from '../../model/Responsable';
import { LoginService } from '../../services/login.service';
import { ResponsableService } from '../../services/responsable.service';
import { SearchComponent } from '../search/search.component';

declare var bootstrap: any;

@Component({
  selector: 'top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, AvatarComponent, NgbDropdownModule, NgbCollapseModule, NgbDropdownModule]
})
export class TopBarComponent implements OnInit {

  @Input() isForQuotationNavbar: boolean = false;

  logoJss: string = '/assets/images/white-logo-myjss.svg';
  logoJssDark: string = '/assets/images/dark-logo-myjss.svg';
  paymentMethods: string = '/assets/images/payment-methods.png';
  map: string = '/assets/images/map.png';
  anonymousConnexion: string = '/assets/images/anonymous.svg';

  currentUser: Responsable | undefined;
  dropdownOpen = false;
  userScope: Responsable[] = [];
  groupedAccounts: { denomination: string, accounts: Responsable[] }[] = [];

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
    private modalService: NgbModal,
    private responsableService: ResponsableService,
    private quotationService: QuotationService,
    private orderService: CustomerOrderService,
    private cdr: ChangeDetectorRef
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
      this.cdr.detectChanges();

      this.responsableService.getPotentialUserScope().subscribe(response => {
        this.userScope = response.filter(u => u.id != this.currentUser!.id);

        const groupedMap = new Map<string, Responsable[]>();

        for (const responsable of this.userScope) {
          const key = responsable.tiers.denomination ? responsable.tiers.denomination : (responsable.tiers.firstname + ' ' + responsable.tiers.lastname);
          if (!groupedMap.has(key)) {
            groupedMap.set(key, []);
          }
          groupedMap.get(key)!.push(responsable);
        }

        this.groupedAccounts = Array.from(groupedMap.entries())
          .map(([denomination, accounts]) => ({
            denomination,
            accounts: accounts.sort((a, b) => {
              const aFirst = (a.firstname || '').toLowerCase();
              const bFirst = (b.firstname || '').toLowerCase();
              const aLast = (a.lastname || '').toLowerCase();
              const bLast = (b.lastname || '').toLowerCase();

              return aFirst.localeCompare(bFirst) || aLast.localeCompare(bLast);
            }),
          }))
          .sort((a, b) => a.denomination.localeCompare(b.denomination));
      })
    });
  }

  switchAccount(account: Responsable) {
    this.appService.showLoadingSpinner();
    this.loginService.switchUser(account.id).subscribe(response => {
      // switch current quotation
      if (this.quotationService.getCurrentDraftQuotationId() || this.orderService.getCurrentDraftOrderId()) {
        this.loginService.getCurrentUser().subscribe(currentUser => {
          if (this.quotationService.getCurrentDraftQuotationId()) {
            this.quotationService.switchResponsableForQuotation(parseInt(this.quotationService.getCurrentDraftQuotationId()!), account).subscribe(response => {
              this.refreshAfterSwitch();
            });
          } else if (this.orderService.getCurrentDraftOrderId()) {
            this.orderService.switchResponsableForOrder(parseInt(this.orderService.getCurrentDraftOrderId()!), account).subscribe(response => {
              this.refreshAfterSwitch();
            });
          }
        });
      } else {
        this.refreshAfterSwitch();
      }
    })
  }

  refreshAfterSwitch() {
    const currentUrl = this.router.url;
    this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
      this.appService.hideLoadingSpinner();
      this.router.navigate([currentUrl]);
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
