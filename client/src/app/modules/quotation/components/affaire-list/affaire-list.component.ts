import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { AffaireSearch } from '../../model/AffaireSearch';
import { AssoAffaireOrderSearchResult } from '../../model/AssoAffaireOrderSearchResult';
import { AssoAffaireOrderSearchResultService } from '../../services/asso.affaire.order.search.result.service';

@Component({
  selector: 'affaire-list',
  templateUrl: './affaire-list.component.html',
  styleUrls: ['./affaire-list.component.css']
})
export class AffaireListComponent implements OnInit {
  @Input() affaireSearch: AffaireSearch | undefined;
  affaires: AssoAffaireOrderSearchResult[] | undefined;
  availableColumns: SortTableColumn[] = [];
  columnToDisplayOnDashboard: string[] = ["affaireLabel", "provisionType", "status"];
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];

  @Input() isForDashboard: boolean = false;

  currentEmployee: Employee | undefined;
  allEmployees: Employee[] = [] as Array<Employee>;
  bookmark: AffaireSearch | undefined;

  constructor(
    private appService: AppService,
    private assoAffaireOrderSearchResultService: AssoAffaireOrderSearchResultService,
    private employeeService: EmployeeService,
    private formBuilder: FormBuilder,
    private userPreferenceService: UserPreferenceService,
  ) { }

  ngOnInit() {
    this.availableColumns = [];

    this.employeeService.getEmployees().subscribe(employees => {
      this.allEmployees = employees;

      this.bookmark = this.userPreferenceService.getUserSearchBookmark("affaires") as AffaireSearch;
      if (!this.isForDashboard) {
        this.appService.changeHeaderTitle("Affaires / Prestations");
        if (this.bookmark && !this.isForDashboard) {
          this.affaireSearch = {} as AffaireSearch;
          this.affaireSearch.assignedTo = this.bookmark.assignedTo;
          this.affaireSearch.label = this.bookmark.label;
          this.affaireSearch.responsible = this.bookmark.responsible;
          this.affaireSearch.status = this.bookmark.status;
        }
      }

      this.availableColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire" } as SortTableColumn);
      this.availableColumns.push({ id: "affaireAddress", fieldName: "affaireAddress", label: "Adresse de l'affaire" } as SortTableColumn);
      this.availableColumns.push({ id: "tiers", fieldName: "tiersLabel", label: "Tiers" } as SortTableColumn);
      this.availableColumns.push({ id: "responsable", fieldName: "responsableLabel", label: "Responsable" } as SortTableColumn);
      this.availableColumns.push({ id: "confrere", fieldName: "confrereLabel", label: "Confrère" } as SortTableColumn);
      this.availableColumns.push({
        id: "responsible", fieldName: "responsibleLabel", label: "Responsable de l'affaire", valueFonction: (element: any) => {
          if (this.allEmployees)
            for (let employee of this.allEmployees)
              if (employee.id == element.responsibleId)
                return employee;
          return null;
        }, displayAsEmployee: true
      } as SortTableColumn);
      this.availableColumns.push({
        id: "assignedTo", fieldName: "assignedToLabel", label: "Assignée à", valueFonction: (element: any) => {
          if (this.allEmployees)
            for (let employee of this.allEmployees)
              if (employee.id == element.assignedToId)
                return employee;
          return null;
        }, displayAsEmployee: true
      } as SortTableColumn);
      this.availableColumns.push({ id: "provisionType", fieldName: "provisionTypeLabel", label: "Prestation" } as SortTableColumn);
      this.availableColumns.push({ id: "status", fieldName: "statusLabel", label: "Statut" } as SortTableColumn);
      this.getCurrentEmployee();

      this.setColumns();

      this.tableAction.push({
        actionIcon: "work", actionName: "Voir l'affaire / prestation", actionLinkFunction: (action: SortTableAction, element: any) => {
          if (element)
            return ['/affaire', element.assoId, element.provisionId];
          return undefined;
        }, display: true,
      } as SortTableAction);

      if (this.isForDashboard && !this.affaires && this.affaireSearch)
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
    )) {
      if (!this.isForDashboard)
        this.userPreferenceService.setUserSearchBookmark(this.affaireSearch, "affaires");
      this.assoAffaireOrderSearchResultService.getAssoAffaireOrders(this.affaireSearch).subscribe(response => {
        this.affaires = response;
      })
    } else {
      this.appService.displaySnackBar("Veuillez remplir au moins un filtre", true, 20);
    }
  }
}
