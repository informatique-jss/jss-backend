import { Component, OnInit, TemplateRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NgbNavModule, NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from '@ng-icons/core';
import { ColumnDef } from '@tanstack/angular-table';
import { Observable } from 'rxjs';
import { SimplebarAngularModule } from 'simplebar-angular';
import { PageTitleComponent } from '../../modules/main/components/page-title/page-title.component';
import { AppService } from '../../modules/main/services/app.service';
import { RestUserPreferenceService } from '../../modules/main/services/rest.user.preference.service';
import { AutocompleteComponent } from '../../modules/miscellaneous/forms/components/autocomplete/autocomplete.component';
import { GenericFormComponent } from '../../modules/miscellaneous/forms/components/generic-form/generic-form.component';
import { SHARED_IMPORTS } from '../SharedImports';
import { IId } from '../tanstack-table/Iid';
import { TanstackTableComponent } from '../tanstack-table/tanstack-table.component';
import { GenericSearchTab } from './GenericSearchTab';
import { GenericTableAction } from './GenericTableAction';
import { GenericTableColumn } from './GenericTableColumn';

/**
 * T is model displayed
 * U is search model
 */
@Component({
  standalone: true,
  templateUrl: './generic-list.component.html',
  imports: [...SHARED_IMPORTS,
    TanstackTableComponent,
    PageTitleComponent,
    NgIcon,
    SimplebarAngularModule,
    NgbNavModule,
    GenericFormComponent,
    AutocompleteComponent
  ]
})
export abstract class GenericListComponent<T extends IId, U extends Record<string, any>> implements OnInit {

  displaySelectColumn: boolean = true;
  columns: ColumnDef<T>[] | undefined;
  actions: GenericTableAction<T>[] | undefined;
  data: T[] | undefined;

  activeFilterPanelId: number = 0;
  searchForm: FormGroup = {} as FormGroup;
  searchTabs: GenericSearchTab<U>[] = [];

  abstract pageTitle: string;
  abstract pageRoute: string;

  breadcrumbPaths: { label: string; route: string; }[] = [];

  Validators = Validators;

  searchModel: U = {} as U;

  constructor(
    private offcanvasService: NgbOffcanvas,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private restUserPreferenceService: RestUserPreferenceService
  ) { }

  abstract getListCode(): string;

  abstract initSearchModel(): U;

  ngOnInit() {
    this.searchForm = this.formBuilder.group({});

    this.breadcrumbPaths = [
      { label: this.pageTitle, route: this.pageRoute },
    ]

    this.setColumns();
    this.actions = this.generateActions();

    this.searchTabs = this.generateSearchTabs();

    // Restore bookmark
    this.restUserPreferenceService.getUserPreferenceValue(this.getListCode()).subscribe(response => {
      if (response) {
        this.searchModel = JSON.parse(response) as U;
        this.searchModel = this.parseBookmarkModel(this.searchModel);
        this.search(undefined);
      } else
        this.searchModel = this.initSearchModel();
    })
  }

  setColumns() {
    this.columns = [];
    if (this.displaySelectColumn) {
      this.columns.push({
        id: 'select'
      })
    }
    this.columns.push(...this.generateColumns());
  }

  abstract generateActions(): GenericTableAction<T>[];

  abstract generateSearchTabs(): GenericSearchTab<U>[];

  abstract generateColumns(): GenericTableColumn<T>[];

  parseBookmarkModel(model: U) {
    return model;
  }

  open(content: TemplateRef<any>) {
    this.offcanvasService.open(content, { panelClass: 'asidebar border-start overflow-hidden', position: 'end' })
  }

  extraFormCheck() {
    return true;
  }

  abstract getSearchFunction(searchModel: U): Observable<T[]>;

  search(content: TemplateRef<any> | undefined) {
    this.searchForm.markAllAsTouched();

    if (!this.extraFormCheck())
      return;

    if (this.searchForm.valid) {
      this.restUserPreferenceService.setUserPreference(JSON.stringify(this.searchModel), this.getListCode()).subscribe();
      if (content)
        this.offcanvasService.dismiss(content);
      this.appService.showLoadingSpinner();
      this.getSearchFunction(this.searchModel).subscribe(response => {
        this.appService.hideLoadingSpinner();
        this.data = response;
        this.setColumns();
      })
    }
  }

  clearFilters() {
    this.searchModel = this.initSearchModel();
    this.restUserPreferenceService.setUserPreference(JSON.stringify(this.searchModel), this.getListCode()).subscribe();
  }
}
