import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../libs/app.service';
import { MenuItem } from '../../general/model/MenuItem';

@Component({
  selector: 'company',
  templateUrl: './company.component.html',
  styleUrls: ['./company.component.css']
})
export class CompanyComponent implements OnInit {

  companyItems: MenuItem[] = this.appService.getAllCompanyMenuItems();

  selectedTab: MenuItem = this.companyItems[0];

  breadcrumbItems = ['La société'];

  constructor(
    private appService: AppService,
  ) { }


  ngOnInit() {
    this.selectedTab = this.companyItems[0];
    this.updateBreadcrumb(this.selectedTab);
  }


  onTabClick(tab: MenuItem): void {
    this.selectedTab = tab;
    this.updateBreadcrumb(this.selectedTab);
  }

  updateBreadcrumb(tab: MenuItem): void {
    this.breadcrumbItems = ['La société', tab.label];
  }
}
