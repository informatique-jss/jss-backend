import { AfterContentChecked, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { AppService } from '../../../../libs/app.service';
import { MenuItem } from '../../../general/model/MenuItem';

@Component({
  selector: 'company',
  templateUrl: './company.component.html',
  styleUrls: ['./company.component.css']
})
export class CompanyComponent implements OnInit, AfterContentChecked {

  companyItems: MenuItem[] = this.appService.getAllCompanyMenuItems();

  selectedTab: MenuItem = this.companyItems[0];

  breadcrumbItems = ['La société'];

  constructor(
    private appService: AppService,
    private changeDetectorRef: ChangeDetectorRef,
    private router: Router
  ) { }

  ngOnInit() {
    this.router.events.subscribe(url => {
      if (url instanceof NavigationEnd) {
        for (let route of this.companyItems) {
          if (url && url.url && url.url.indexOf(route.route) >= 0) {
            this.selectedTab = route;
          }
        }
        this.updateBreadcrumb(this.selectedTab);

      }
    });
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  onTabClick(tab: MenuItem): void {
    this.selectedTab = tab;
    this.updateBreadcrumb(this.selectedTab);
  }

  updateBreadcrumb(tab: MenuItem): void {
    this.breadcrumbItems = ['La société', tab.label];
  }
}
