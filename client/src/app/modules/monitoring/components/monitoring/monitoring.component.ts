import { Component, OnInit } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { BatchSettings } from '../../model/BatchSettings';

@Component({
  selector: 'monitoring',
  templateUrl: './monitoring.component.html',
  styleUrls: ['./monitoring.component.css']
})
export class MonitoringComponent implements OnInit {

  constructor(
    private habilitationsService: HabilitationsService,
    private appService: AppService,
    private userPreferenceService: UserPreferenceService
  ) { }

  batchSettingsSelected: BatchSettings | undefined;
  selectedTabIndex = 0;

  ngOnInit() {
    this.appService.changeHeaderTitle("Supervision");
    this.restoreTab();
  }

  canDisplayExtendentMonitoring() {
    return this.habilitationsService.canDisplayExtendentMonitoring();
  }

  selectBatchSetting(batchSetting: BatchSettings) {
    this.batchSettingsSelected = undefined;
    setTimeout(() => {
      this.batchSettingsSelected = batchSetting;
      this.selectedTabIndex = 1;
    }, 0);
  }

  //Tabs management
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('monitoring', event.index);
  }

  restoreTab() {
    this.selectedTabIndex = this.userPreferenceService.getUserTabsSelectionIndex('monitoring');
  }
}
