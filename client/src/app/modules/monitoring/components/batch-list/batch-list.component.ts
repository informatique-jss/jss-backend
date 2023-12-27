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

  batchDetailsColomns: SortTableColumn[] = [];
  batchDetailsActions: SortTableAction[] = [];
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
    this.batchDetailsColomns = [];
    this.batchDetailsColomns.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn);
    this.batchDetailsColomns.push({ id: "batchSettings", fieldName: "batchSettings.label", label: "Batch" } as SortTableColumn);
    this.batchDetailsColomns.push({ id: "createdDate", fieldName: "createdDate", label: "Création", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.batchDetailsColomns.push({ id: "startDate", fieldName: "startDate", label: "Début", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.batchDetailsColomns.push({ id: "endDate", fieldName: "endDate", label: "Fin", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.batchDetailsColomns.push({ id: "time", fieldName: "time", label: "Durée", valueFonction: (element: Batch) => { return element.endDate ? (Math.round((new Date(element.endDate).getTime() - new Date(element.startDate).getTime()) / 1000 * 10) / 10) + "s" : null }, sortFonction: (element: Batch) => { return element.endDate ? (new Date(element.endDate).getTime() - new Date(element.startDate).getTime()) : 0 } } as SortTableColumn);
    this.batchDetailsColomns.push({ id: "batchStatus", fieldName: "batchStatus.label", label: "Status" } as SortTableColumn);
    this.batchDetailsColomns.push({ id: "entityId", fieldName: "entityId", label: "Identifiant de l'entité" } as SortTableColumn);
    this.batchDetailsColomns.push({ id: "node", fieldName: "node.hostname", label: "Noeud" } as SortTableColumn);
    this.batchDetailsColomns.push({ id: "osirisLog", fieldName: "osirisLog.id", label: "N° de log" } as SortTableColumn);

    this.batchDetailsActions.push({
      actionIcon: "check_circle", actionName: "Acquiter", actionClick: (action: SortTableAction, element: Batch) => {
        if (element && element.batchStatus.code == BATCH_STATUS_ERROR_CODE) {
          for (let batchStatu of this.batchStatus)
            if (batchStatu.code == BATCH_STATUS_ACKNOWLEDGE_CODE)
              element.batchStatus = batchStatu;
          this.batchService.addOrUpdateBatchStatus(element).subscribe(response => this.searchBatchs());
        }
        return undefined;
      }, display: true,
    } as SortTableAction);
    this.batchDetailsActions.push({
      actionIcon: "restart_alt", actionName: "Exécuter de nouveau", actionClick: (action: SortTableAction, element: Batch) => {
        if (element) {
          this.batchService.declareNewBatch(element.batchSettings, element.entityId).subscribe(response => this.searchBatchs());
        }
        return undefined;
      }, display: true,
    } as SortTableAction);
    this.batchDetailsActions.push({
      actionIcon: "error", actionName: "Voir le log", actionLinkFunction: (action: SortTableAction, element: Batch) => {
        if (element && element.osirisLog) {
          return ['/administration/log', element.osirisLog.id];
        }
        return undefined;
      }, display: true,
    } as SortTableAction);

    if (this.batchSearch)
      this.searchBatchs();
  }

  searchBatchs() {
    if (this.batchSearchForm.valid && this.batchSearch && this.batchSearch.startDate && this.batchSearch.endDate) {
      this.batchService.getBatchs(this.batchSearch).subscribe(response => {
        this.batchDetails = response;
      })
    }
  }

}
