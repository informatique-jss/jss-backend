import { Component, OnDestroy, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { debounceTime, fromEvent, Subscription } from 'rxjs';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { LayoutStoreService } from '../../services/layout-store.service';
import { SidenavComponent } from '../sidenav/sidenav.component';
import { TopbarComponent } from '../topbar/topbar.component';

@Component({
  selector: 'vertical-layout',
  imports: [SHARED_IMPORTS, RouterOutlet, SidenavComponent, TopbarComponent],
  templateUrl: './vertical-layout.component.html',
  standalone: true
})
export class VerticalLayoutComponent implements OnInit, OnDestroy {
  constructor(public layout: LayoutStoreService) {
  }

  resizeSubscription!: Subscription;

  ngOnInit() {
    this.onResize();

    this.resizeSubscription = fromEvent(window, 'resize')
      .pipe(debounceTime(200))
      .subscribe(() => this.onResize());
  }

  onResize(): void {
    const width = window.innerWidth;
    const size = this.layout.sidenavSize;

    if (width <= 767.98) {
      this.layout.setSidenavSize('offcanvas', false);
    } else if (width <= 1140 && !['offcanvas'].includes(size)) {
      this.layout.setSidenavSize(size === 'on-hover' ? 'condensed' : 'condensed', false);
    } else {
      this.layout.setSidenavSize(size);
    }
  }

  ngOnDestroy(): void {
    this.resizeSubscription?.unsubscribe();
  }
}
