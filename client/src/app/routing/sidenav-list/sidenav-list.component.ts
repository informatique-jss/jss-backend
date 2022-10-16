import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
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
    protected searchService: SearchService) { }

  ngOnInit() {
  }

  public onSidenavClose = () => {
    this.appService.changeSidenavOpenState(false);
  }

  public getCurrentRoute = () => {
    return this.router.url;
  }

  canViewTiersModule() {
    return this.habilitationService.canViewTiersModule();
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

  openSearch() {
    this.searchService.openSearch();
  }

}
