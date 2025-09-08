import { Component, ElementRef, HostListener, OnInit, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { MY_JSS_HOME_ROUTE, MY_JSS_NEW_ANNOUNCEMENT_ROUTE, MY_JSS_NEW_FORMALITY_ROUTE, MY_JSS_SIGN_IN_ROUTE } from '../../../libs/Constants';
import { capitalizeName } from '../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { PlatformService } from '../../../services/platform.service';
import { AccountMenuItem, MAIN_ITEM_ACCOUNT, MAIN_ITEM_DASHBOARD } from '../../model/AccountMenuItem';
import { IndexEntity } from '../../model/IndexEntity';
import { JssCategory } from '../../model/JssCategory';
import { MenuItem } from '../../model/MenuItem';
import { PublishingDepartment } from '../../model/PublishingDepartment';
import { Responsable } from '../../model/Responsable';
import { DepartmentService } from '../../services/department.service';
import { IndexEntityService } from '../../services/index.entity.service';
import { JssCategoryService } from '../../services/jss.category.service';
import { LoginService } from '../../services/login.service';
import { AvatarComponent } from '../avatar/avatar.component';

@Component({
  selector: 'main-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  imports: [
    SHARED_IMPORTS,
    AvatarComponent,
    NgbTooltipModule
  ],
  standalone: true
})
export class HeaderComponent implements OnInit {

  departments: PublishingDepartment[] = [];
  categories: JssCategory[] = [];
  categoriesByOrder: JssCategory[] = [];
  debounce: any;
  searchInProgress: boolean = false;

  searchText: string = "";
  indexedEntities: IndexEntity[] | undefined;
  searchObservableRef: Subscription | undefined;

  searchModalInstance: any | undefined;

  currentUser: Responsable | undefined;

  capitalizeName = capitalizeName;

  isMobileMenuOpen: boolean = false;
  showDepartments: boolean = false;

  myAccountItems: AccountMenuItem[] = [];
  MAIN_ITEM_ACCOUNT = MAIN_ITEM_ACCOUNT;
  MAIN_ITEM_DASHBOARD = MAIN_ITEM_DASHBOARD;
  MY_JSS_SIGN_IN_ROUTE = MY_JSS_SIGN_IN_ROUTE;
  frontendMyJssUrl = environment.frontendMyJssUrl;
  MY_JSS_NEW_ANNOUNCEMENT_ROUTE = MY_JSS_NEW_ANNOUNCEMENT_ROUTE;
  MY_JSS_NEW_FORMALITY_ROUTE = MY_JSS_NEW_FORMALITY_ROUTE;
  MY_JSS_HOME_ROUTE = MY_JSS_HOME_ROUTE;

  constructor(
    private router: Router,
    private departmentService: DepartmentService,
    private jssCategoryService: JssCategoryService,
    private appService: AppService,
    private indexEntityService: IndexEntityService,
    private loginService: LoginService,
    private modalService: NgbModal,
    private eRef: ElementRef,
    private plaformService: PlatformService,
  ) { }

  ngOnInit() {
    this.myAccountItems = this.appService.getAllAccountMenuItems();

    if (this.plaformService.isBrowser())
      this.loginService.getCurrentUser().subscribe(response => {
        this.currentUser = response;
      })
    this.departmentService.getAvailablePublishingDepartments().subscribe(departments => {
      this.departments = departments
        .slice()
        .sort((a, b) => {
          const aCode = parseInt(a.code);
          const bCode = parseInt(b.code);

          const aValid = !isNaN(aCode);
          const bValid = !isNaN(bCode);

          if (!aValid && !bValid) return 0;
          if (!aValid) return 1;
          if (!bValid) return -1;

          return aCode - bCode;
        });
    });
    this.jssCategoryService.getAvailableJssCategories().subscribe(categories => {
      this.categories = categories.sort((a: JssCategory, b: JssCategory) => b.categoryOrder - a.categoryOrder);
      this.categoriesByOrder = this.categories.slice(0, 4);
    });
  }

  dropdownOpen = false;

  toggleDropdown(event: Event): void {
    event.preventDefault();
    this.dropdownOpen = !this.dropdownOpen;
  }

  @HostListener('document:click', ['$event'])
  handleClickOutside(event: MouseEvent): void {
    this.attempToCloseNavbar(false, event);
  }

  attempToCloseNavbar(force: boolean = true, event: MouseEvent | any) {
    if (force ||
      this.dropdownOpen &&
      !this.eRef.nativeElement.contains(event.target as HTMLElement)
    ) {
      this.dropdownOpen = false;
    }
  }



  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
    this.showDepartments = false;
  }

  toggleDepartmentDropdown(): void {
    this.showDepartments = !this.showDepartments;
  }

  openMyJssRoute(item: MenuItem) {
    this.appService.openMyJssRoute(undefined, item.route, false);
  }

  openMyJssRouteUrl(item: string) {
    this.appService.openMyJssRoute(undefined, item, false);
  }

  disconnect() {
    this.loginService.signOut().subscribe(response => {
      this.currentUser = undefined;
    })
  }

  displaySearchModal(content: TemplateRef<any>) {
    if (this.searchModalInstance) {
      return;
    }

    this.searchModalInstance = this.modalService.open(content, {
      backdrop: 'static',
      modalDialogClass: 'modal-fullscreen'
    });

    this.searchModalInstance.shown.subscribe(() => {
      const input = document.querySelector('input[name="search"]') as HTMLInputElement;
      if (input) {
        input.focus();
      }
    });

    this.searchModalInstance.result.finally(() => {
      this.searchModalInstance = undefined;
      this.indexedEntities = [];
      this.searchText = "";
      if (this.searchObservableRef)
        this.searchObservableRef.unsubscribe();
    });
  }

  hideSearchModal() {
    if (this.searchModalInstance) {
      this.searchModalInstance.dismiss('manual-close');
      this.searchModalInstance = undefined;
      this.indexedEntities = [];
      this.searchText = "";
      if (this.searchObservableRef)
        this.searchObservableRef.unsubscribe();
    }
  }

  public getCurrentRoute = () => {
    return this.router.url;
  }

  openSubscribe(event: any) {
    this.isMobileMenuOpen = false;
  }

  openSignIn() {
    this.isMobileMenuOpen = false;
  }

  openCategoryPosts() {
    this.isMobileMenuOpen = false;
    this.hideSearchModal();
  }

  openDepartment() {
    this.isMobileMenuOpen = false;
  }

  openPremium() {
    this.isMobileMenuOpen = false;
  }

  openPremiumPosts() {
    this.isMobileMenuOpen = false;
    // TODO
  }

  openSearchAnnouncement(event: any) {
    this.isMobileMenuOpen = false;
  }


  openMyJss() {
    this.isMobileMenuOpen = false;
  }

  searchEntities() {
    clearTimeout(this.debounce);
    this.indexedEntities = [];
    this.debounce = setTimeout(() => {
      this.globalSearch();
    }, 500);
  }

  clearSearch() {
    this.searchText = '';
    this.indexedEntities = [];
  }

  globalSearch() {
    if (this.searchObservableRef)
      this.searchObservableRef.unsubscribe();

    this.searchInProgress = true;
    if (this.searchText && this.searchText.length > 2)
      this.searchObservableRef = this.indexEntityService.globalSearchForPost(this.searchText).subscribe(response => {
        this.indexedEntities = [];
        for (let foundEntity of response) {
          if (foundEntity && foundEntity.text) {
            foundEntity.text = JSON.parse((foundEntity.text as string));
            this.indexedEntities.push(foundEntity);
          }
        }
        this.searchInProgress = false;
      })
  }

  openPost() {
    this.hideSearchModal();
  }

}
