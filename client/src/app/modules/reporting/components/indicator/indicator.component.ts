import { Component, OnInit } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { UserPreferenceService } from 'src/app/services/user.preference.service';

@Component({
  selector: 'indicator',
  templateUrl: './indicator.component.html',
  styleUrls: ['./indicator.component.css']
})
export class IndicatorComponent implements OnInit {

  constructor(private userPreferenceService: UserPreferenceService) { }

  ngOnInit() {
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('indicator', event.index);
  }

}
