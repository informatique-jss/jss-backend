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

  selectedTab: MenuItem | null = null;

  constructor(
    private appService: AppService,
    private changeDetectorRef: ChangeDetectorRef,
    private router: Router) { }

  ngOnInit() {
    this.companyItems = this.appService.getAllCompanyMenuItems();

    if (this.companyItems.length > 0) {
      this.selectedTab = this.companyItems[0];
    }

    this.router.events.subscribe(url => {
      if (url instanceof NavigationEnd) {
        for (let route of this.companyItems) {
          if (url && url.url && url.url.indexOf(route.route) >= 0) {
            this.selectedTab = route;
          }
        }
      }
    });
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }
}
