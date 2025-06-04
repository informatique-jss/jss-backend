import { Component, OnInit } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';

@Component({
  selector: 'invoicing',
  templateUrl: './invoicing.component.html',
  styleUrls: ['./invoicing.component.css']
})
export class InvoiceComponent implements OnInit {

  constructor(
    private appService: AppService,
    private userPreferenceService: UserPreferenceService,
    private habilitationService: HabilitationsService
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Facturation");
    this.restoreTab();
  }

  canViewBankBalance() {
    return this.habilitationService.canViewBankBalance();
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('invoicing', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('invoicing');
  }

}
