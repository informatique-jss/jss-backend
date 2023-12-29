import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { formatDateTimeForSortTable } from '../../../../libs/FormatHelper';
import { AffaireSearch } from '../../model/AffaireSearch';
import { AssoAffaireOrderSearchResult } from '../../model/AssoAffaireOrderSearchResult';
import { AssoAffaireOrderSearchResultService } from '../../services/asso.affaire.order.search.result.service';

@Component({
  selector: 'provision-list',
  templateUrl: './provision-list.component.html',
  styleUrls: ['./provision-list.component.css']
})
export class ProvisionListComponent implements OnInit {
  @Input() affaireSearch: AffaireSearch | undefined;
  affaires: AssoAffaireOrderSearchResult[] | undefined;
  availableColumns: SortTableColumn<AssoAffaireOrderSearchResult>[] = [];
  columnToDisplayOnDashboard: string[] = ["customerOrderId", "provisionCreatedDatetime", "provisionStatusDatetime", "affaireLabel", "provisionType", "status"];
  displayedColumns: SortTableColumn<AssoAffaireOrderSearchResult>[] = [];
  tableAction: SortTableAction<AssoAffaireOrderSearchResult>[] = [];

  @Input() isForDashboard: boolean = false;
  @Input() isForTiersIntegration: boolean = false;

  currentEmployee: Employee | undefined;
  allEmployees: Employee[] = [] as Array<Employee>;
  bookmark: AffaireSearch | undefined;

  constructor(
    private appService: AppService,
    private assoAffaireOrderSearchResultService: AssoAffaireOrderSearchResultService,
    private employeeService: EmployeeService,
    private formBuilder: FormBuilder,
    private userPreferenceService: UserPreferenceService,
    private activatedRoute: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.availableColumns = [];


    this.employeeService.getEmployees().subscribe(employees => {
      this.allEmployees = employees;
      let employeeId = this.activatedRoute.snapshot.params.employeeId;

      this.bookmark = this.userPreferenceService.getUserSearchBookmark("prestations") as AffaireSearch;
      if (!this.isForDashboard && !this.isForTiersIntegration) {
        this.appService.changeHeaderTitle("Prestations");
        if (this.bookmark && !this.isForDashboard && !this.isForTiersIntegration) {
          this.affaireSearch = {} as AffaireSearch;
          this.affaireSearch.assignedTo = this.bookmark.assignedTo;
          this.affaireSearch.label = this.bookmark.label;
          this.affaireSearch.responsible = this.bookmark.responsible;
          this.affaireSearch.status = this.bookmark.status;
        }

        if (employeeId) {
          this.affaireSearch = {} as AffaireSearch;
          if (this.allEmployees)
            for (let employee of this.allEmployees)
              if (employee.id == employeeId)
                this.affaireSearch.assignedTo = employee;
        }
      }

      this.availableColumns.push({
        id: "customerOrderId", fieldName: "customerOrderId", label: "N° de commande", actionLinkFunction: (column: SortTableColumn<AssoAffaireOrderSearchResult>, element: AssoAffaireOrderSearchResult) => {
          return ['/order/', element.customerOrderId];
        }, actionIcon: "visibility", actionTooltip: "Voir la commande associée"
      } as SortTableColumn<AssoAffaireOrderSearchResult>);
      this.availableColumns.push({ id: "provisionCreatedDatetime", fieldName: "provisionCreatedDatetime", label: "Date de création", valueFonction: formatDateTimeForSortTable, colorWarnFunction: (element: AssoAffaireOrderSearchResult) => { return element.isEmergency } } as SortTableColumn<AssoAffaireOrderSearchResult>);
      this.availableColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire", colorWarnFunction: (element: AssoAffaireOrderSearchResult) => { return element.isEmergency } } as SortTableColumn<AssoAffaireOrderSearchResult>);
      this.availableColumns.push({ id: "affaireAddress", fieldName: "affaireAddress", label: "Adresse de l'affaire", colorWarnFunction: (element: AssoAffaireOrderSearchResult) => { return element.isEmergency } } as SortTableColumn<AssoAffaireOrderSearchResult>);
      this.availableColumns.push({ id: "tiers", fieldName: "tiersLabel", label: "Tiers", colorWarnFunction: (element: AssoAffaireOrderSearchResult) => { return element.isEmergency } } as SortTableColumn<AssoAffaireOrderSearchResult>);
      this.availableColumns.push({ id: "responsable", fieldName: "responsableLabel", label: "Responsable", colorWarnFunction: (element: AssoAffaireOrderSearchResult) => { return element.isEmergency } } as SortTableColumn<AssoAffaireOrderSearchResult>);
      this.availableColumns.push({ id: "confrere", fieldName: "confrereLabel", label: "Confrère", colorWarnFunction: (element: AssoAffaireOrderSearchResult) => { return element.isEmergency } } as SortTableColumn<AssoAffaireOrderSearchResult>);
      this.availableColumns.push({ id: "responsible", fieldName: "responsibleId", label: "Responsable de l'affaire", displayAsEmployee: true } as SortTableColumn<AssoAffaireOrderSearchResult>);
      this.availableColumns.push({ id: "assignedTo", fieldName: "assignedToId", label: "Assignée à", displayAsEmployee: true } as SortTableColumn<AssoAffaireOrderSearchResult>);
      this.availableColumns.push({ id: "provisionType", fieldName: "provisionTypeLabel", label: "Prestation", colorWarnFunction: (element: AssoAffaireOrderSearchResult) => { return element.isEmergency } } as SortTableColumn<AssoAffaireOrderSearchResult>);
      this.availableColumns.push({ id: "status", fieldName: "statusLabel", label: "Statut", colorWarnFunction: (element: AssoAffaireOrderSearchResult) => { return element.isEmergency } } as SortTableColumn<AssoAffaireOrderSearchResult>);
      this.availableColumns.push({ id: "provisionStatusDatetime", fieldName: "provisionStatusDatetime", label: "Date de mise à jour", valueFonction: formatDateTimeForSortTable, colorWarnFunction: (element: AssoAffaireOrderSearchResult) => { return element.isEmergency } } as SortTableColumn<AssoAffaireOrderSearchResult>);
      this.availableColumns.push({ id: "competentAuthorityLabel", fieldName: "competentAuthorityLabel", label: "Autorité compétente" } as SortTableColumn<AssoAffaireOrderSearchResult>);
      this.availableColumns.push({ id: "waitedCompetentAuthorityLabel", fieldName: "waitedCompetentAuthorityLabel", label: "Autorité compétente en attente" } as SortTableColumn<AssoAffaireOrderSearchResult>);
      this.getCurrentEmployee();

      this.setColumns();

      this.tableAction.push({
        actionIcon: "work", actionName: "Voir la prestation", actionLinkFunction: (action: SortTableAction<AssoAffaireOrderSearchResult>, element: AssoAffaireOrderSearchResult) => {
          if (element)
            return ['/provision', element.assoId, element.provisionId];
          return undefined;
        }, display: true,
      } as SortTableAction<AssoAffaireOrderSearchResult>);

      if (this.isForDashboard && !this.affaires && this.affaireSearch || employeeId || this.isForTiersIntegration)
        this.searchAffaires();
    })
  }

  setColumns() {
    this.displayedColumns = [];
    if (this.availableColumns && this.columnToDisplayOnDashboard && this.isForDashboard) {
      for (let availableColumn of this.availableColumns)
        for (let columnToDisplay of this.columnToDisplayOnDashboard)
          if (availableColumn.id == columnToDisplay)
            this.displayedColumns.push(availableColumn);
    }
    else
      this.displayedColumns.push(...this.availableColumns);
  }

  getCurrentEmployee() {
    this.employeeService.getCurrentEmployee().subscribe(response => this.currentEmployee = response);
  }

  affaireSearchForm = this.formBuilder.group({
  });

  searchAffaires() {
    if (this.affaireSearch && this.affaireSearchForm.valid && (
      this.affaireSearch.assignedTo
      || this.affaireSearch.label
      || this.affaireSearch.responsible
      || this.affaireSearch.status
      || this.affaireSearch.customerOrders
      || this.affaireSearch.affaire
      || this.affaireSearch.waitedCompetentAuthority
    )) {
      if (!this.isForDashboard)
        this.userPreferenceService.setUserSearchBookmark(this.affaireSearch, "prestations");
      this.assoAffaireOrderSearchResultService.getAssoAffaireOrders(this.affaireSearch).subscribe(response => {
        this.affaires = response;
      })
    } else {
      this.appService.displaySnackBar("Veuillez remplir au moins un filtre", true, 20);
    }
  }
}
