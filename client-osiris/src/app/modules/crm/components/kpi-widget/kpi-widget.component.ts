import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { CountUpModule } from 'ngx-countup';
import { AGGREGATE_TYPE_LAST_VALUE } from '../../../../libs/Constants';
import { copyObject } from '../../../../libs/GenericHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { KpiCrm } from '../../../main/model/KpiCrm';
import { KpiCrmSearchModel } from '../../model/KpiCrmSearchModel';
import { KpiCrmValueService } from '../../services/kpi.crm.value.service';

@Component({
  selector: 'kpi-widget',
  templateUrl: './kpi-widget.component.html',
  styleUrls: ['./kpi-widget.component.css'],
  standalone: true,
  imports: [...SHARED_IMPORTS, CountUpModule, NgIcon]
})
export class KpiWidgetComponent implements OnInit {

  AGGREGATE_TYPE_LAST_VALUE = AGGREGATE_TYPE_LAST_VALUE;

  @Input() kpiCrm!: KpiCrm;
  @Input() searchModel!: KpiCrmSearchModel;
  value: number = 0;
  valueN1: number = 0;
  valueN2: number = 0;
  evolution: number = 0;
  evolutionIsGood: boolean = false;
  @Output() onDisplayDetails: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor(
    private kpiCrmValueService: KpiCrmValueService,
  ) { }

  ngOnInit() {
    this.searchForKpiCrmAndYearOffset(0);
    this.searchForKpiCrmAndYearOffset(1);
    this.searchForKpiCrmAndYearOffset(2);
  }

  displayDetails() {
    this.onDisplayDetails.next(true);
  }

  searchForKpiCrmAndYearOffset(yearOffset: number) {
    if (KpiWidgetComponent.isAllTiers(this.searchModel) || this.searchModel.tiersIds && this.searchModel.tiersIds.length > 0 && (!this.searchModel.responsableIds || this.searchModel.responsableIds.length == 0)) {
      this.kpiCrmValueService.getJobForAggregateValuesForTiersList(this.kpiCrm.code, this.generateSearchModelForApi(yearOffset))
        .subscribe(job => {
          let intervalId = setInterval(() => {
            this.kpiCrmValueService.getJobResultForAggregateValuesForTiersList(job.id).subscribe(jobResult => {
              if (jobResult && jobResult.status === 'DONE') {
                if (jobResult.result == undefined || jobResult.result == null)
                  jobResult.result = this.kpiCrm.defaultValue ? this.kpiCrm.defaultValue : 0;
                if (yearOffset == 0)
                  this.value = jobResult.result;
                if (yearOffset == 1)
                  this.valueN1 = jobResult.result;
                if (yearOffset == 2)
                  this.valueN2 = jobResult.result;

                if (this.value != undefined && this.value) {
                  let evolution = Math.ceil(((this.value - this.valueN1) / this.valueN1) * 1000) / 10;
                  this.evolution = evolution;
                  if (evolution && evolution > 0 && this.kpiCrm.isPositiveEvolutionGood)
                    this.evolutionIsGood = true;
                  else
                    this.evolutionIsGood = false;

                }
                clearInterval(intervalId);
              }
            })
          }, 600);
        })
    } else {
      this.kpiCrmValueService.getJobForAggregateValuesForResponsableList(this.kpiCrm.code, this.generateSearchModelForApi(yearOffset))
        .subscribe(job => {
          let intervalId = setInterval(() => {
            this.kpiCrmValueService.getJobResultForAggregateValuesForResponsableList(job.id).subscribe(jobResult => {
              if (jobResult && jobResult.status === 'DONE') {
                if (jobResult.result == undefined || jobResult.result == null)
                  jobResult.result = this.kpiCrm.defaultValue ? this.kpiCrm.defaultValue : 0;
                if (yearOffset == 0)
                  this.value = jobResult.result;
                if (yearOffset == 1)
                  this.valueN1 = jobResult.result;
                if (yearOffset == 2)
                  this.valueN2 = jobResult.result;

                if (this.value != undefined && this.valueN1) {
                  this.evolution = Math.ceil(((this.value - this.valueN1) / this.valueN1) * 1000) / 10;
                }
                clearInterval(intervalId);
              }
            })
          }, 300);
        })
    }
  }

  generateSearchModelForApi(yearOffset: number) {
    let newSearchModel = copyObject<KpiCrmSearchModel>(this.searchModel);
    if (newSearchModel.salesEmployee) {
      newSearchModel.salesEmployeeId = newSearchModel.salesEmployee.id;
      newSearchModel.salesEmployee = undefined;
    }
    if (newSearchModel.startDateKpis && yearOffset)
      newSearchModel.startDateKpis = KpiWidgetComponent.getPreviousYear(newSearchModel.startDateKpis, yearOffset);
    if (newSearchModel.endDateKpis && yearOffset)
      newSearchModel.endDateKpis = KpiWidgetComponent.getPreviousYear(newSearchModel.endDateKpis, yearOffset);
    newSearchModel.isAllTiers = KpiWidgetComponent.isAllTiers(this.searchModel);
    return newSearchModel;
  }


  static getPreviousYear(date: Date, offsetYear: number): Date {
    date = new Date(date);
    const year = date.getFullYear() - offsetYear;
    const month = date.getMonth();
    const day = date.getDate();

    const previousYearDate = new Date(year, month, day);

    if (previousYearDate.getMonth() !== month) {
      return new Date(year, month + 1, 0);
    }

    return previousYearDate;
  }

  static generateSearchModelForApi(searchModel: KpiCrmSearchModel, yearOffset: number) {
    let newSearchModel = copyObject<KpiCrmSearchModel>(searchModel);
    if (newSearchModel.salesEmployee) {
      newSearchModel.salesEmployeeId = newSearchModel.salesEmployee.id;
      newSearchModel.salesEmployee = undefined;
    }
    if (newSearchModel.startDateKpis && yearOffset)
      newSearchModel.startDateKpis = this.getPreviousYear(newSearchModel.startDateKpis, yearOffset);
    if (newSearchModel.endDateKpis && yearOffset)
      newSearchModel.endDateKpis = this.getPreviousYear(newSearchModel.endDateKpis, yearOffset);
    newSearchModel.isAllTiers = this.isAllTiers(searchModel);
    return newSearchModel;
  }

  static isAllTiers(searchModel: KpiCrmSearchModel) {
    return !searchModel.tiersIds && !searchModel.responsableIds || searchModel.tiersIds.length == 0 && searchModel.responsableIds.length == 0;
  }
}
