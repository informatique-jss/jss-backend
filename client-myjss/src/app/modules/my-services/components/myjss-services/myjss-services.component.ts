import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { MenuItem } from '../../../general/model/MenuItem';
import { AppService } from '../../../main/services/app.service';

@Component({
  selector: 'jss-services',
  templateUrl: './myjss-services.component.html',
  styleUrls: ['./myjss-services.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class MyJssServicesComponent implements OnInit {
  myJssServicesItems!: MenuItem[];

  selectedTab: MenuItem | null = null;

  constructor(
    private appService: AppService,
    private changeDetectorRef: ChangeDetectorRef,
    private router: Router) { }

  ngOnInit() {
    this.myJssServicesItems = this.appService.getAllServicesMenuItems();
    if (this.myJssServicesItems.length > 0 && this.router.url) {
      this.matchRoute(this.router.url);
    } else {
      this.selectedTab = this.myJssServicesItems[0];
    }

    this.router.events.subscribe(url => {
      if (url instanceof NavigationEnd) {
        this.matchRoute(url.url);
      }
    });
  }

  private matchRoute(url: string) {
    for (let route of this.myJssServicesItems) {
      if (url && url.indexOf(route.route) >= 0) {
        this.selectedTab = route;
      }
    }
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }
}
