import { Component, OnInit } from '@angular/core';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { BatchSettings } from '../../model/BatchSettings';

@Component({
  selector: 'monitoring',
  templateUrl: './monitoring.component.html',
  styleUrls: ['./monitoring.component.css']
})
export class MonitoringComponent implements OnInit {

  constructor(
    private habilitationsService: HabilitationsService,
    private appService: AppService
  ) { }

  batchSettingsSelected: BatchSettings | undefined;
  selectedTabIndex = 0;

  ngOnInit() {
    this.appService.changeHeaderTitle("Supervision");
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
}
