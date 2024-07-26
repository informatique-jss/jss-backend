import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BatchStatus } from 'src/app/modules/monitoring/model/BatchStatus';
import { BatchStatusService } from 'src/app/modules/monitoring/services/batch.status.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-batch-status',
  templateUrl: './../generic-select/generic-multiple-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html']
})

export class SelectBatchStatusComponent extends GenericMultipleSelectComponent<BatchStatus> implements OnInit {

  types: BatchStatus[] = [] as Array<BatchStatus>;

  constructor(private formBuild: UntypedFormBuilder,
    private batchStatusService: BatchStatusService,
  ) {
    super(formBuild,)
  }

  initTypes(): void {
    this.batchStatusService.getBatchStatus().subscribe(response => {
      this.types = response;
    });
  }
}
