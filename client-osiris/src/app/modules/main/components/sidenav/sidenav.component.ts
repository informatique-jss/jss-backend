import { NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NgIcon } from '@ng-icons/core';
import { SimplebarAngularModule } from 'simplebar-angular';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { LayoutStoreService } from '../../services/layout-store.service';
import { AppMenuComponent } from '../app-menu/app-menu.component';
import { UserProfileComponent } from '../user-profile/user-profile.component';

@Component({
  selector: 'app-sidenav',
  imports: [
    UserProfileComponent,
    AppMenuComponent,
    SimplebarAngularModule,
    NgIf,
    NgIcon,
    RouterLink, SHARED_IMPORTS
  ],
  templateUrl: './sidenav.component.html',
  standalone: true
})
export class SidenavComponent {
  constructor(public layout: LayoutStoreService) {
  }

  hoverSidebar() {
    this.layout.setSidenavSize(this.layout.sidenavSize === 'on-hover-active' ? 'on-hover' : 'on-hover-active')
  }

  closeSidebar() {
    const html = document.documentElement;
    html.classList.toggle('sidebar-enable')
    this.layout.hideBackdrop()
  }
}
