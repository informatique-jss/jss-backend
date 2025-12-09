import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router, RouterLink } from '@angular/router';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from '@ng-icons/core';
import { filter } from 'rxjs';
import { scrollToElement } from '../../../../libs/GenericHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { ReportingDashboard } from '../../../reporting/model/ReportingDashboard';
import { ReportingDashboardService } from '../../../reporting/services/reporting.dashboard.service';
import { ResponsableService } from '../../../tiers/services/responsable.service';
import { TiersService } from '../../../tiers/services/tiers.service';
import { MenuItemType } from '../../model/MenuItemType';
import { AppService } from '../../services/app.service';

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
    private tiersService: TiersService,
    private responsableService: ResponsableService,
    private appService: AppService
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

    this.tiersService.getSelectedTiersUniqueChangeEvent().subscribe(response => {
      this.initMenu();
    })

    this.responsableService.getSelectedResponsableUniqueChangeEvent().subscribe(response => {
      this.initMenu();
    })
  }

  initMenu() {
    this.menuItems = [
      { label: "Menu", isTitle: true } as MenuItemType,
      {
        label: "Tiers/Responsables", isTitle: false, isDisabled: false, isSpecial: false, icon: "tablerUsers", url: "tiers",
        children: [
          { label: this.getTiersLabel(), isTitle: false, isDisabled: false, isSpecial: false, icon: "tablerBuilding", url: "tiers", children: this.getTiersChildren() },
          { label: this.getResponsableLabel(), isTitle: false, isDisabled: false, isSpecial: false, icon: "tablerUsers", url: "responsables", children: this.getResponsableChildren() },
          { label: "Crm", isTitle: false, isDisabled: false, isSpecial: false, icon: "tablerHeartHandshake", url: "tiers/crm/kpi" }
        ]
      } as MenuItemType,
      { label: "Reporting", isTitle: false, isCollapsed: true, isDisabled: false, isSpecial: false, icon: "tablerLayoutDashboard", children: this.getAllDashboardsItem() } as MenuItemType
    ]
  }

  getTiersLabel() {
    let selectedTiers = this.tiersService.getSelectedTiersUnique();
    if (selectedTiers)
      return selectedTiers.denomination ? selectedTiers.denomination : (selectedTiers.firstname + ' ' + selectedTiers.lastname);
    return "Tiers";
  }

  getResponsableLabel() {
    let selectedResponsable = this.responsableService.getSelectedResponsableUnique();
    if (selectedResponsable)
      return selectedResponsable.firstname + ' ' + selectedResponsable.lastname;
    return "Responsables";
  }

  getTiersChildren() {
    let selectedTiers = this.tiersService.getSelectedTiersUnique();
    if (selectedTiers) {
      return [{ label: selectedTiers.denomination ? selectedTiers.denomination : (selectedTiers.firstname + ' ' + selectedTiers.lastname), isTitle: false, isDisabled: false, isSpecial: false, icon: "tablerBuilding", url: "tiers/view/" + selectedTiers.id, children: this.getResponsableChildren() }]
    }
    return [];
  }

  getResponsableChildren() {
    let selectedResponsable = this.responsableService.getSelectedResponsableUnique();
    if (selectedResponsable) {
      return [{ label: selectedResponsable.firstname + ' ' + selectedResponsable.lastname, isTitle: false, isDisabled: false, isSpecial: false, icon: "tablerUser", url: "responsable/view/" + selectedResponsable.id }]
    }
    return [];
  }

  hasSubMenu(item: MenuItemType): boolean {
    return (item.children != null && item.children != undefined && item.children.length > 0);
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
