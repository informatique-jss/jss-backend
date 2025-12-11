import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbNavModule, NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from '@ng-icons/core';
import { Row } from '@tanstack/angular-table';
import { Observable, Subject } from 'rxjs';
import { SimplebarAngularModule } from 'simplebar-angular';
import { GenericListComponent } from '../../../../libs/generic-list/generic-list.component';
import { GenericForm } from '../../../../libs/generic-list/GenericForm';
import { GenericSearchForm } from '../../../../libs/generic-list/GenericSearchForm';
import { GenericSearchTab } from '../../../../libs/generic-list/GenericSearchTab';
import { GenericTableAction } from '../../../../libs/generic-list/GenericTableAction';
import { GenericTableColumn } from '../../../../libs/generic-list/GenericTableColumn';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { TanstackTableComponent } from '../../../../libs/tanstack-table/tanstack-table.component';
import { KpiCrm } from '../../../crm/model/KpiCrm';
import { KpiCrmService } from '../../../crm/services/kpi.crm.service';
import { PageTitleComponent } from '../../../main/components/page-title/page-title.component';
import { AppService } from '../../../main/services/app.service';
import { RestUserPreferenceService } from '../../../main/services/rest.user.preference.service';
import { GenericFormComponent } from '../../../miscellaneous/forms/components/generic-form/generic-form.component';
import { ResponsableDto } from '../../model/ResponsableDto';
import { ResponsableSearch } from '../../model/ResponsableSearch';
import { ResponsableService } from '../../services/responsable.service';
import { TiersService } from '../../services/tiers.service';

@Component({
  selector: 'app-responsable-list',
  templateUrl: './../../../../libs/generic-list/generic-list.component.html',
  styleUrls: ['./../../../../libs/generic-list/generic-list.component.css'],
  imports: [...SHARED_IMPORTS, TanstackTableComponent, PageTitleComponent, NgIcon, SimplebarAngularModule, NgbNavModule, GenericFormComponent],
  standalone: true
})
export class ResponsableListComponent extends GenericListComponent<ResponsableDto, ResponsableSearch> implements OnInit {

  eventOnClickOpenAction = new Subject<Row<ResponsableDto>[]>();
  eventOnClickOpenTiersAction = new Subject<Row<ResponsableDto>[]>();
  eventOnClickOpenKpisAction = new Subject<Row<ResponsableDto>[]>();
  eventOnClickOpenResponsable = new Subject<Row<ResponsableDto>>();

  kpiCrms: KpiCrm[] | undefined;

  constructor(private offcanvasService2: NgbOffcanvas,
    private formBuilder2: FormBuilder,
    private appService2: AppService,
    private restUserPreferenceService2: RestUserPreferenceService,
    private router: Router,
    private responsableService: ResponsableService,
    private kpiCrmService: KpiCrmService,
    private tiersService: TiersService
  ) {
    super(offcanvasService2, formBuilder2, appService2, restUserPreferenceService2);

  }

  override ngOnInit(): void {
    this.pageTitle = "Liste des responsables";
    this.breadcrumbPaths = [
      { label: "Liste des responsables", route: "/responsables" },
    ]

    this.kpiCrmService.getKpiCrm().subscribe(reponse => {
      this.kpiCrms = reponse;
      super.ngOnInit();
    })
  }

  override generateActions(): GenericTableAction<ResponsableDto>[] {
    let actions = [] as GenericTableAction<ResponsableDto>[];
    actions.push({
      label: 'Voir le responsable',
      eventOnClick: this.eventOnClickOpenAction,
      maxNumberOfElementsRequiredToDisplay: 1,
      minNumberOfElementsRequiredToDisplay: 1
    })
    actions.push({
      label: 'Voir le tiers',
      eventOnClick: this.eventOnClickOpenTiersAction,
      maxNumberOfElementsRequiredToDisplay: 1,
      minNumberOfElementsRequiredToDisplay: 1
    })
    actions.push({
      label: 'Voir les KPIs pour ces responsables',
      eventOnClick: this.eventOnClickOpenKpisAction,
      minNumberOfElementsRequiredToDisplay: 1
    })

    this.eventOnClickOpenAction.subscribe((row: Row<ResponsableDto>[]) => {
      this.responsableService.setSelectedResponsableUnique(row[0].original);
      this.router.navigate(['tiers/view/' + row[0].original.tiersId + '/responsable/' + row[0].original.id]);
    });
    this.eventOnClickOpenTiersAction.subscribe((row: Row<ResponsableDto>[]) => {
      this.responsableService.setSelectedResponsableUnique(row[0].original);
      this.router.navigate(['tiers/view/' + row[0].original.tiersId]);
    });

    this.eventOnClickOpenKpisAction.subscribe((row: Row<ResponsableDto>[]) => {
      this.responsableService.setCurrentSelectedResponsable(row.map(r => r.original));
      this.responsableService.setSelectedKpiStartDate(this.searchModel.startDateKpis);
      this.responsableService.setSelectedKpiEndDate(this.searchModel.endDateKpis);
      this.tiersService.clearKpiSelection();
      this.router.navigate(['tiers/crm/kpi/selection/' + this.getFirstKpiCodeDefined()]);
    });

    this.eventOnClickOpenResponsable.subscribe((row: Row<ResponsableDto>) => {
      this.responsableService.setSelectedResponsableUnique(row.original);
      this.router.navigate(['tiers/view/' + row.original.tiersId + '/responsable/' + row.original.id]);
    });

    return actions;
  }

  override   getListCode(): string {
    return 'RESPONSABLE_LIST';
  }

  getFirstKpiCodeDefined() {
    if (this.searchModel && this.searchModel.kpis)
      for (let key of Object.keys(this.searchModel.kpis)) {
        if (this.searchModel.kpis[key].minValue != undefined || this.searchModel.kpis[key].maxValue != undefined)
          return key;
      }
    return "UNDEFINED";
  }

  override generateSearchTabs(): GenericSearchTab<ResponsableSearch>[] {
    let searchTabs = [] as GenericSearchTab<ResponsableSearch>[];
    searchTabs.push({
      label: "Généraux",
      icon: 'tablerFilter',
      forms: [
        {
          accessorKey: "label",
          form: {
            label: 'Dénomination du responsable',
            type: 'input',
            inputType: 'text',
            validators: [Validators.minLength(3)],
            errorMessages: {
              minlength: 'Entrez au moins 3 caractères'
            } as Record<string, string>
          } as GenericForm<ResponsableSearch>
        } as GenericSearchForm<ResponsableSearch>,
        {
          accessorKey: "salesEmployee",
          form: {
            label: 'Commercial',
            type: 'select',
            selectType: 'commercial',
            validators: [Validators.required],
            errorMessages: {
              required: 'Sélectionnez au moins un commercial'
            } as Record<string, string>
          } as GenericForm<ResponsableSearch>
        } as GenericSearchForm<ResponsableSearch>,
        {
          accessorKey: "mail",
          form: {
            label: 'Mail du responsable',
            type: 'input',
            inputType: 'text',
            validators: [Validators.email],
            errorMessages: {
              email: 'Entrez un mail valide'
            } as Record<string, string>
          } as GenericForm<ResponsableSearch>
        } as GenericSearchForm<ResponsableSearch>
      ]
    });
    let kpiSearchTab = {
      label: "KPIs",
      icon: 'tablerRulerMeasure',
      forms: [
        {
          accessorKey: "startDateKpis",
          form: {
            label: 'Date de début de calcul',
            type: 'input',
            inputType: 'date',
          } as GenericForm<ResponsableSearch>
        } as GenericSearchForm<ResponsableSearch>,
        {
          accessorKey: "endDateKpis",
          form: {
            label: 'Date de fin de calcul',
            type: 'input',
            inputType: 'date',
          } as GenericForm<ResponsableSearch>
        } as GenericSearchForm<ResponsableSearch>
      ]
    };

    if (this.kpiCrms)
      for (let kpiCrm of this.kpiCrms) {
        kpiSearchTab.forms.push(
          {
            accessorKey: "kpis",
            accessorIndex: kpiCrm.code,
            form: {
              label: kpiCrm.label,
              type: 'minmax',
            } as GenericForm<ResponsableSearch>
          } as GenericSearchForm<ResponsableSearch>
        );
      }

    searchTabs.push(kpiSearchTab);
    return searchTabs;
  }

  override generateColumns() {
    let columns = [] as GenericTableColumn<ResponsableDto>[];
    columns.push({
      accessorKey: 'id', header: 'N°', enableSorting: true, cell: info => info.getValue(), meta: {
      }
    })
    columns.push({
      accessorFn: (originalRow: ResponsableDto, index: number) => { return originalRow.firstname + " " + originalRow.lastname }, header: 'Dénomination', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenResponsable
      }
    })
    columns.push({
      accessorKey: 'tiersDenomination', header: 'Tiers', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenResponsable
      }
    })
    columns.push({
      accessorKey: 'salesEmployee', header: 'Commercial', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenResponsable
      }
    })
    columns.push({
      accessorKey: 'tiersCategory', header: 'Catégorie du tiers', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenResponsable
      }
    })
    columns.push({
      accessorKey: 'responsableCategory', header: 'Catégorie du responsable', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenResponsable
      }
    })

    if (this.data && this.data[0] && this.data[0].kpiValues) {
      for (let key in this.data[0].kpiValues) {
        columns.push({
          accessorFn: (originalRow: ResponsableDto, index: number) => { return originalRow.kpiValues[key] }, header: key, enableSorting: true, cell: info => info.getValue(), meta: {
            eventOnDoubleClick: this.eventOnClickOpenResponsable
          }
        })
      }
    }
    return columns;
  }

  override parseBookmarkModel(model: ResponsableSearch) {
    if (model.kpis)
      for (let key in model.kpis) {
        if (!model.kpis[key].maxValue)
          model.kpis[key].maxValue = undefined;
        if (!this.searchModel.kpis[key].minValue)
          model.kpis[key].minValue = undefined;
      }
    return model;
  }

  override extraFormCheck() {
    let needKpiDate = false;
    if (this.searchModel.kpis && (!this.searchModel.startDateKpis || !this.searchModel.endDateKpis))
      for (let key in this.searchModel.kpis) {
        if (this.searchModel.kpis[key].maxValue != undefined && this.searchModel.kpis[key].maxValue != null)
          needKpiDate = true;
        if (this.searchModel.kpis[key].minValue != undefined && this.searchModel.kpis[key].minValue != null)
          needKpiDate = true;
      }

    if (needKpiDate) {
      this.appService2.displayToast("Les filtres de date sont obligatoires pour la recherche de KPIs", true, "Filtres manquants");
      return false;
    }

    return true;
  }

  getSearchFunction(searchModel: ResponsableSearch): Observable<ResponsableDto[]> {
    return this.responsableService.searchResponsable(searchModel);
  }

  override initSearchModel() {
    let model = {} as ResponsableSearch;
    if (this.kpiCrms) {
      model.kpis = {};
      for (let kpiCrm of this.kpiCrms) {
        model.kpis[kpiCrm.code] = { key: kpiCrm.code, minValue: undefined, maxValue: undefined };
      }
    }
    return model;
  }
}
