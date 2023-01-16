import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { NotificationService } from '../../modules/miscellaneous/services/notification.service';
import { SearchService } from '../../services/search.service';
@Component({
  selector: 'app-sidenav-list',
  templateUrl: './sidenav-list.component.html',
  styleUrls: ['./sidenav-list.component.css']
})
export class SidenavListComponent implements OnInit {
  logoOsiris: string = '/assets/images/jss_icon.png';

  constructor(protected appService: AppService, protected router: Router,
    protected habilitationService: HabilitationsService,
    private notidicationService: NotificationService,
    protected searchService: SearchService) { }

  ngOnInit() {
    this.notidicationService.getNotificationsObservable().subscribe();
  }

  public onSidenavClose = () => {
    this.appService.changeSidenavOpenState(false);
  }

  public getCurrentRoute = () => {
    return this.router.url;
  }

  canViewDashboardModule() {
    return this.habilitationService.canViewDashboardModule();
  }

  canViewTiersModule() {
    return this.habilitationService.canViewTiersModule();
  }

  canViewConfrereModule() {
    return this.habilitationService.canViewConfrereModule();
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
