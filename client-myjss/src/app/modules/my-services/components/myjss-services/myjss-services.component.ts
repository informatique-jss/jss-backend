import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { AppService } from '../../../../libs/app.service';
import { MenuItem } from '../../../general/model/MenuItem';

@Component({
  selector: 'jss-services',
  templateUrl: './myjss-services.component.html',
  styleUrls: ['./myjss-services.component.css'],
  standalone: false
})
export class MyJssServicesComponent implements OnInit {
  myJssServicesItems: MenuItem[] = this.appService.getAllServicesMenuItems();

  selectedTab: MenuItem | null = null;

  constructor(
    private appService: AppService,
    private changeDetectorRef: ChangeDetectorRef,
    private router: Router) { }

  ngOnInit() {
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
