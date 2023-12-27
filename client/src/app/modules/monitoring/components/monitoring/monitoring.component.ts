import { Component, OnInit } from '@angular/core';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { BatchSettings } from '../../model/BatchSettings';

@Component({
  selector: 'monitoring',
  templateUrl: './monitoring.component.html',
  styleUrls: ['./monitoring.component.css']
})
export class MonitoringComponent implements OnInit {

  constructor(
    private habilitationsService: HabilitationsService
  ) { }

  batchSettingsSelected: BatchSettings | undefined;
  selectedTabIndex = 0;

  ngOnInit() {
  }

  canDisplayExtendentMonitoring() {
    return this.habilitationsService.canDisplayExtendentMonitoring();
  }

  selectBatchSetting(batchSetting: BatchSettings) {
    this.batchSettingsSelected = batchSetting;
    this.selectedTabIndex = 1;
  }
}
