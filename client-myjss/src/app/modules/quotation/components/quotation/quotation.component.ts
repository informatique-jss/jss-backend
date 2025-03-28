import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { AppService } from '../../../../libs/app.service';
import { MenuItem } from '../../../general/model/MenuItem';

@Component({
  selector: 'app-quotation',
  templateUrl: './quotation.component.html',
  styleUrls: ['./quotation.component.css']
})
export class QuotationComponent implements OnInit {
  myJssQuotationItems: MenuItem[] = this.appService.getAllQuotationMenuItems();

  selectedTab: MenuItem | null = null;

  constructor(
    private appService: AppService,
    private changeDetectorRef: ChangeDetectorRef,
    private router: Router) { }

  ngOnInit() {
    if (this.myJssQuotationItems.length > 0 && this.router.url) {
      this.matchRoute(this.router.url);
    } else {
      this.selectedTab = this.myJssQuotationItems[0];
    }

    this.router.events.subscribe(url => {
      if (url instanceof NavigationEnd) {
        this.matchRoute(url.url);
      }
    });
  }

  private matchRoute(url: string) {
    for (let route of this.myJssQuotationItems) {
      if (url && url.indexOf(route.route) >= 0) {
        this.selectedTab = route;
      }
    }
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }
}
