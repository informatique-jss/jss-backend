import { Component, OnInit } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';

@Component({
  selector: 'invoicing',
  templateUrl: './invoicing.component.html',
  styleUrls: ['./invoicing.component.css']
})
export class InvoiceComponent implements OnInit {

  constructor(
    private appService: AppService,
    private userPreferenceService: UserPreferenceService
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Facturation");
    this.restoreTab();
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
