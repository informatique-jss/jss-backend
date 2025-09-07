import { Component, OnInit } from '@angular/core';
import { MY_JSS_CGV_ROUTE, MY_JSS_CONFIDENTIALITY_ROUTE, MY_JSS_JOIN_US_ROUTE, MY_JSS_LEGAL_MENTIONS_ROUTE, MY_JSS_SERVICES_ANNOUNCEMENT_ROUTE, MY_JSS_SERVICES_APOSITLLE_ROUTE, MY_JSS_SERVICES_DOCUMENT_ROUTE, MY_JSS_SERVICES_DOMICILIATION_ROUTE, MY_JSS_SERVICES_FORMALITY_ROUTE, MY_JSS_SUBSCRIBE_ROUTE, MY_JSS_WHO_ARE_WE_ROUTE } from '../../../libs/Constants';
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

  openLegalMentions(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_LEGAL_MENTIONS_ROUTE);
  }

  openCgv(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_CGV_ROUTE);
  }

  openPrivacyPolicy(event: any) {
    this.appService.openMyJssRoute(event, MY_JSS_CONFIDENTIALITY_ROUTE);
  }
}
