import { Component, OnInit } from '@angular/core';
import { MY_JSS_CGV_ROUTE, MY_JSS_CONFIDENTIALITY_ROUTE, MY_JSS_JOIN_US_ROUTE, MY_JSS_LEGAL_MENTIONS_ROUTE, MY_JSS_PARTNERS_ROUTE, MY_JSS_SERVICES_ANNOUNCEMENT_ROUTE, MY_JSS_SERVICES_APOSITLLE_ROUTE, MY_JSS_SERVICES_DOCUMENT_ROUTE, MY_JSS_SERVICES_DOMICILIATION_ROUTE, MY_JSS_SERVICES_FORMALITY_ROUTE, MY_JSS_SUBSCRIBE_ROUTE, MY_JSS_WHO_ARE_WE_ROUTE } from '../../../libs/Constants';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { JssCategory } from '../../model/JssCategory';
import { PublishingDepartment } from '../../model/PublishingDepartment';
import { DepartmentService } from '../../services/department.service';
import { JssCategoryService } from '../../services/jss.category.service';

@Component({
  selector: 'footer-page',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css'],
  imports: [SHARED_IMPORTS],
  standalone: true
})
export class FooterComponent implements OnInit {

  jssCategories: JssCategory[] | undefined;
  departments: PublishingDepartment[] | undefined;
  hasAcceptCookie: boolean = false;

  constructor(
    private jssCategoryService: JssCategoryService,
    private publishingDepartmentService: DepartmentService,
    private appService: AppService
  ) { }

  ngOnInit() {
    this.jssCategoryService.getAvailableJssCategories().subscribe(categories => {
      this.jssCategories = categories.sort((a: JssCategory, b: JssCategory) => a.name.localeCompare(b.name));
    })
    this.publishingDepartmentService.getAvailablePublishingDepartments().subscribe(response => {
      this.departments = response.sort((a: PublishingDepartment, b: PublishingDepartment) => a.name.localeCompare(b.name));
    })

    //if (localStorage.getItem('hasAcceptCookie') != null) {
    //  let a = localStorage.getItem('hasAcceptCookie');
    //  let hasAcceptCookie = JSON.parse(a!) as boolean;
    //  if (hasAcceptCookie)
    //    this.hasAcceptCookie = hasAcceptCookie;
    //}
  }

  acceptCookie() {
    this.hasAcceptCookie = true;
    localStorage.setItem('hasAcceptCookie', 'true');
  }


  openContact(event: any) {
    this.appService.openRoute(event, "contact", undefined);
  }

  openDepartment(department: PublishingDepartment, event: any) {
    this.appService.openRoute(event, "post/department/" + department.id, undefined);
  }

  openCategoryPosts(category: JssCategory, event: any) {
    this.appService.openRoute(event, "post/category/" + category.slug, undefined);
  }

  openSubscribe(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_SUBSCRIBE_ROUTE);
  }

  openConfidentiality(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_CONFIDENTIALITY_ROUTE);
  }

  openProductAnnounce(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_SERVICES_ANNOUNCEMENT_ROUTE);
  }

  openProductFormality(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_SERVICES_FORMALITY_ROUTE);
  }

  openProductApositlle(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_SERVICES_APOSITLLE_ROUTE);
  }

  openProductDomiciliation(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_SERVICES_DOMICILIATION_ROUTE);
  }

  openProductDocument(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_SERVICES_DOCUMENT_ROUTE);
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

  openContributePage(event: any) {
    this.appService.openRoute(event, "contribute", undefined);
  }

  openSeriesPosts(event: any) {
    this.appService.openRoute(event, "series", undefined);
  }

  openKiosk(event: any) {
    this.appService.openRoute(event, "kiosk", undefined);
  }
}
