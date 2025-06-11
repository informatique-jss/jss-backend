import { Component, ElementRef, HostListener, OnInit, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { MY_JSS_HOME_ROUTE, MY_JSS_NEW_ANNOUNCEMENT_ROUTE, MY_JSS_NEW_FORMALITY_ROUTE, MY_JSS_SIGN_IN_ROUTE } from '../../../libs/Constants';
import { capitalizeName } from '../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
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

  constructor(
    private router: Router,
    private departmentService: DepartmentService,
    private jssCategoryService: JssCategoryService,
    private appService: AppService,
    private indexEntityService: IndexEntityService,
    private loginService: LoginService,
    private modalService: NgbModal,
    private eRef: ElementRef,
  ) { }

  ngOnInit() {
    this.myAccountItems = this.appService.getAllAccountMenuItems();

    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
    })
    this.departmentService.getAvailablePublishingDepartments().subscribe(departments => {
      this.departments = departments.sort((a: PublishingDepartment, b: PublishingDepartment) => parseInt(a.code) - parseInt(b.code));
    });
    this.jssCategoryService.getAvailableJssCategories().subscribe(categories => {
      this.categories = categories.sort((a: JssCategory, b: JssCategory) => b.categoryOrder - a.categoryOrder);
      this.categoriesByOrder = this.categories.slice(0, 3);
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

  disconnect() {
    this.loginService.signOut().subscribe(response => {
      this.currentUser = undefined;
      this.appService.openRoute(undefined, '/', undefined);
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
    this.appService.openRoute(event, "subscription/", undefined);
  }

  openSignIn(event: any) {
    this.isMobileMenuOpen = false;
    this.appService.openMyJssRoute(event, MY_JSS_SIGN_IN_ROUTE, false);
  }

  openCategoryPosts(category: JssCategory, event: any) {
    this.isMobileMenuOpen = false;
    this.appService.openRoute(event, "post/category/" + category.slug, undefined);
    this.hideSearchModal();
  }

  openDepartment(department: PublishingDepartment, event: any) {
    this.isMobileMenuOpen = false;
    this.appService.openRoute(event, "post/department/" + department.code, undefined);
  }

  openPremiumPosts() {
    this.isMobileMenuOpen = false;
    // TODO
  }

  openPodcasts(event: any) {
    this.isMobileMenuOpen = false;
    this.appService.openRoute(event, "podcasts", undefined);
  }

  openSearchAnnouncement(event: any) {
    this.isMobileMenuOpen = false;
    this.appService.openRoute(event, "announcement/search", undefined);
  }

  openNewAnnouncement(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_NEW_ANNOUNCEMENT_ROUTE);
  }

  openNewFormality(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_NEW_FORMALITY_ROUTE);
  }

  openMyJss(event: any) {
    this.isMobileMenuOpen = false;
    this.appService.openMyJssRoute(event, MY_JSS_HOME_ROUTE);
  }

  openLinkedinJssPage() {
    this.appService.openLinkedinJssPage();
  }

  openInstagramJssPage() {
    this.appService.openInstagramJssPage();
  }

  openFacebookJssPage() {
    this.appService.openFacebookJssPage();
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

  openPost(slug: string, event: any) {
    this.appService.openRoute(event, "post/" + slug, undefined);
    this.hideSearchModal();
  }

}
