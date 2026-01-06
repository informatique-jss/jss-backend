import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbNavModule, NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from '@ng-icons/core';
import { Row } from '@tanstack/angular-table';
import { Observable, Subject } from 'rxjs';
import { SimplebarAngularModule } from 'simplebar-angular';
import { formatDateFrance } from '../../../../../../../client/src/app/libs/FormatHelper';
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
import { AppService } from '../../../main/services/app.service';
import { RestUserPreferenceService } from '../../../main/services/rest.user.preference.service';
import { AutocompleteComponent } from '../../../miscellaneous/forms/components/autocomplete/autocomplete.component';
import { GenericFormComponent } from '../../../miscellaneous/forms/components/generic-form/generic-form.component';
import { ProvisionDto } from '../../model/ProvisionDto';
import { ProvisionSearch } from '../../model/ProvisionSearch';
import { ProvisionService } from '../../services/provision.service';

@Component({
  selector: 'provision-list',
  templateUrl: './../../../../libs/generic-list/generic-list.component.html',
  styleUrls: ['./../../../../libs/generic-list/generic-list.component.css'],
  imports: [...SHARED_IMPORTS,
    TanstackTableComponent,
    PageTitleComponent,
    NgIcon,
    SimplebarAngularModule,
    NgbNavModule,
    GenericFormComponent,
    AutocompleteComponent],
  standalone: true
})
export class ProvisionListComponent extends GenericListComponent<ProvisionDto, ProvisionSearch> implements OnInit {

  eventOnClickOpenAction = new Subject<Row<ProvisionDto>[]>();
  eventOnClickOpenQuotation = new Subject<Row<ProvisionDto>>();

  override pageTitle = "Liste des prestations";

  constructor(private offcanvasService2: NgbOffcanvas,
    private formBuilder2: FormBuilder,
    private appService2: AppService,
    private restUserPreferenceService2: RestUserPreferenceService,
    private router: Router,
    private kpiCrmService: KpiCrmService,
    private provisionService: ProvisionService,
  ) {
    super(offcanvasService2, formBuilder2, appService2, restUserPreferenceService2);

  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.breadcrumbPaths = [
      { label: this.pageTitle, route: "/provision" },
    ]
  }

  override generateActions(): GenericTableAction<ProvisionDto>[] {
    let actions = [] as GenericTableAction<ProvisionDto>[];
    actions.push({
      label: 'Voir la provision',
      eventOnClick: this.eventOnClickOpenAction,
      maxNumberOfElementsRequiredToDisplay: 1,
      minNumberOfElementsRequiredToDisplay: 1
    })

    this.eventOnClickOpenAction.subscribe((row: Row<ProvisionDto>[]) => {
      this.provisionService.setSelectedProvisionUnique(row[0].original);
      this.router.navigate(['provision/view/' + row[0].original.id]);
    });

    this.eventOnClickOpenQuotation.subscribe((row: Row<ProvisionDto>) => {
      this.provisionService.setSelectedProvisionUnique(row.original);
      this.router.navigate(['provision/view/' + row.original.id]);
    });

    return actions;
  }

  override getListCode(): string {
    return 'PROVISION_LIST';
  }

  override generateSearchTabs(): GenericSearchTab<ProvisionSearch>[] {
    let searchTabs = [] as GenericSearchTab<ProvisionSearch>[];
    searchTabs.push({
      label: "Généraux",
      icon: 'tablerFilter',
      forms: [
        {
          accessorKey: "salesEmployee",
          form: {
            label: 'Commercial',
            type: 'select',
            selectType: 'commercial',
            validators: [],
          } as GenericForm<ProvisionSearch>
        } as GenericSearchForm<ProvisionSearch>,
        {
          accessorKey: "formalisteEmployee",
          form: {
            label: 'Formaliste assigné',
            type: 'select',
            selectType: 'formaliste',
            validators: [],
            errorMessages: {
            } as Record<string, string>
          } as GenericForm<ProvisionSearch>
        } as GenericSearchForm<ProvisionSearch>,
        {
          accessorKey: "startDate",
          form: {
            label: 'Date de début',
            type: 'input',
            inputType: 'date',
            validators: [],
          } as GenericForm<ProvisionSearch>
        } as GenericSearchForm<ProvisionSearch>,
        {
          accessorKey: "endDate",
          form: {
            label: 'Date de fin',
            type: 'input',
            inputType: 'date',
            validators: [],
          } as GenericForm<ProvisionSearch>
        } as GenericSearchForm<ProvisionSearch>,
        {
          accessorKey: "provisionStatus",
          form: {
            label: 'Status de la prestation',
            type: 'select',
            selectType: 'provisionStatus',
            validators: [],
            errorMessages: {
            } as Record<string, string>
          } as GenericForm<ProvisionSearch>
        } as GenericSearchForm<ProvisionSearch>,
      ]
    });

    return searchTabs;
  }

  override generateColumns() {
    let columns = [] as GenericTableColumn<ProvisionDto>[];
    columns.push({
      accessorKey: 'id', header: 'N°', enableSorting: true, cell: info => info.getValue(), meta: {
      }
    })
    columns.push({
      accessorFn: (originalRow: ProvisionDto, index: number) => { return formatDateFrance(originalRow.productionDate) }, header: 'Date de production', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorKey: 'customerOrderId', header: 'N° de commande', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorKey: 'service', header: 'Service(s)', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorKey: 'responsable', header: 'Responsable', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorKey: 'tiers', header: 'Tiers', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorKey: 'affaire', header: 'Affaire', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorKey: 'confrere', header: 'Confrère', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorKey: 'formalisteEmployee', header: 'Formaliste', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorKey: 'provisionLabel', header: 'Nom de la prestation', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorKey: 'competentAuthority', header: 'Autorité comptétente', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    return columns;
  }

  override parseBookmarkModel(model: ProvisionSearch) {
    return model;
  }

  getSearchFunction(searchModel: ProvisionSearch): Observable<ProvisionDto[]> {
    return this.provisionService.searchProvisions(searchModel);
  }

  override initSearchModel() {
    let model = {} as ProvisionSearch;
    return model;
  }
}
