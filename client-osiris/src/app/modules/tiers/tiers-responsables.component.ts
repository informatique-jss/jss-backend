import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { SHARED_IMPORTS } from '../../libs/SharedImports';
import { MenuItemType } from '../main/model/MenuItemType';
import { AppService } from '../main/services/app.service';
import { SidebarComponent } from './components/right-sidebar/sidebar.component';
import { TiersSummaryComponent } from "./components/tiers-summary/tiers-summary.component";
import { TiersService } from './services/tiers.service';

@Component({
  selector: 'tiers-responsables',
  imports: [SHARED_IMPORTS,
    NgbNavModule,
    SidebarComponent, TiersSummaryComponent],
  standalone: true,
  templateUrl: './tiers-responsables.component.html',
  styleUrls: ['./tiers-responsables.component.scss'],
})
export class TiersResponsablesComponent implements OnInit {
  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private appService: AppService,
    private tiersService: TiersService,
  ) { }
  screen: string = "";
  tiersMenuItems!: MenuItemType[];
  selectedTab: MenuItemType | null = null;

  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      const idTiersSelected = params['idTiers'];
      this.screen = params['screen'];

      if (idTiersSelected) {
        this.tiersService.selectTiers(idTiersSelected);
        this.tiersMenuItems = this.appService.getTiersMenuItems(idTiersSelected);
      }

      if (this.tiersMenuItems?.length > 0 && this.router.url) {
        this.matchRoute(this.router.url);
      } else {
        this.selectedTab = this.tiersMenuItems?.[0];
      }
    });

    this.router.events.subscribe(url => {
      if (url instanceof NavigationEnd) {
        this.matchRoute(url.url);
      }
    });
  }

  private matchRoute(url: string) {
    if (!this.selectedTab)
      this.selectedTab = this.tiersMenuItems[0];

    for (let route of this.tiersMenuItems) {
      if (url && route.url && url.indexOf(route.url) >= 0) {
        this.selectedTab.url = url;
      }
    }
  }

}
