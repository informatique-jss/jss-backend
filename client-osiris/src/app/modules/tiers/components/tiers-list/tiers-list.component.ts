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
import { KpiCrmService } from '../../../crm/services/kpi.crm.service';
import { PageTitleComponent } from '../../../main/components/page-title/page-title.component';
import { KpiCrm } from '../../../main/model/KpiCrm';
import { AppService } from '../../../main/services/app.service';
import { RestUserPreferenceService } from '../../../main/services/rest.user.preference.service';
import { GenericFormComponent } from '../../../miscellaneous/forms/components/generic-form/generic-form.component';
import { TiersDto } from '../../model/TiersDto';
import { TiersSearch } from '../../model/TiersSearch';
import { ResponsableService } from '../../services/responsable.service';
import { TiersService } from '../../services/tiers.service';

@Component({
  selector: 'tiers-list',
  templateUrl: './../../../../libs/generic-list/generic-list.component.html',
  styleUrls: ['./../../../../libs/generic-list/generic-list.component.css'],
  imports: [...SHARED_IMPORTS, TanstackTableComponent, PageTitleComponent, NgIcon, SimplebarAngularModule, NgbNavModule, GenericFormComponent],
  standalone: true
})
export class TiersListComponent extends GenericListComponent<TiersDto, TiersSearch> implements OnInit {

  eventOnClickOpenAction = new Subject<Row<TiersDto>[]>();
  eventOnClickOpenKpisAction = new Subject<Row<TiersDto>[]>();
  eventOnClickOpenTiers = new Subject<Row<TiersDto>>();

  kpiCrms: KpiCrm[] | undefined;

  constructor(private offcanvasService2: NgbOffcanvas,
    private formBuilder2: FormBuilder,
    private appService2: AppService,
    private restUserPreferenceService2: RestUserPreferenceService,
    private router: Router,
    private tiersService: TiersService,
    private kpiCrmService: KpiCrmService,
    private responsableService: ResponsableService
  ) {
    super(offcanvasService2, formBuilder2, appService2, restUserPreferenceService2);

  }

  override ngOnInit(): void {
    this.kpiCrmService.getKpiCrm().subscribe(reponse => {
      this.kpiCrms = reponse;
      super.ngOnInit();
    })
  }

  override generateActions(): GenericTableAction<TiersDto>[] {
    let actions = [] as GenericTableAction<TiersDto>[];
    actions.push({
      label: 'Voir le tiers',
      eventOnClick: this.eventOnClickOpenAction,
      maxNumberOfElementsRequiredToDisplay: 1,
      minNumberOfElementsRequiredToDisplay: 1
    })
    actions.push({
      label: 'Voir les KPIs pour ces tiers',
      eventOnClick: this.eventOnClickOpenKpisAction,
      minNumberOfElementsRequiredToDisplay: 1
    })

    this.eventOnClickOpenAction.subscribe((row: Row<TiersDto>[]) => {
      this.tiersService.setSelectedTiersUnique(row[0].original);
      this.router.navigate(['tiers/view/' + row[0].original.id]);
    });

    this.eventOnClickOpenKpisAction.subscribe((row: Row<TiersDto>[]) => {
      this.tiersService.setCurrentSelectedTiers(row.map(r => r.original));
      this.tiersService.setSelectedKpiStartDate(this.searchModel.startDateKpis);
      this.tiersService.setSelectedKpiEndDate(this.searchModel.endDateKpis);
      this.responsableService.clearKpiSelection()
      this.router.navigate(['tiers/crm/kpi/selection']);
    });

    this.eventOnClickOpenTiers.subscribe((row: Row<TiersDto>) => {
      this.tiersService.setSelectedTiersUnique(row.original);
      this.router.navigate(['tiers/view/' + row.original.id]);
    });

    return actions;
  }

  override   getListCode(): string {
    return 'TIERS_LIST';
  }

  override generateSearchTabs(): GenericSearchTab<TiersSearch>[] {
    let searchTabs = [] as GenericSearchTab<TiersSearch>[];
    searchTabs.push({
      label: "Généraux",
      icon: 'tablerFilter',
      forms: [
        {
          accessorKey: "label",
          form: {
            label: 'Dénomination du tiers',
            type: 'input',
            inputType: 'text',
            validators: [Validators.minLength(3)],
            errorMessages: {
              minlength: 'Entrez au moins 3 caractères'
            } as Record<string, string>
          } as GenericForm<TiersSearch>
        } as GenericSearchForm<TiersSearch>,
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
          } as GenericForm<TiersSearch>
        } as GenericSearchForm<TiersSearch>,
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
          } as GenericForm<TiersSearch>
        } as GenericSearchForm<TiersSearch>,
        {
          accessorKey: "isNewTiers",
          form: {
            label: 'Nouveau tiers ?',
            type: 'switch',
          } as GenericForm<TiersSearch>
        } as GenericSearchForm<TiersSearch>
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
          } as GenericForm<TiersSearch>
        } as GenericSearchForm<TiersSearch>,
        {
          accessorKey: "endDateKpis",
          form: {
            label: 'Date de fin de calcul',
            type: 'input',
            inputType: 'date',
          } as GenericForm<TiersSearch>
        } as GenericSearchForm<TiersSearch>
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
            } as GenericForm<TiersSearch>
          } as GenericSearchForm<TiersSearch>
        );
      }

    searchTabs.push(kpiSearchTab);
    return searchTabs;
  }

  override generateColumns() {
    let columns = [] as GenericTableColumn<TiersDto>[];
    columns.push({
      accessorKey: 'id', header: 'N°', enableSorting: true, cell: info => info.getValue(), meta: {
      }
    })
    columns.push({
      accessorKey: 'tiersCategory', header: 'Catégorie', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenTiers
      }
    })
    columns.push({
      accessorKey: 'denomination', header: 'Dénomination', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenTiers
      }
    })
    columns.push({
      accessorKey: 'salesEmployee', header: 'Commercial', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenTiers
      }
    })
    columns.push({
      accessorKey: 'formalisteEmployee', header: 'Formaliste', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenTiers
      }
    })
    columns.push({
      accessorFn: (originalRow: TiersDto, index: number) => { return originalRow.address + " " + originalRow.city.label }, header: 'Adresse', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenTiers
      }
    })
    columns.push({
      accessorFn: (originalRow: TiersDto, index: number) => { return originalRow.isNewTiers ? "Oui" : "Non" }, header: 'Nouveau tiers ?', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenTiers
      }
    })

    if (this.data && this.data[0] && this.data[0].kpiValues) {
      for (let key in this.data[0].kpiValues) {
        columns.push({
          accessorFn: (originalRow: TiersDto, index: number) => { return originalRow.kpiValues[key] }, header: key, enableSorting: true, cell: info => info.getValue(), meta: {
            eventOnDoubleClick: this.eventOnClickOpenTiers
          }
        })
      }
    }
    return columns;
  }

  override parseBookmarkModel(model: TiersSearch) {
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

  getSearchFunction(searchModel: TiersSearch): Observable<TiersDto[]> {
    return this.tiersService.searchTiers(searchModel);
  }

  override initSearchModel() {
    let model = {} as TiersSearch;
    if (this.kpiCrms) {
      model.kpis = {};
      for (let kpiCrm of this.kpiCrms) {
        model.kpis[kpiCrm.code] = { key: kpiCrm.code, minValue: undefined, maxValue: undefined };
      }
    }
    return model;
  }
}
