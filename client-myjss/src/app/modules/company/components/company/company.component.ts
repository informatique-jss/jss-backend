import { AfterContentChecked, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';
import { MenuItem } from '../../../general/model/MenuItem';
import { TabService } from '../../services/tab.service';

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
    private tabService: TabService,
    private changeDetectorRef: ChangeDetectorRef,
  ) { }

  ngOnInit() {
    this.selectedTab = this.companyItems[0];
    this.updateBreadcrumb(this.selectedTab);

    // Listen to breadcrumb updates
    this.tabService.selectedTab$.subscribe(tab => {
      this.selectedTab = tab;
      this.updateBreadcrumb(this.selectedTab);
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
