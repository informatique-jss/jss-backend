import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { NotificationService } from '../../modules/miscellaneous/services/notification.service';
import { SearchService } from '../../services/search.service';
@Component({
  selector: 'app-sidenav-list',
  templateUrl: './sidenav-list.component.html',
  styleUrls: ['./sidenav-list.component.css']
})
export class SidenavListComponent implements OnInit {
  logoOsiris: string = '/assets/images/jss_icon.png';
  @Input() isDarkTheme: boolean = false;

  constructor(protected appService: AppService, protected router: Router,
    protected habilitationService: HabilitationsService,
    private notidicationService: NotificationService,
    private userPreferenceService: UserPreferenceService,
    protected searchService: SearchService) { }

  ngOnInit() {
    this.notidicationService.getNotificationsObservable().subscribe();
    this.isDarkTheme = this.userPreferenceService.getDarkMode();
    this.refreshBodyTheme();
  }

  public onSidenavClose = () => {
    this.appService.changeSidenavOpenState(false);
  }

  public getCurrentRoute = () => {
    return this.router.url;
  }

  toggleDarkTheme() {
    this.isDarkTheme = !this.isDarkTheme;
    this.userPreferenceService.setDarkMode(this.isDarkTheme);
    this.refreshBodyTheme();
  }

  refreshBodyTheme() {
    // get html body element
    const bodyElement = document.body;

    if (bodyElement) {
      // remove existing class (needed if theme is being changed)
      bodyElement.classList.remove(!this.isDarkTheme ? 'dark' : 'light');
      // add next theme class
      bodyElement.classList.add(this.isDarkTheme ? 'dark' : 'light');

    }
  }

  canViewDashboardModule() {
    return this.habilitationService.canViewDashboardModule();
  }

  canViewReportingModule() {
    return this.habilitationService.canViewReportingModule();
  }

  canViewTiersModule() {
    return this.habilitationService.canViewTiersModule();
  }

  canViewConfrereModule() {
    return this.habilitationService.canViewConfrereModule();
  }

  canViewCompetentAuthorityModule() {
    return this.habilitationService.canViewCompetentAuthorityModule();
  }

  canViewProviderModule() {
    return this.habilitationService.canViewProviderModule();
  }

  canViewQuotationModule() {
    return this.habilitationService.canViewQuotationModule();
  }

  canViewCustomerOrderModule() {
    return this.habilitationService.canViewCustomerOrderModule();
  }

  canViewAdministrationModule() {
    return this.habilitationService.canViewAdministrationModule();
  }

  canViewAccountingModule() {
    return this.habilitationService.canViewAccountingModule();
  }

  canViewInvoiceModule() {
    return this.habilitationService.canViewInvoiceModule();
  }

  canViewAffaireModule() {
    return this.habilitationService.canViewAffaireModule();
  }

  canViewProvisionModule() {
    return this.habilitationService.canViewProvisionModule();
  }

  openSearch() {
    this.searchService.openSearch();
  }

  openNotificationDialog() {
    this.notidicationService.openNotificationDialog();
  }

  getNotificationNumber() {
    let notificationNumber = 0;
    for (let notification of this.notidicationService.getNotificationsResult())
      if (notification.isRead == false)
        notificationNumber++;
    return notificationNumber;
  }

}
