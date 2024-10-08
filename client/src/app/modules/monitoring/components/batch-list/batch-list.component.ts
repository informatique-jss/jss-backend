import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { BATCH_STATUS_ACKNOWLEDGE_CODE, BATCH_STATUS_ERROR_CODE } from 'src/app/libs/Constants';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Batch } from '../../model/Batch';
import { BatchSearch } from '../../model/BatchSearch';
import { BatchStatus } from '../../model/BatchStatus';
import { BatchService } from '../../services/batch.service';
import { BatchStatusService } from '../../services/batch.status.service';

@Component({
  selector: 'batch-list',
  templateUrl: './batch-list.component.html',
  styleUrls: ['./batch-list.component.css']
})
export class BatchListComponent implements OnInit {

  batchDetailsColomns: SortTableColumn<Batch>[] = [];
  batchDetailsActions: SortTableAction<Batch>[] = [];
  batchDetails: Batch[] = [];
  batchStatus: BatchStatus[] = [];
  @Input() batchSearch: BatchSearch | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private batchService: BatchService,
    private batchStatusService: BatchStatusService
  ) { }

  batchSearchForm = this.formBuilder.group({});

  ngOnInit() {

    this.batchStatusService.getBatchStatus().subscribe(response => this.batchStatus = response);

    this.batchDetailsColomns = [];
    this.batchDetailsColomns.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn<Batch>);
    this.batchDetailsColomns.push({ id: "batchSettings", fieldName: "batchSettings.label", label: "Batch" } as SortTableColumn<Batch>);
    this.batchDetailsColomns.push({ id: "createdDate", fieldName: "createdDate", label: "Création", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Batch>);
    this.batchDetailsColomns.push({ id: "startDate", fieldName: "startDate", label: "Début", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Batch>);
    this.batchDetailsColomns.push({ id: "endDate", fieldName: "endDate", label: "Fin", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Batch>);
    this.batchDetailsColomns.push({ id: "time", fieldName: "time", label: "Durée", valueFonction: (element: Batch, column: SortTableColumn<Batch>) => { return element.endDate ? (Math.round((new Date(element.endDate).getTime() - new Date(element.startDate).getTime()) / 1000 * 10) / 10) + "s" : null } } as SortTableColumn<Batch>);
    this.batchDetailsColomns.push({ id: "batchStatus", fieldName: "batchStatus.label", label: "Status" } as SortTableColumn<Batch>);
    this.batchDetailsColomns.push({ id: "entityId", fieldName: "entityId", label: "Identifiant de l'entité" } as SortTableColumn<Batch>);
    this.batchDetailsColomns.push({ id: "node", fieldName: "node.hostname", label: "Noeud" } as SortTableColumn<Batch>);
    this.batchDetailsColomns.push({ id: "osirisLog", fieldName: "osirisLog.id", label: "N° de log" } as SortTableColumn<Batch>);

    this.batchDetailsActions.push({
      actionIcon: "check_circle", actionName: "Acquiter", actionClick: (column: SortTableAction<Batch>, element: Batch, event: any) => {
        if (element && element.batchStatus.code == BATCH_STATUS_ERROR_CODE) {
          for (let batchStatu of this.batchStatus)
            if (batchStatu.code == BATCH_STATUS_ACKNOWLEDGE_CODE)
              element.batchStatus = batchStatu;
          this.batchService.addOrUpdateBatchStatus(element).subscribe(response => this.searchBatchs());
        }
        return undefined;
      }, display: true,
    } as SortTableAction<Batch>);
    this.batchDetailsActions.push({
      actionIcon: "restart_alt", actionName: "Exécuter de nouveau", actionClick: (column: SortTableAction<Batch>, element: Batch, event: any) => {
        if (element) {
          this.batchService.declareNewBatch(element.batchSettings, element.entityId).subscribe(response => this.searchBatchs());
        }
        return undefined;
      }, display: true,
    } as SortTableAction<Batch>);
    this.batchDetailsActions.push({
      actionIcon: "error", actionName: "Voir le log", actionLinkFunction: (action: SortTableAction<Batch>, element: Batch) => {
        if (element && element.osirisLog) {
          return ['/administration/log', element.osirisLog.id];
        }
        return undefined;
      }, display: true,
    } as SortTableAction<Batch>);

    if (this.batchSearch)
      this.searchBatchs();
  }

  searchBatchs() {
    if (this.batchSearchForm.valid && this.batchSearch && this.batchSearch.startDate && this.batchSearch.endDate) {
      this.batchSearch.startDate = new Date(this.batchSearch.startDate);
      this.batchSearch.endDate = new Date(this.batchSearch.endDate);
      this.batchSearch.startDate.setHours(0);
      this.batchSearch.startDate.setMinutes(0);
      this.batchSearch.startDate.setSeconds(0);
      this.batchSearch.endDate.setHours(23);
      this.batchSearch.endDate.setMinutes(59);
      this.batchSearch.endDate.setSeconds(59);
      this.batchService.getBatchs(this.batchSearch).subscribe(response => {
        this.batchDetails = response;
      })
    }
  }

}
