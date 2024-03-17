import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable, Subject, retry, share, switchMap, takeUntil, tap, timer } from 'rxjs';
import { BATCH_STATUS_ERROR_CODE, SUPERVISION_REFRESH_INTERVAL } from 'src/app/libs/Constants';
import { BatchSearch } from '../../model/BatchSearch';
import { BatchSettings } from '../../model/BatchSettings';
import { BatchStatistics } from '../../model/BatchStatictics';
import { BatchStatus } from '../../model/BatchStatus';
import { BatchTimeStatistics } from '../../model/BatchTimeStatistics';
import { BatchSettingsService } from '../../services/batch.settings.service';
import { BatchStatisticsService } from '../../services/batch.statistics.settings.service';
import { BatchStatusService } from '../../services/batch.status.service';
import { BatchTimeStatisticsService } from '../../services/batch.time.statistics.settings.service';
import { MonitoringSummaryComponent } from '../monitoring-summary/monitoring-summary.component';

@Component({
  selector: 'monitoring-details',
  templateUrl: './monitoring-details.component.html',
  styleUrls: ['./monitoring-details.component.css']
})
export class MonitoringDetailsComponent implements OnInit {

  @Input() selectedBatchSetting: BatchSettings | undefined;
  batchSearch: BatchSearch | undefined;
  batchStatistics: BatchStatistics | undefined;
  batchTimeStatistics: BatchTimeStatistics[] | undefined;
  statisticsNotifications: Observable<BatchStatistics[]> | undefined;
  statisticsTimeNotifications: Observable<BatchTimeStatistics[]> | undefined;
  private stopPolling = new Subject();
  isItWarn = MonitoringSummaryComponent.isItWarn;
  batchStatusError: BatchStatus | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private batchSettingsService: BatchSettingsService,
    private batchStatisticsService: BatchStatisticsService,
    private batchTimeStatisticsService: BatchTimeStatisticsService,
    private batchStatusService: BatchStatusService
  ) {
    this.statisticsNotifications = timer(1, SUPERVISION_REFRESH_INTERVAL).pipe(
      switchMap(() => this.batchStatisticsService.getBatchStatistics()),
      retry(),
      tap((value) => {
        if (value && this.selectedBatchSetting)
          for (let val of value)
            if (val.idBatchSettings == this.selectedBatchSetting.id)
              this.batchStatistics = val;
      }),
      share(),
      takeUntil(this.stopPolling)
    );
    this.statisticsTimeNotifications = timer(1, SUPERVISION_REFRESH_INTERVAL).pipe(
      switchMap(() => this.batchTimeStatisticsService.getBatchTimeStatistics(this.selectedBatchSetting!)),
      retry(),
      tap((value) => {
        if (value && this.selectedBatchSetting) {
          this.batchTimeStatistics = value;
          this.setTimeFirstSerieColors();
          this.setTimeFirstSerieData();
          this.setTimeSecondSerieData();
        }
      }),
      share(),
      takeUntil(this.stopPolling)
    );
  }

  batchDetailsForm = this.formBuilder.group({});
  editMode: boolean = false;

  ngOnInit() {
    if (this.statisticsNotifications)
      this.statisticsNotifications.subscribe();
    if (this.statisticsTimeNotifications)
      this.statisticsTimeNotifications.subscribe();
  }

  ngOnDestroy() {
    this.stopPolling.next(false);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes && changes.selectedBatchSetting) {
      this.batchSearch = undefined;
      this.batchStatistics = undefined;
      this.batchTimeStatistics = undefined;
      setTimeout(() => {
        let newSearch = { batchSettings: [this.selectedBatchSetting!], startDate: new Date(), endDate: new Date(), batchStatus: [this.batchStatusError] } as any;
        if (!this.batchStatusError)
          this.batchStatusService.getBatchStatus().subscribe(allStatus => {
            for (let s of allStatus)
              if (s.code == BATCH_STATUS_ERROR_CODE)
                this.batchStatusError = s;
            newSearch.batchStatus = [this.batchStatusError];
            this.batchSearch = newSearch;
          })
        else
          this.batchSearch = newSearch;
      }, 0);
    }
  }

  editBatchSettings() {
    this.editMode = true;
  }

  saveBatchSettings(): boolean {
    if (this.batchDetailsForm.valid && this.selectedBatchSetting) {
      this.batchSettingsService.addOrUpdateBatchSetting(this.selectedBatchSetting).subscribe(response => {
        this.selectedBatchSetting = response;
        this.editMode = false;
      })
    }
    return false;
  }

  // Execution time chart
  timeFirstSerieData: Array<Array<Object>> = [] as Array<Array<number>>;
  timeFirstSerieColors: Array<string> = [] as Array<string>;
  timeSecondSerieData: Array<Array<Object>> = [] as Array<Array<number>>;

  setTimeFirstSerieData() {
    this.timeFirstSerieData = [] as Array<Array<number>>;
    if (this.batchTimeStatistics && this.batchTimeStatistics.length > 0) {
      this.batchTimeStatistics.forEach(measure => {
        this.timeFirstSerieData.push([new Date(measure.dateTime), measure.nbr]);
      });
    }
  }

  setTimeFirstSerieColors() {
    this.timeFirstSerieColors = [] as Array<string>;
    if (this.batchTimeStatistics && this.batchTimeStatistics.length > 0) {
      this.batchTimeStatistics.forEach(measure => {
        if (measure.hasError)
          this.timeFirstSerieColors.push("#e74c3c ");
        else
          this.timeFirstSerieColors.push("#27ae60 ");
      });
    }
  }

  setTimeSecondSerieData() {
    this.timeSecondSerieData = [] as Array<Array<number>>;
    let total = 0;
    let number = 0;
    if (this.batchTimeStatistics && this.batchTimeStatistics.length > 0) {
      for (let measure of this.batchTimeStatistics) {
        this.timeSecondSerieData.push([new Date(measure.dateTime), Math.trunc(measure.meanTime * 10) / 10]);
      }
    }
  }

}
