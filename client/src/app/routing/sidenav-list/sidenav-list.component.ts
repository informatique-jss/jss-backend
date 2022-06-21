import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AppService } from 'src/app/app.service';
import { HabilitationsService } from 'src/app/habilitations.service';
import { SearchService } from './../../search.service';
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

  openSearch() {
    this.searchService.openSearch();
  }

}
