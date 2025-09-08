import { Component } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { SHARED_IMPORTS } from './libs/SharedImports';
import { ConstantService } from './services/constant.service';
import { GtmService } from './services/gtm.service';
import { PageInfo, PageViewPayload } from './services/GtmPayload';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,
    SHARED_IMPORTS],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  standalone: true
})
export class AppComponent {

  constructor(
    private constantService: ConstantService,
    private router: Router,
    private gtmService: GtmService
  ) { }

  ngOnInit() {
    this.constantService.initConstant();

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.gtmService.trackPageView({ page: { type: "page", name: event.urlAfterRedirects } as PageInfo } as PageViewPayload);
      });
  }
}
