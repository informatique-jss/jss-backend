import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Modal } from 'bootstrap';
import { Subscription } from 'rxjs';
import { MY_JSS_HOME_ROUTE, MY_JSS_NEW_ANNOUNCEMENT_ROUTE, MY_JSS_NEW_FORMALITY_ROUTE, MY_JSS_SIGN_IN_ROUTE, MY_JSS_SUBSCRIBE_ROUTE } from '../../../libs/Constants';
import { capitalizeName } from '../../../libs/FormatHelper';
import { AppService } from '../../../services/app.service';
import { ConstantService } from '../../../services/constant.service';
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

@Component({
  selector: 'main-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  standalone: false
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

  searchModalInstance: Modal | undefined;

  currentUser: Responsable | undefined;

  capitalizeName = capitalizeName;

  myAccountItems: AccountMenuItem[] = this.appService.getAllAccountMenuItems();
  MAIN_ITEM_ACCOUNT = MAIN_ITEM_ACCOUNT;
  MAIN_ITEM_DASHBOARD = MAIN_ITEM_DASHBOARD;

  constructor(
    private router: Router,
    private departmentService: DepartmentService,
    private jssCategoryService: JssCategoryService,
    private appService: AppService,
    private indexEntityService: IndexEntityService,
    private loginService: LoginService,
    private constantService: ConstantService,
    private platformService: PlatformService
  ) { }

  dropdownOpen = false;

  toggleDropdown(event: Event): void {
    event.preventDefault();
    this.dropdownOpen = !this.dropdownOpen;
  }

  handleItemClick(item: any): void {
    this.dropdownOpen = false;
    this.openMyJssRoute(item);
  }
  ngOnInit() {
    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
    })
    this.departmentService.getAvailablePublishingDepartments().subscribe(departments => {
      this.departments = departments
    });
    this.jssCategoryService.getAvailableJssCategories().subscribe(categories => {
      this.categories = categories
      this.categoriesByOrder = this.categories.sort((a: JssCategory, b: JssCategory) => b.categoryOrder - a.categoryOrder);
    });
  }

  openMyJssRoute(item: MenuItem) {
    this.appService.openMyJssRoute(undefined, item.route, false);
  }

  displaySearchModal() {
    const doc = this.platformService.getNativeDocument();
    if (doc) {
      const modalElement = doc.getElementById('searchModal');
      if (!this.searchModalInstance) {
        if (modalElement) {
          this.searchModalInstance = new Modal(modalElement, {
            backdrop: 'static'
          });
        }
      }
      this.searchModalInstance!.show();
      if (modalElement) {
        const input = modalElement.querySelector('input[name="search"]') as HTMLInputElement;
        if (input) {
          input.focus();
        }
      }
    }
  }

  hideSearchModal() {
    if (this.searchModalInstance) {
      this.searchModalInstance.hide();
      this.indexedEntities = [];
      this.searchText = "";
    }
  }

  public getCurrentRoute = () => {
    return this.router.url;
  }

  openSubscribe(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_SUBSCRIBE_ROUTE);
  }

  openSignIn(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_SIGN_IN_ROUTE, false);
  }

  openCategoryPosts(category: JssCategory, event: any) {
    this.appService.openRoute(event, "post/category/" + category.slug, undefined);
    this.hideSearchModal();
  }

  openDepartment(department: PublishingDepartment, event: any) {
    this.appService.openRoute(event, "post/department/" + department.id, undefined);
  }

  openSearchAnnouncement(event: any) {
    this.appService.openRoute(event, "announcement/search", undefined);
  }

  openNewAnnouncement(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_NEW_ANNOUNCEMENT_ROUTE);
  }

  openNewFormality(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_NEW_FORMALITY_ROUTE);
  }

  openMyJss(event: any) {
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
