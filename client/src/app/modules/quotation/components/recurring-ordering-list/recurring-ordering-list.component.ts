import { Component, OnInit } from '@angular/core';
import { UserPreferenceService } from 'src/app/services/user.preference.service';

@Component({
  selector: 'recurring-ordering-list',
  templateUrl: './recurring-ordering-list.component.html',
  styleUrls: ['./recurring-ordering-list.component.css']
})
export class RecurringOrderingListComponent implements OnInit {

  selectedTabIndex = 0;

  constructor(
    private userPreferenceService: UserPreferenceService,
  ) { }

  ngOnInit() {
    this.selectedTabIndex = this.userPreferenceService.getUserTabsSelectionIndex('recurringList');
  }

  //Tabs management
  changeTab(event: any) {
    this.userPreferenceService.setUserTabsSelectionIndex('recurringList', event.index);
  }
}
