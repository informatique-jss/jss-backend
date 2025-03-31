import { Component, OnInit } from '@angular/core';
import { MY_JSS_CGV_ROUTE, MY_JSS_CONFIDENTIALITY_ROUTE, MY_JSS_JOIN_US_ROUTE, MY_JSS_LEGAL_MENTIONS_ROUTE, MY_JSS_PARTNERS_ROUTE, MY_JSS_PRODUCT_ROUTE, MY_JSS_SUBSCRIBE_ROUTE, MY_JSS_WHO_ARE_WE_ROUTE } from '../../../libs/Constants';
import { AppService } from '../../../services/app.service';
import { MyJssCategory } from '../../model/MyJssCategory';
import { PublishingDepartment } from '../../model/PublishingDepartment';
import { DepartmentService } from '../../services/department.service';
import { MyJssCategoryService } from '../../services/myjss.category.service';

@Component({
    selector: 'footer-page',
    templateUrl: './footer.component.html',
    styleUrls: ['./footer.component.css'],
    standalone: false
})
export class FooterComponent implements OnInit {

  myJssCategories: MyJssCategory[] | undefined;
  departments: PublishingDepartment[] | undefined;
  hasAcceptCookie: boolean = false;

  constructor(
    private myJssCategoryService: MyJssCategoryService,
    private publishingDepartmentService: DepartmentService,
    private appService: AppService
  ) { }

  ngOnInit() {
    this.myJssCategoryService.getAvailableMyJssCategories().subscribe(categories => {
      this.myJssCategories = categories.sort((a: MyJssCategory, b: MyJssCategory) => a.name.localeCompare(b.name));
    })
    this.publishingDepartmentService.getAvailablePublishingDepartments().subscribe(response => {
      this.departments = response.sort((a: PublishingDepartment, b: PublishingDepartment) => a.name.localeCompare(b.name));
    })

    if (localStorage.getItem('hasAcceptCookie') != null) {
      let a = localStorage.getItem('hasAcceptCookie');
      let hasAcceptCookie = JSON.parse(a!) as boolean;
      if (hasAcceptCookie)
        this.hasAcceptCookie = hasAcceptCookie;
    }
  }

  acceptCookie() {
    this.hasAcceptCookie = true;
    localStorage.setItem('hasAcceptCookie', 'true');
  }


  openContact(event: any) {
    this.appService.openRoute(event, "contact", undefined);
  }

  openDepartment(department: PublishingDepartment, event: any) {
    this.appService.openRoute(event, "department/" + department.code, undefined);
  }

  openCategoryPosts(category: MyJssCategory, event: any) {
    this.appService.openRoute(event, "category/" + category.slug, undefined);
  }

  openSubscribe(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_SUBSCRIBE_ROUTE);
  }

  openConfidentiality(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_CONFIDENTIALITY_ROUTE);
  }

  openProduct(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_PRODUCT_ROUTE);
  }

  openWhoAreWe(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_WHO_ARE_WE_ROUTE);
  }

  openJoinUs(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_JOIN_US_ROUTE);
  }

  openPartners(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_PARTNERS_ROUTE);
  }

  openLegalMentions(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_LEGAL_MENTIONS_ROUTE);
  }

  openCgv(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_CGV_ROUTE);
  }

  openPodcastPosts(event: any) {
    this.appService.openRoute(event, "podcasts", undefined);
  }

  openInterviewPosts(event: any) {
    this.appService.openRoute(event, "interviews", undefined);
  }

  openSeriesPosts(event: any) {
    this.appService.openRoute(event, "series", undefined);
  }
}
