import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateTimeForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { Employee } from '../../../profile/model/Employee';
import { EmployeeService } from '../../../profile/services/employee.service';
import { CustomerOrder } from '../../model/CustomerOrder';
import { OrderingSearch } from '../../model/OrderingSearch';
import { OrderingSearchResult } from '../../model/OrderingSearchResult';
import { OrderingSearchResultService } from '../../services/ordering.search.result.service';

@Component({
  selector: 'ordering-list',
  templateUrl: './ordering-list.component.html',
  styleUrls: ['./ordering-list.component.css']
})
export class OrderingListComponent implements OnInit {
  @Input() orderingSearch: OrderingSearch = {} as OrderingSearch;
  @Input() isForDashboard: boolean = false;
  @Input() isForTiersIntegration: boolean = false;
  orders: OrderingSearchResult[] | undefined;
  availableColumns: SortTableColumn[] = [];
  columnToDisplayOnDashboard: string[] = ["customerOrderLabel", "customerOrderStatus", "customerOrderDescription"];
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];
  bookmark: OrderingSearch | undefined;

  @Output() actionBypass: EventEmitter<CustomerOrder> = new EventEmitter<CustomerOrder>();
  @Input() overrideIconAction: string = "";
  @Input() overrideTooltipAction: string = "";
  @Input() defaultStatusFilter: string[] | undefined;

  allEmployees: Employee[] | undefined;

  constructor(
    private appService: AppService,
    private orderingSearchResultService: OrderingSearchResultService,
    private employeeService: EmployeeService,
    private userPreferenceService: UserPreferenceService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() {
    this.putDefaultPeriod();
    this.employeeService.getEmployees().subscribe(response => {

      this.bookmark = this.userPreferenceService.getUserSearchBookmark("customerOrders") as OrderingSearch;

      if (this.bookmark && !this.isForDashboard) {
        this.orderingSearch = {} as OrderingSearch;
        this.orderingSearch.endDate = new Date(this.bookmark.endDate as any);
        this.orderingSearch.startDate = new Date(this.bookmark.startDate as any);
        this.orderingSearch.salesEmployee = this.bookmark.salesEmployee;
        this.orderingSearch.customerOrderStatus = this.bookmark.customerOrderStatus;
      }

      this.allEmployees = response;

      if (!this.isForDashboard && !this.isForTiersIntegration)
        this.appService.changeHeaderTitle("Commande")
      this.availableColumns = [];
      this.availableColumns.push({ id: "id", fieldName: "customerOrderId", label: "N° de la commande" } as SortTableColumn);
      this.availableColumns.push({ id: "customerOrderLabel", fieldName: "customerOrderLabel", label: "Donneur d'ordre", actionLinkFunction: this.getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la fiche du donneur d'ordre" } as SortTableColumn);
      this.availableColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire(s)", isShrinkColumn: true } as SortTableColumn);
      this.availableColumns.push({ id: "customerOrderStatus", fieldName: "customerOrderStatus", label: "Statut" } as SortTableColumn);
      this.availableColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date de création", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
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
      this.availableColumns.push({ id: "customerOrderDescription", fieldName: "customerOrderDescription", label: "Description", isShrinkColumn: true } as SortTableColumn);

      this.setColumns();

      if (this.overrideIconAction == "") {
        this.tableAction.push({
          actionIcon: "shopping_cart", actionName: "Voir la commande", actionLinkFunction: (action: SortTableAction, element: any) => {
            if (element)
              return ['/order', element.customerOrderId];
            return undefined;
          }, display: true,
        } as SortTableAction);
      } else {
        this.tableAction.push({
          actionIcon: this.overrideIconAction, actionName: this.overrideTooltipAction, actionClick: (action: SortTableAction, element: any) => {
            this.actionBypass.emit(element);
          }, display: true,
        } as SortTableAction);
      };
      if ((this.isForDashboard || this.isForTiersIntegration) && !this.orders && this.orderingSearch)
        this.searchOrders();
    });
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

  orderingSearchForm = this.formBuilder.group({
  });

  getSalesEmployee(element: any): Employee | undefined {
    if (element && this.allEmployees) {
      for (let employee of this.allEmployees)
        if (employee.id == element.salesEmployeeId)
          return employee;
    }
    return undefined;
  }

  getColumnLink(column: SortTableColumn, element: any) {
    if (element && column.id == "customerOrderLabel") {
      if (element.responsableId)
        return ['/tiers/responsable/', element.responsableId];
      if (element.tiersId)
        return ['/tiers/', element.tiersId];
      if (element.confrereId)
        return ['/referential/confrere/', element.confrereId];
    }
    return ['/tiers'];
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


  putDefaultPeriod() {
    if (!this.orderingSearch.startDate && !this.orderingSearch.endDate) {
      this.orderingSearch.startDate = new Date();
      this.orderingSearch.endDate = new Date();
      this.orderingSearch.startDate.setDate(this.orderingSearch.endDate.getDate() - 30);
    }
  }

  searchOrders() {
    if (this.orderingSearchForm.valid && this.orderingSearch.startDate && this.orderingSearch.endDate) {
      if (!this.isForDashboard)
        this.userPreferenceService.setUserSearchBookmark(this.orderingSearch, "customerOrders");
      this.orderingSearch.startDate = new Date(toIsoString(this.orderingSearch.startDate));
      this.orderingSearch.endDate = new Date(toIsoString(this.orderingSearch.endDate));
      this.orderingSearchResultService.getOrders(this.orderingSearch).subscribe(response => {
        this.orders = response;
      })
    }
  }
}
