import { Component } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { LucideAngularModule, Search } from 'lucide-angular';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { LayoutStoreService } from '../../services/layout-store.service';


@Component({
  selector: 'app-topbar',
  imports: [
    NgIcon,
    LucideAngularModule,
    SHARED_IMPORTS,
  ],
  templateUrl: './topbar.component.html',
  standalone: true
})
export class TopbarComponent {
  constructor(public layout: LayoutStoreService) {
  }

  toggleSidebar() {

    const html = document.documentElement;
    const currentSize = html.getAttribute('data-sidenav-size');
    const savedSize = this.layout.sidenavSize;

    if (currentSize === 'offcanvas') {
      html.classList.toggle('sidebar-enable')
      this.layout.showBackdrop()
    } else if (savedSize === 'compact') {
      this.layout.setSidenavSize(currentSize === 'compact' ? 'condensed' : 'compact', false);
    } else {
      this.layout.setSidenavSize(currentSize === 'condensed' ? 'default' : 'condensed');
    }
  }

  toggleTheme() {
    if (this.layout.theme === 'light') {
      this.layout.setTheme('dark')
    } else {
      this.layout.setTheme('light')
    }
  }

  Search = Search;
}
