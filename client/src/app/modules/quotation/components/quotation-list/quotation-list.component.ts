import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { Employee } from '../../../profile/model/Employee';
import { IQuotation } from '../../model/IQuotation';
import { QuotationSearch } from '../../model/QuotationSearch';
import { QuotationSearchResult } from '../../model/QuotationSearchResult';
import { QuotationSearchResultService } from '../../services/quotation.search.result.service';
import { QuotationComponent } from '../quotation/quotation.component';

@Component({
  selector: 'quotation-list',
  templateUrl: './quotation-list.component.html',
  styleUrls: ['./quotation-list.component.css']
})
export class QuotationListComponent implements OnInit {
  @Input() quotationSearch: QuotationSearch = {} as QuotationSearch;
  @Input() isForDashboard: boolean = false;
  @Input() isForTiersIntegration: boolean = false;
  quotations: QuotationSearchResult[] | undefined;
  availableColumns: SortTableColumn[] = [];
  columnToDisplayOnDashboard: string[] = ["customerOrderName", "quotationStatus", "affaireLabel", "createdDate"];
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];
  bookmark: QuotationSearch | undefined;

  allEmployees: Employee[] | undefined;

  constructor(
    private appService: AppService,
    private quotationSearchResultService: QuotationSearchResultService,
    private employeeService: EmployeeService,
    private userPreferenceService: UserPreferenceService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() {
    this.employeeService.getEmployees().subscribe(response => {
      this.bookmark = this.userPreferenceService.getUserSearchBookmark("quotations") as QuotationSearch;

      if (this.bookmark && !this.isForDashboard && !this.isForTiersIntegration) {
        this.quotationSearch = {} as QuotationSearch;
        this.quotationSearch.salesEmployee = this.bookmark.salesEmployee;
        this.quotationSearch.quotationStatus = this.bookmark.quotationStatus;
      }

      this.allEmployees = response;
      if (!this.isForDashboard && !this.isForTiersIntegration)
        this.appService.changeHeaderTitle("Devis");


      this.availableColumns = [];
      this.availableColumns.push({ id: "id", fieldName: "quotationId", label: "N° de la devis" } as SortTableColumn);
      this.availableColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date de création", valueFonction: formatDateForSortTable } as SortTableColumn);
      this.availableColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire(s)", isShrinkColumn: true } as SortTableColumn);
      this.availableColumns.push({ id: "quotationStatus", fieldName: "quotationStatus", label: "Statut" } as SortTableColumn);
      this.availableColumns.push({ id: "quotationDescription", fieldName: "quotationDescription", label: "Description", isShrinkColumn: true } as SortTableColumn);
      this.availableColumns.push({ id: "tiersLabel", fieldName: "tiersLabel", label: "Tiers", actionLinkFunction: this.getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la fiche du tiers" } as SortTableColumn);
      this.availableColumns.push({ id: "customerOrderName", fieldName: "customerOrderLabel", label: "Donneur d'ordre", actionLinkFunction: this.getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la fiche du donneur d'ordre" } as SortTableColumn);
      this.availableColumns.push({
        id: "salesEmployee", fieldName: "salesEmployeeId", label: "Commercial", displayAsEmployee: true, valueFonction: (element: any) => {
          if (element && this.allEmployees) {
            for (let employee of this.allEmployees)
              if (employee.id == element.salesEmployeeId)
                return employee;
          }
          return undefined;
        }
      } as SortTableColumn);

      this.setColumns();

      this.tableAction.push({ actionIcon: "request_quote", actionName: "Voir le devis", actionLinkFunction: this.getActionLink, display: true, } as SortTableAction);

      if ((this.isForDashboard || this.isForTiersIntegration) && !this.quotations && this.quotationSearch)
        this.searchOrders();

    });
  }

  quotationSearchForm = this.formBuilder.group({
  });

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

  getSalesEmployee(element: any): Employee | undefined {
    if (element && this.allEmployees) {
      for (let employee of this.allEmployees)
        if (employee.id == element.salesEmployeeId)
          return employee;
    }
    return undefined;
  }

  getActionLink(action: SortTableAction, element: any) {
    if (element)
      return ['/quotation', element.quotationId];
    return undefined;
  }


  getColumnLink(column: SortTableColumn, element: any) {
    if (element && column.id == "tiersLabel") {
      return ['/tiers', element.tiersId];
    }
    if (element && column.id == "customerOrderName") {
      if (element.responsableId)
        return ['/tiers/responsable', element.responsableId];
      if (element.tiersId)
        return ['/tiers', element.tiersId];
      if (element.confrereId)
        return ['/confrere', element.confrereId];
    }
    return ['/tiers'];
  }

  getTotalPrice(element: any) {
    QuotationComponent.computePriceTotal(element as IQuotation);
  }

  getCustomerOrderName(element: any) {
    if (element) {
      if (element.confrere)
        return element.confrere.denomination
      if (element.responsable)
        return element.responsable.firstname + " " + element.responsable.lastname;
      if (element.tiers)
        return element.tiers.firstname + " " + element.tiers.lastname;
    }
  }


  searchOrders() {
    if (this.quotationSearchForm.valid) {
      if (!this.isForDashboard)
        this.userPreferenceService.setUserSearchBookmark(this.quotationSearch, "quotations");
      if (this.quotationSearch.startDate)
        this.quotationSearch.startDate = new Date(toIsoString(this.quotationSearch.startDate));
      if (this.quotationSearch.endDate)
        this.quotationSearch.endDate = new Date(toIsoString(this.quotationSearch.endDate));
      this.quotationSearchResultService.getQuotations(this.quotationSearch).subscribe(response => {
        this.quotations = response;
      })
    }
  }
}
