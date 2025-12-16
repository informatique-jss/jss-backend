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
import { AppService } from '../../../main/services/app.service';
import { RestUserPreferenceService } from '../../../main/services/rest.user.preference.service';
import { GenericFormComponent } from '../../../miscellaneous/forms/components/generic-form/generic-form.component';
import { QuotationDto } from '../../model/QuotationDto';
import { QuotationSearch } from '../../model/QuotationSearch';
import { QuotationService } from '../../services/quotation.service';

@Component({
  selector: 'quotation-list',
  templateUrl: './../../../../libs/generic-list/generic-list.component.html',
  styleUrls: ['./../../../../libs/generic-list/generic-list.component.css'],
  imports: [...SHARED_IMPORTS, TanstackTableComponent, PageTitleComponent, NgIcon, SimplebarAngularModule, NgbNavModule, GenericFormComponent],
  standalone: true
})
export class QuotationListComponent extends GenericListComponent<QuotationDto, QuotationSearch> implements OnInit {

  eventOnClickOpenAction = new Subject<Row<QuotationDto>[]>();
  eventOnClickOpenKpisAction = new Subject<Row<QuotationDto>[]>();
  eventOnClickOpenTiers = new Subject<Row<QuotationDto>>();

  override pageTitle = "Liste des devis";

  constructor(private offcanvasService2: NgbOffcanvas,
    private formBuilder2: FormBuilder,
    private appService2: AppService,
    private restUserPreferenceService2: RestUserPreferenceService,
    private router: Router,
    private kpiCrmService: KpiCrmService,
    private quotationService: QuotationService,
  ) {
    super(offcanvasService2, formBuilder2, appService2, restUserPreferenceService2);

  }

  override ngOnInit(): void {
    this.breadcrumbPaths = [
      { label: "Liste des devis", route: "/quotation" },
    ]
  }

  override generateActions(): GenericTableAction<QuotationDto>[] {
    let actions = [] as GenericTableAction<QuotationDto>[];
    actions.push({
      label: 'Voir le devis',
      eventOnClick: this.eventOnClickOpenAction,
      maxNumberOfElementsRequiredToDisplay: 1,
      minNumberOfElementsRequiredToDisplay: 1
    })

    this.eventOnClickOpenAction.subscribe((row: Row<QuotationDto>[]) => {
      this.quotationService.setSelectedQuotationUnique(row[0].original);
      this.router.navigate(['quotation/view/' + row[0].original.id]);
    });

    this.eventOnClickOpenTiers.subscribe((row: Row<QuotationDto>) => {
      this.quotationService.setSelectedQuotationUnique(row.original);
      this.router.navigate(['quotation/view/' + row.original.id]);
    });

    return actions;
  }

  override getListCode(): string {
    return 'QUOTATION_LIST';
  }

  override generateSearchTabs(): GenericSearchTab<QuotationSearch>[] {
    let searchTabs = [] as GenericSearchTab<QuotationSearch>[];
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
            validators: [Validators.required],
            errorMessages: {
              required: 'Sélectionnez au moins un commercial'
            } as Record<string, string>
          } as GenericForm<QuotationSearch>
        } as GenericSearchForm<QuotationSearch>,
        {
          accessorKey: "startDate",
          form: {
            label: 'Date de début',
            type: 'input',
            inputType: 'date',
            validators: [],
            errorMessages: {
              email: 'Entrez un mail valide'
            } as Record<string, string>
          } as GenericForm<QuotationSearch>
        } as GenericSearchForm<QuotationSearch>,
        {
          accessorKey: "endDate",
          form: {
            label: 'Date de fin',
            type: 'input',
            inputType: 'date',
            validators: [],
            errorMessages: {
              email: 'Entrez un mail valide'
            } as Record<string, string>
          } as GenericForm<QuotationSearch>
        } as GenericSearchForm<QuotationSearch>,
      ]
    });

    return searchTabs;
  }

  override generateColumns() {
    let columns = [] as GenericTableColumn<QuotationDto>[];
    columns.push({
      accessorKey: 'id', header: 'N°', enableSorting: true, cell: info => info.getValue(), meta: {
      }
    })
    columns.push({
      accessorKey: 'salesEmployee', header: 'Commercial', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenTiers
      }
    })
    columns.push({
      accessorKey: 'startDate', header: 'Date début', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenTiers
      }
    })
    columns.push({
      accessorKey: 'endDate', header: 'Date fin', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenTiers
      }
    })
    return columns;
  }

  override parseBookmarkModel(model: QuotationSearch) {
    return model;
  }

  getSearchFunction(searchModel: QuotationSearch): Observable<QuotationDto[]> {
    return this.quotationService.searchQuotation(searchModel);
  }

  override initSearchModel() {
    let model = {} as QuotationSearch;
    return model;
  }
}
