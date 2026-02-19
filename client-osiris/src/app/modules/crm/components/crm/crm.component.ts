import { Component, OnInit, TemplateRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NgbNavModule, NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from '@ng-icons/core';
import { provideEchartsCore } from 'ngx-echarts';
import { combineLatest } from 'rxjs';
import { SimplebarAngularModule } from 'simplebar-angular';
import { getPreviousYear } from '../../../../libs/DateHelper';
import { EchartComponent } from '../../../../libs/echart/echart.component';
import { GenericForm } from '../../../../libs/generic-list/GenericForm';
import { GenericSearchForm } from '../../../../libs/generic-list/GenericSearchForm';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { PageTitleComponent } from '../../../main/components/page-title/page-title.component';
import { RestUserPreferenceService } from '../../../main/services/rest.user.preference.service';
import { GenericFormComponent } from '../../../miscellaneous/forms/components/generic-form/generic-form.component';
import { ResponsableService } from '../../../tiers/services/responsable.service';
import { TiersService } from '../../../tiers/services/tiers.service';
import { KpiCrm } from '../../model/KpiCrm';
import { KpiCrmCategory } from '../../model/KpiCrmCategory';
import { KpiCrmSearchModel } from '../../model/KpiCrmSearchModel';
import { KpiCrmCategoryService } from '../../services/kpi.crm.category.service';
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
  selectedKpiCrmCategory: KpiCrmCategory | undefined;
  kpiCrmCategories: KpiCrmCategory[] = [];
  isLoading = true;
  series: any[] = [];
  searchModel: KpiCrmSearchModel = {
    endDateKpis: undefined,
    startDateKpis: undefined,
    salesEmployeeId: undefined,
    kpiCrmKey: '',
    tiersIds: [],
    responsableIds: [],
    isAllTiers: false,
    kpiScale: 'MONTHLY'
  } as KpiCrmSearchModel;

  loadBookarks: boolean = true;

  searchForm: FormGroup = {} as FormGroup;
  Validators = Validators;
  forms: GenericSearchForm<any>[] = [];
  kpiCrms: KpiCrm[] | undefined;
  selectedKpiCode: string | undefined;

  constructor(
    private offcanvasService: NgbOffcanvas,
    private restUserPreferenceService: RestUserPreferenceService,
    private formBuilder: FormBuilder,
    private kpiCrmValueService: KpiCrmValueService,
    private tiersService: TiersService,
    private kpiCrmService: KpiCrmService,
    private responsableService: ResponsableService,
    private activeRoute: ActivatedRoute,
    private kpiCrmCategoryService: KpiCrmCategoryService,
  ) { }

  ngOnInit() {
    this.searchForm = this.formBuilder.group({});
    this.selectedKpiCode = this.activeRoute.snapshot.params["kpiCode"] != "UNDEFINED" ? this.activeRoute.snapshot.params["kpiCode"] : undefined;
    this.searchForm.valueChanges.subscribe(res => this.isLoading = false);
    this.forms = [
      {
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
      }, {
        accessorKey: "salesEmployee",
        form: {
          label: 'Commercial',
          type: 'select',
          selectType: 'commercial',
        } as GenericForm<any>
      }];

    // Restore bookmark
    if (this.activeRoute.snapshot.url.toString().indexOf(",selection") >= 0) {
      let selectedTiers = this.tiersService.getCurrentSelectedTiers();
      if (selectedTiers && selectedTiers.length > 0) {
        this.searchModel.tiersIds = selectedTiers.map(t => t.id);
        if (this.tiersService.getSelectedKpiStartDate())
          this.searchModel.startDateKpis = new Date(this.tiersService.getSelectedKpiStartDate()!);
        if (this.tiersService.getSelectedKpiEndDate())
          this.searchModel.endDateKpis = new Date(this.tiersService.getSelectedKpiEndDate()!);

        if (this.searchModel.startDateKpis)
          this.loadBookarks = false;
      }
      let selectedResponsables = this.responsableService.getCurrentSelectedResponsable();
      if (selectedResponsables && selectedResponsables.length > 0) {
        this.searchModel.responsableIds = selectedResponsables.map(r => r.id);
        if (this.responsableService.getSelectedKpiStartDate())
          this.searchModel.startDateKpis = new Date(this.responsableService.getSelectedKpiStartDate()!);
        if (this.responsableService.getSelectedKpiEndDate())
          this.searchModel.endDateKpis = new Date(this.responsableService.getSelectedKpiEndDate()!);

        if (this.searchModel.startDateKpis)
          this.loadBookarks = false;
      }
    }



    this.kpiCrmService.getKpiCrm().subscribe(kpiCrms => {
      this.kpiCrms = kpiCrms;

      this.kpiCrmCategoryService.getKpiCrmCategories().subscribe(response => {
        this.kpiCrmCategories = response;
        if (!this.selectedKpiCrmCategory)
          if (!this.selectedKpiCode)
            this.selectCategory(this.kpiCrmCategories[0]);
          else
            for (let k of kpiCrms) {
              if (k.code == this.selectedKpiCode) {
                this.selectedKpiCrm = k;
                for (let c of this.kpiCrmCategories)
                  if (c.code == k.kpiCrmCategory.code)
                    this.selectCategory(c, false);
              }
            }
      })

      if (this.loadBookarks) {
        this.restUserPreferenceService.getUserPreferenceValue(this.screenCode).subscribe(response => {
          if (response) {
            this.searchModel = JSON.parse(response) as any;
            this.searchModel.responsableIds = [];
            this.searchModel.tiersIds = [];
            this.search(undefined);
          }
        })
      } else {
        this.search(undefined);
      }
    })
  }

  open(content: TemplateRef<any>) {
    this.offcanvasService.open(content, { panelClass: 'asidebar border-start overflow-hidden', position: 'end' })
  }

  clearFilters() {
    this.restUserPreferenceService.setUserPreference(JSON.stringify(this.searchModel), this.screenCode).subscribe();
  }

  search(content: TemplateRef<any> | undefined) {
    this.searchForm.markAllAsTouched();

    if (this.searchForm.valid) {
      if (!this.searchModel.startDateKpis || !this.searchModel.endDateKpis)
        return;

      this.isLoading = true;
      if (this.selectedKpiCrm)
        this.displayDetails(this.selectedKpiCrm);
      this.restUserPreferenceService.setUserPreference(JSON.stringify(this.searchModel), this.screenCode).subscribe();
      if (content)
        this.offcanvasService.dismiss(content);
    }
  }

  displayDetails(kpiCrm: KpiCrm | undefined) {
    this.selectedKpiCrm = kpiCrm;
    if (!this.searchModel.startDateKpis || !this.searchModel.endDateKpis || !this.selectedKpiCrm) {
      this.series = [];
      return;
    }

    this.restUserPreferenceService.setUserPreference(JSON.stringify(this.searchModel), this.screenCode).subscribe();
    this.series = [];
    let promises;
    if (KpiWidgetComponent.isAllTiers(this.searchModel) || this.searchModel.tiersIds && this.searchModel.tiersIds.length > 0 && (!this.searchModel.responsableIds || this.searchModel.responsableIds.length == 0)) {
      promises = [
        this.kpiCrmValueService.getKpiCrmValuePayloadAggregatedByTiersAndDate(this.selectedKpiCrm.code, KpiWidgetComponent.generateSearchModelForApi(this.searchModel, 0)),
        this.kpiCrmValueService.getKpiCrmValuePayloadAggregatedByTiersAndDate(this.selectedKpiCrm.code, KpiWidgetComponent.generateSearchModelForApi(this.searchModel, 1)),
        this.kpiCrmValueService.getKpiCrmValuePayloadAggregatedByTiersAndDate(this.selectedKpiCrm.code, KpiWidgetComponent.generateSearchModelForApi(this.searchModel, 2)),
      ]
    } else {
      promises = [this.kpiCrmValueService.getKpiCrmValuePayloadAggregatedByResponsableAndDate(this.selectedKpiCrm.code, KpiWidgetComponent.generateSearchModelForApi(this.searchModel, 0)),
      this.kpiCrmValueService.getKpiCrmValuePayloadAggregatedByResponsableAndDate(this.selectedKpiCrm.code, KpiWidgetComponent.generateSearchModelForApi(this.searchModel, 1)),
      this.kpiCrmValueService.getKpiCrmValuePayloadAggregatedByResponsableAndDate(this.selectedKpiCrm.code, KpiWidgetComponent.generateSearchModelForApi(this.searchModel, 2)),
      ]
    }
    combineLatest(promises).subscribe(series => {
      let serieN = JSON.parse(series[0].json)[0];
      serieN.name = new Date(this.searchModel.endDateKpis!).getFullYear();
      let serieN1 = JSON.parse(series[1].json)[0];
      this.shiftDate(serieN1.data, -1);
      serieN1.name = new Date(this.searchModel.endDateKpis!).getFullYear() - 1;
      let serieN2 = JSON.parse(series[2].json)[0];
      this.shiftDate(serieN2.data, -2);
      serieN2.name = new Date(this.searchModel.endDateKpis!).getFullYear() - 2;

      this.series = [serieN, serieN1, serieN2];
    });
  }

  shiftDate(serie: any, offset: number) {
    if (serie && serie.length > 0)
      for (let d of serie) {
        d[0] = getPreviousYear(new Date(d[0]), offset)
      }
  }

  toggleGraphScale(scale: string) {
    this.searchModel.kpiScale = scale;
    if (this.selectedKpiCrm)
      this.displayDetails(this.selectedKpiCrm);
  }

  selectCategory(category: KpiCrmCategory, selectFirstKpi = true) {
    this.selectedKpiCrmCategory = category;
    if (this.kpiCrms && selectFirstKpi)
      for (let kpiCrm of this.kpiCrms)
        if (kpiCrm && kpiCrm.kpiCrmCategory && kpiCrm.kpiCrmCategory.id == category.id) {
          this.displayDetails(kpiCrm);
          return;
        }
    this.displayDetails(this.selectedKpiCrm);
  }
}
