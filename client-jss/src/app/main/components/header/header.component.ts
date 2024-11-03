import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MY_JSS_NEW_ANNOUNCEMENT_ROUTE, MY_JSS_NEW_FORMALITY_ROUTE, MY_JSS_OFFERS_ROUTE, MY_JSS_SIGN_IN_ROUTE, MY_JSS_SUBSCRIBE_ROUTE } from '../../../libs/Constants';
import { AppService } from '../../../services/app.service';
import { MyJssCategory } from '../../model/MyJssCategory';
import { PublishingDepartment } from '../../model/PublishingDepartment';
import { DepartmentService } from '../../services/department.service';
import { MyJssCategoryService } from '../../services/myjss.category.service';
import { PageService } from '../../services/page.service';

@Component({
  selector: 'main-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  departments: PublishingDepartment[] = [];
  categories: MyJssCategory[] = [];
  categoriesByOrder: MyJssCategory[] = [];

  constructor(
    private router: Router,
    private departmentService: DepartmentService,
    private myJssCategoryService: MyJssCategoryService,
    private pageService: PageService,
    private appService: AppService
  ) { }

  ngOnInit() {
    this.departmentService.getAvailablePublishingDepartments().subscribe(departments => {
      this.departments = departments
    });
    this.myJssCategoryService.getAvailableMyJssCategories().subscribe(categories => {
      this.categories = categories
      this.categoriesByOrder = this.categories.sort((a: MyJssCategory, b: MyJssCategory) => b.categoryOrder - a.categoryOrder);
    });
  }

  public getCurrentRoute = () => {
    return this.router.url;
  }

  openSubscribe(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_SUBSCRIBE_ROUTE);
  }

  openSignIn(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_SIGN_IN_ROUTE);
  }

  openCategoryPosts(category: MyJssCategory, event: any) {
    this.appService.openRoute(event, "category/" + category.slug, undefined);
  }

  openDepartment(department: PublishingDepartment, event: any) {
    this.appService.openRoute(event, "department/" + department.code, undefined);
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
    this.appService.openMyJssRoute(event, MY_JSS_OFFERS_ROUTE);
  }

}
