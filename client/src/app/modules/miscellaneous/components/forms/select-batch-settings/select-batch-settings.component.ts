import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BatchSettings } from 'src/app/modules/monitoring/model/BatchSettings';
import { BatchSettingsService } from 'src/app/modules/monitoring/services/batch.settings.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-batch-settings',
  templateUrl: './../generic-select/generic-multiple-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html']
})

export class SelectBatchSettingsComponent extends GenericMultipleSelectComponent<BatchSettings> implements OnInit {

  types: BatchSettings[] = [] as Array<BatchSettings>;

  constructor(private formBuild: UntypedFormBuilder,
    private batchSettingsService: BatchSettingsService,
  ) {
    super(formBuild,)
  }

  initTypes(): void {
    this.batchSettingsService.getBatchSettings().subscribe(response => {
      this.types = response;
    });
  }
}
