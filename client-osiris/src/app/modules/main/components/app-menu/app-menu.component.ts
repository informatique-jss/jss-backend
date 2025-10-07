import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router, RouterLink } from '@angular/router';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from '@ng-icons/core';
import { filter } from 'rxjs';
import { scrollToElement } from '../../../../libs/GenericHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { ReportingDashboard } from '../../../reporting/model/ReportingDashboard';
import { ReportingDashboardService } from '../../../reporting/services/reporting.dashboard.service';
import { MenuItemType } from '../../model/MenuItemType';

@Component({
  selector: 'app-menu',
  imports: [NgIcon, NgbCollapse, RouterLink, SHARED_IMPORTS],
  templateUrl: './app-menu.component.html',
  standalone: true
})
export class AppMenuComponent implements OnInit {

  constructor(
    private router: Router,
    private dashboardService: ReportingDashboardService,
    private activatedRoute: ActivatedRoute,
  ) {
  }

  userDashboards: ReportingDashboard[] | undefined;

  @ViewChild('MenuItemWithChildren', { static: true })
  menuItemWithChildren!: TemplateRef<{ item: MenuItemType }>;

  @ViewChild('MenuItem', { static: true })
  menuItem!: TemplateRef<{ item: MenuItemType }>;

  menuItems: MenuItemType[] = [];
  idTiers: number | undefined;

  ngOnInit(): void {
    this.idTiers = this.activatedRoute.snapshot.params['idTiers'];
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.expandActivePaths(this.menuItems);
        setTimeout(() => this.scrollToActiveLink(), 50);
      });

    this.expandActivePaths(this.menuItems);
    setTimeout(() => this.scrollToActiveLink(), 100);

    this.dashboardService.getReportingDashboardsForCurrentUser().subscribe(response => {
      this.userDashboards = response;
      this.initMenu();
    })
  }

  initMenu() {
    this.menuItems = [
      { label: "Menu", isTitle: true } as MenuItemType,
      {
        label: "Tiers/Responsables", isTitle: false, isDisabled: false, isSpecial: false, icon: "tablerUsers", url: "",
        children: [{
          label: "Crm", isTitle: false, isDisabled: false, isSpecial: false, icon: "tablerUsers",
          children: [
            //TODO boucle for pour charger / afficher menu
            { label: "Home", isTitle: false, isDisabled: false, isSpecial: false, icon: "tablerUsers", url: `tiers/crm/TIERS_KPI_HOME_DISPLAY/${this.idTiers}` },
            { label: "Infos générales", isTitle: false, isDisabled: false, isSpecial: false, icon: "tablerUsers", url: `tiers/crm/TIERS_KPI_MAIN_DISPLAY/${this.idTiers}` },
            { label: "Business", isTitle: false, isDisabled: false, isSpecial: false, icon: "tablerUsers", url: "" },
            { label: "Relation client", isTitle: false, isDisabled: false, isSpecial: false, icon: "tablerUsers", url: "" }
          ]
        },
        { label: "Osiris", isTitle: false, isDisabled: false, isSpecial: false, icon: "tablerUsers", url: "" }]
      } as MenuItemType,
      { label: "CRM", isTitle: false, isDisabled: false, isSpecial: false, icon: "tablerApps", url: "crm" } as MenuItemType,
      { label: "Reporting", isTitle: false, isCollapsed: true, isDisabled: false, isSpecial: false, icon: "tablerLayoutDashboard", children: this.getAllDashboardsItem() } as MenuItemType
    ]
  }

  hasSubMenu(item: MenuItemType): boolean {
    return !!item.children;
  }

  expandActivePaths(items: MenuItemType[]) {
    for (const item of items) {
      if (this.hasSubMenu(item)) {
        if (this.isChildActive(item))
          item.isCollapsed = false;
        this.expandActivePaths(item.children || []);
      }
    }
  }

  isChildActive(item: MenuItemType): boolean {
    if (item.url && this.router.url === item.url) return true;
    if (!item.children) return false;
    return item.children.some((child: MenuItemType) => this.isChildActive(child));
  }

  isActive(item: MenuItemType): boolean {
    return this.router.url === item.url;
  }

  scrollToActiveLink(): void {
    const activeItem = document.querySelector('[data-active-link="true"]') as HTMLElement;
    const scrollContainer = document.querySelector("#sidenav .simplebar-content-wrapper") as HTMLElement;

    if (activeItem && scrollContainer) {
      const containerRect = scrollContainer.getBoundingClientRect();
      const itemRect = activeItem.getBoundingClientRect();

      const offset = itemRect.top - containerRect.top - window.innerHeight * 0.4;

      scrollToElement(scrollContainer, scrollContainer.scrollTop + offset, 500);
    }
  }

  getAllDashboardsItem() {
    let dashboardItems = [];
    if (this.userDashboards) {
      for (let userDashboard of this.userDashboards) {
        dashboardItems.push({ label: userDashboard.label, url: '/dashboards/' + userDashboard.id })
      }
    }
    return dashboardItems;
  }

}
