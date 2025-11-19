import { Component, OnInit, TemplateRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbNavModule, NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from '@ng-icons/core';
import { provideEchartsCore } from 'ngx-echarts';
import { combineLatest } from 'rxjs';
import { SimplebarAngularModule } from 'simplebar-angular';
import { EchartComponent } from '../../../../libs/echart/echart.component';
import { GenericForm } from '../../../../libs/generic-list/GenericForm';
import { GenericSearchForm } from '../../../../libs/generic-list/GenericSearchForm';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { PageTitleComponent } from '../../../main/components/page-title/page-title.component';
import { KpiCrm } from '../../../main/model/KpiCrm';
import { AppService } from '../../../main/services/app.service';
import { RestUserPreferenceService } from '../../../main/services/rest.user.preference.service';
import { GenericFormComponent } from '../../../miscellaneous/forms/components/generic-form/generic-form.component';
import { ResponsableDto } from '../../../tiers/model/ResponsableDto';
import { TiersDto } from '../../../tiers/model/TiersDto';
import { ResponsableService } from '../../../tiers/services/responsable.service';
import { TiersService } from '../../../tiers/services/tiers.service';
import { KpiCrmService } from '../../services/kpi.crm.service';
import { KpiCrmValueService } from '../../services/kpi.crm.value.service';
import { KpiWidgetComponent } from '../kpi-widget/kpi-widget.component';
import { echarts } from './echarts-config';

@Component({
  selector: 'crm',
  templateUrl: './crm.component.html',
  styleUrls: ['./crm.component.scss'],
  providers: [provideEchartsCore({ echarts })],
  imports: [SHARED_IMPORTS,
    PageTitleComponent,
    PageTitleComponent,
    NgIcon,
    SimplebarAngularModule,
    EchartComponent,
    NgbNavModule,
    GenericFormComponent,
    KpiWidgetComponent],
  standalone: true,
})
export class CrmComponent implements OnInit {
  screenCode = 'KPI_CRM';

  selectedKpiCrm: KpiCrm | undefined;
  series: any[] = [];
  searchModel: any | undefined = {}

  searchForm: FormGroup = {} as FormGroup;
  Validators = Validators;
  forms: GenericSearchForm<any>[] = [];
  selectedTiers: TiersDto[] = [];
  selectedResponsables: ResponsableDto[] = [];
  kpiCrms: KpiCrm[] | undefined;
  kpiCrmsValues: Record<string, number> = {};
  kpiCrmsValuesN1: Record<string, number> = {};
  kpiCrmsValuesN2: Record<string, number> = {};
  kpiCrmsValuesEvolution: Record<string, number> = {};
  kpiCrmsValuesEvolutionGood: Record<string, boolean> = {};

  constructor(
    private offcanvasService: NgbOffcanvas,
    private restUserPreferenceService: RestUserPreferenceService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private kpiCrmValueService: KpiCrmValueService,
    private tiersService: TiersService,
    private kpiCrmService: KpiCrmService,
    private responsableService: ResponsableService
  ) { }

  ngOnInit() {
    this.selectedTiers = this.tiersService.getCurrentSelectedTiers();
    this.selectedResponsables = this.responsableService.getCurrentSelectedResponsable();
    this.searchForm = this.formBuilder.group({});
    this.forms = [{
      accessorKey: "startDateKpis",
      form: {
        label: 'Date de début de calcul',
        type: 'input',
        inputType: 'date',
        validators: [Validators.required],
        errorMessages: {
          required: 'La date de début est obligatoire'
        } as Record<string, string>
      } as GenericForm<any>
    } as GenericSearchForm<any>,
    {
      accessorKey: "endDateKpis",
      form: {
        label: 'Date de fin de calcul',
        type: 'input',
        inputType: 'date',
        validators: [Validators.required],
        errorMessages: {
          required: 'La date de fin est obligatoire'
        } as Record<string, string>
      } as GenericForm<any>
    }];

    // Restore bookmark
    this.initSearchModel();
    this.kpiCrmService.getKpiCrm().subscribe(kpiCrms => {
      this.kpiCrms = kpiCrms;
      this.restUserPreferenceService.getUserPreferenceValue(this.screenCode).subscribe(response => {
        if (response) {
          this.searchModel = JSON.parse(response) as any;
          this.search(undefined);
        }
      })
    })
  }

  open(content: TemplateRef<any>) {
    this.offcanvasService.open(content, { panelClass: 'asidebar border-start overflow-hidden', position: 'end' })
  }

  clearFilters() {
    this.initSearchModel();
    this.restUserPreferenceService.setUserPreference(JSON.stringify(this.searchModel), this.screenCode).subscribe();
  }

  initSearchModel() {
    this.searchModel = { 'endDateKpis': undefined, 'startDateKpis': undefined }
  }

  displayDetails(kpiCrm: KpiCrm) {
    this.selectedKpiCrm = kpiCrm;
    if (!this.searchModel.startDateKpis || !this.searchModel.endDateKpis || !this.selectedKpiCrm)
      return;

    this.series = [];
    let isAllTiers = !this.selectedTiers && !this.selectedResponsables || this.selectedTiers.length == 0 && this.selectedResponsables.length == 0;
    let promises;
    if (isAllTiers || this.selectedTiers && this.selectedTiers.length > 0 && (!this.selectedResponsables || this.selectedResponsables.length == 0)) {
      promises = [this.kpiCrmValueService.getKpiCrmValuePayloadAggregatedByTiersAndDate(this.selectedKpiCrm.code, this.getPreviousYear(this.searchModel.startDateKpis, 0), this.getPreviousYear(this.searchModel.endDateKpis, 0), this.selectedTiers.map(t => t.id), !this.selectedTiers || this.selectedTiers.length == 0),
      this.kpiCrmValueService.getKpiCrmValuePayloadAggregatedByTiersAndDate(this.selectedKpiCrm.code, this.getPreviousYear(this.searchModel.startDateKpis, 1), this.getPreviousYear(this.searchModel.endDateKpis, 1), this.selectedTiers.map(t => t.id), !this.selectedTiers || this.selectedTiers.length == 0),
      this.kpiCrmValueService.getKpiCrmValuePayloadAggregatedByTiersAndDate(this.selectedKpiCrm.code, this.getPreviousYear(this.searchModel.startDateKpis, 2), this.getPreviousYear(this.searchModel.endDateKpis, 2), this.selectedTiers.map(t => t.id), !this.selectedTiers || this.selectedTiers.length == 0),
      ]
    } else {
      promises = [this.kpiCrmValueService.getKpiCrmValuePayloadAggregatedByResponsableAndDate(this.selectedKpiCrm.code, this.getPreviousYear(this.searchModel.startDateKpis, 0), this.getPreviousYear(this.searchModel.endDateKpis, 0), this.selectedResponsables.map(t => t.id), false),
      this.kpiCrmValueService.getKpiCrmValuePayloadAggregatedByResponsableAndDate(this.selectedKpiCrm.code, this.getPreviousYear(this.searchModel.startDateKpis, 1), this.getPreviousYear(this.searchModel.endDateKpis, 1), this.selectedResponsables.map(t => t.id), false),
      this.kpiCrmValueService.getKpiCrmValuePayloadAggregatedByResponsableAndDate(this.selectedKpiCrm.code, this.getPreviousYear(this.searchModel.startDateKpis, 2), this.getPreviousYear(this.searchModel.endDateKpis, 2), this.selectedResponsables.map(t => t.id), false),
      ]
    }
    combineLatest(promises).subscribe(series => {
      let serieN = JSON.parse(series[0].json)[0];
      serieN.name = new Date(this.searchModel.endDateKpis).getFullYear();
      let serieN1 = JSON.parse(series[1].json)[0];
      this.shiftDate(serieN1.data, -1);
      serieN1.name = new Date(this.searchModel.endDateKpis).getFullYear() - 1;
      let serieN2 = JSON.parse(series[2].json)[0];
      this.shiftDate(serieN2.data, -2);
      serieN2.name = new Date(this.searchModel.endDateKpis).getFullYear() - 2;

      this.series = [serieN, serieN1, serieN2];
    });
  }

  shiftDate(serie: any, offset: number) {
    if (serie && serie.length > 0)
      for (let d of serie) {
        d[0] = this.getPreviousYear(new Date(d[0]), offset)
      }
  }

  search(content: TemplateRef<any> | undefined) {
    this.searchForm.markAllAsTouched();

    if (this.searchForm.valid) {
      this.restUserPreferenceService.setUserPreference(JSON.stringify(this.searchModel), this.screenCode).subscribe();
      if (content)
        this.offcanvasService.dismiss(content);
      if (this.kpiCrms) {
        this.kpiCrmsValues = {};
        this.kpiCrmsValuesN1 = {};
        this.kpiCrmsValuesN2 = {};
        this.kpiCrmsValuesEvolutionGood = {};
        this.kpiCrmsValuesEvolution = {};
        for (let kpiCrm of this.kpiCrms) {
          this.searchForKpiCrmAndYearOffset(kpiCrm, 0, !this.selectedTiers && !this.selectedResponsables || this.selectedTiers.length == 0 && this.selectedResponsables.length == 0);
          this.searchForKpiCrmAndYearOffset(kpiCrm, 1, !this.selectedTiers && !this.selectedResponsables || this.selectedTiers.length == 0 && this.selectedResponsables.length == 0);
          this.searchForKpiCrmAndYearOffset(kpiCrm, 2, !this.selectedTiers && !this.selectedResponsables || this.selectedTiers.length == 0 && this.selectedResponsables.length == 0);
        }
      }
    }
  }

  searchForKpiCrmAndYearOffset(kpiCrm: KpiCrm, yearOffset: number, isAllTiers: boolean) {
    if (isAllTiers || this.selectedTiers && this.selectedTiers.length > 0 && (!this.selectedResponsables || this.selectedResponsables.length == 0)) {
      this.kpiCrmValueService.getJobForAggregateValuesForTiersList(kpiCrm.code, this.getPreviousYear(this.searchModel.startDateKpis, yearOffset), this.getPreviousYear(this.searchModel.endDateKpis, yearOffset), this.selectedTiers.map(t => t.id), isAllTiers)
        .subscribe(job => {
          let intervalId = setInterval(() => {
            this.kpiCrmValueService.getJobResultForAggregateValuesForTiersList(job.id).subscribe(jobResult => {
              let status = jobResult.status;
              if (status === 'DONE') {
                if (jobResult.result == undefined || jobResult.result == null)
                  jobResult.result = kpiCrm.defaultValue ? kpiCrm.defaultValue : 0;
                if (yearOffset == 0)
                  this.kpiCrmsValues[kpiCrm.code] = jobResult.result;
                if (yearOffset == 1)
                  this.kpiCrmsValuesN1[kpiCrm.code] = jobResult.result;
                if (yearOffset == 2)
                  this.kpiCrmsValuesN2[kpiCrm.code] = jobResult.result;

                if (this.kpiCrmsValues[kpiCrm.code] != undefined && this.kpiCrmsValuesN1[kpiCrm.code]) {
                  let evolution = Math.ceil(((this.kpiCrmsValues[kpiCrm.code] - this.kpiCrmsValuesN1[kpiCrm.code]) / this.kpiCrmsValuesN1[kpiCrm.code]) * 1000) / 10;
                  this.kpiCrmsValuesEvolution[kpiCrm.code] = evolution;
                  if (evolution && evolution > 0 && kpiCrm.isPositiveEvolutionGood)
                    this.kpiCrmsValuesEvolutionGood[kpiCrm.code] = true;
                  else
                    this.kpiCrmsValuesEvolutionGood[kpiCrm.code] = false;

                }
                clearInterval(intervalId);
              }
            })
          }, 300);
        })
    } else {
      this.kpiCrmValueService.getJobForAggregateValuesForResponsableList(kpiCrm.code, this.getPreviousYear(this.searchModel.startDateKpis, yearOffset), this.getPreviousYear(this.searchModel.endDateKpis, yearOffset), this.selectedResponsables.map(t => t.id), false)
        .subscribe(job => {
          let intervalId = setInterval(() => {
            this.kpiCrmValueService.getJobResultForAggregateValuesForResponsableList(job.id).subscribe(jobResult => {
              let status = jobResult.status;
              if (status === 'DONE') {
                if (jobResult.result == undefined || jobResult.result == null)
                  jobResult.result = kpiCrm.defaultValue ? kpiCrm.defaultValue : 0;
                if (yearOffset == 0)
                  this.kpiCrmsValues[kpiCrm.code] = jobResult.result;
                if (yearOffset == 1)
                  this.kpiCrmsValuesN1[kpiCrm.code] = jobResult.result;
                if (yearOffset == 2)
                  this.kpiCrmsValuesN2[kpiCrm.code] = jobResult.result;

                if (this.kpiCrmsValues[kpiCrm.code] != undefined && this.kpiCrmsValuesN1[kpiCrm.code]) {
                  this.kpiCrmsValuesEvolution[kpiCrm.code] = Math.ceil(((this.kpiCrmsValues[kpiCrm.code] - this.kpiCrmsValuesN1[kpiCrm.code]) / this.kpiCrmsValuesN1[kpiCrm.code]) * 1000) / 10;
                }
                clearInterval(intervalId);
              }
            })
          }, 300);
        })
    }
  }

  getPreviousYear(date: Date, offsetYear: number): Date {
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
}
