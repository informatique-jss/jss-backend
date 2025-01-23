import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { CUSTOMER_ORDER_STATUS_BEING_PROCESSED, CUSTOMER_ORDER_STATUS_OPEN, CUSTOMER_ORDER_STATUS_TO_BILLED, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT } from 'src/app/libs/Constants';
import { formatDateForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { AppService } from 'src/app/services/app.service';
import { formatDateTimeForSortTable, formatEurosForSortTable } from '../../../../libs/FormatHelper';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { Employee } from '../../../profile/model/Employee';
import { EmployeeService } from '../../../profile/services/employee.service';
import { CustomerOrderStatus } from '../../model/CustomerOrderStatus';
import { OrderingSearch } from '../../model/OrderingSearch';
import { OrderingSearchResult } from '../../model/OrderingSearchResult';
import { CustomerOrderStatusService } from '../../services/customer.order.status.service';
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
  @Input() isForPaymentAssocationIntegration: boolean = false;
  orders: OrderingSearchResult[] | undefined;
  availableColumns: SortTableColumn<OrderingSearchResult>[] = [];
  columnToDisplayOnDashboard: string[] = ["id", "customerOrderLabel", "customerOrderStatus", "affaireLabel", "serviceTypeLabel", "createdDate", "lastStatusUpdate"];
  displayedColumns: SortTableColumn<OrderingSearchResult>[] = [];
  tableAction: SortTableAction<OrderingSearchResult>[] = [];
  bookmark: OrderingSearch | undefined;

  @Output() actionBypass: EventEmitter<OrderingSearchResult> = new EventEmitter<OrderingSearchResult>();
  @Input() overrideIconAction: string = "";
  @Input() overrideTooltipAction: string = "";
  @Input() defaultStatusFilter: string[] | undefined;
  searchedTiers: IndexEntity | undefined;

  allEmployees: Employee[] | undefined;

  constructor(
    private appService: AppService,
    private orderingSearchResultService: OrderingSearchResultService,
    private employeeService: EmployeeService,
    private userPreferenceService: UserPreferenceService,
    private formBuilder: FormBuilder,
    private customerOrderStatusService: CustomerOrderStatusService
  ) { }

  ngOnInit() {
    this.employeeService.getEmployees().subscribe(response => {

      this.allEmployees = response;

      if (!this.isForDashboard && !this.isForTiersIntegration)
        this.appService.changeHeaderTitle("Commande")
      this.availableColumns = [];
      this.availableColumns.push({ id: "id", fieldName: "customerOrderId", label: "N°" } as SortTableColumn<OrderingSearchResult>);
      this.availableColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Création", valueFonction: formatDateForSortTable } as SortTableColumn<OrderingSearchResult>);
      this.availableColumns.push({ id: "origin", fieldName: "customerOrderOriginLabel", label: "Origine" } as SortTableColumn<OrderingSearchResult>);
      this.availableColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire(s)", isShrinkColumn: true } as SortTableColumn<OrderingSearchResult>);
      this.availableColumns.push({ id: "serviceTypeLabel", fieldName: "serviceTypeLabel", label: "Service(s)", isShrinkColumn: true } as SortTableColumn<OrderingSearchResult>);
      this.availableColumns.push({ id: "customerOrderStatus", fieldName: "customerOrderStatus", label: "Statut" } as SortTableColumn<OrderingSearchResult>);
      this.availableColumns.push({ id: "customerOrderDescription", fieldName: "customerOrderDescription", label: "Description", isShrinkColumn: true } as SortTableColumn<OrderingSearchResult>);
      this.availableColumns.push({ id: "tiersLabel", fieldName: "tiersLabel", label: "Tiers" } as SortTableColumn<OrderingSearchResult>);
      this.availableColumns.push({ id: "customerOrderLabel", fieldName: "customerOrderLabel", label: "Donneur d'ordre" } as SortTableColumn<OrderingSearchResult>);
      this.availableColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Prix TTC", valueFonction: formatEurosForSortTable } as SortTableColumn<OrderingSearchResult>);
      this.availableColumns.push({ id: "depositTotalAmount", fieldName: "depositTotalAmount", label: "Acompte versé", valueFonction: formatEurosForSortTable } as SortTableColumn<OrderingSearchResult>);
      this.availableColumns.push({ id: "salesEmployee", fieldName: "salesEmployeeId", label: "Commercial", displayAsEmployee: true } as SortTableColumn<OrderingSearchResult>);
      this.availableColumns.push({ id: "assignedToEmployee", fieldName: "assignedToEmployeeId", label: "Assignée à", displayAsEmployee: true } as SortTableColumn<OrderingSearchResult>);
      this.availableColumns.push({ id: "lastStatusUpdate", fieldName: "lastStatusUpdate", label: "Mise à jour", valueFonction: formatDateTimeForSortTable } as SortTableColumn<OrderingSearchResult>);
      this.setColumns();

      if (this.overrideIconAction == "") {
        this.tableAction.push({
          actionIcon: "shopping_cart", actionName: "Voir la commande", actionLinkFunction: (action: SortTableAction<OrderingSearchResult>, element: OrderingSearchResult) => {
            if (element)
              return ['/order', element.customerOrderId];
            return undefined;
          }, display: true,
        } as SortTableAction<OrderingSearchResult>);
      } else {
        this.tableAction.push({
          actionIcon: this.overrideIconAction, actionName: this.overrideTooltipAction, actionClick: (column: SortTableAction<OrderingSearchResult>, element: OrderingSearchResult, event: any) => {
            this.actionBypass.emit(element);
          }, display: true,
        } as SortTableAction<OrderingSearchResult>);
      };
      if ((this.isForDashboard || this.isForTiersIntegration) && !this.orders && this.orderingSearch) {
        this.customerOrderStatusService.getCustomerOrderStatus().subscribe(res => {
          if (this.isForTiersIntegration && !this.orderingSearch.customerOrderStatus) {
            let status = [] as Array<CustomerOrderStatus>;
            status.push(this.customerOrderStatusService.getCustomerStatusByCode(res, CUSTOMER_ORDER_STATUS_OPEN)!);
            status.push(this.customerOrderStatusService.getCustomerStatusByCode(res, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT)!);
            status.push(this.customerOrderStatusService.getCustomerStatusByCode(res, CUSTOMER_ORDER_STATUS_TO_BILLED)!);
            status.push(this.customerOrderStatusService.getCustomerStatusByCode(res, CUSTOMER_ORDER_STATUS_BEING_PROCESSED)!);
            this.orderingSearch.customerOrderStatus = status;
          }
          this.searchOrders();
        })
      } else {
        this.bookmark = this.userPreferenceService.getUserSearchBookmark("customerOrders") as OrderingSearch;

        if (this.bookmark && !this.isForDashboard && !this.isForTiersIntegration) {
          this.orderingSearch = this.bookmark;
          if (this.orderingSearch.startDate)
            this.orderingSearch.startDate = new Date(this.orderingSearch.startDate);
          if (this.orderingSearch.endDate)
            this.orderingSearch.endDate = new Date(this.orderingSearch.endDate);
          this.searchOrders();
        }
      }
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

  getColumnLink(column: SortTableColumn<OrderingSearchResult>, element: OrderingSearchResult) {
    if (element && column.id == "tiersLabel") {
      return ['/tiers', element.tiersId];
    }
    if (element && column.id == "customerOrderLabel") {
      if (element.responsableId)
        return ['/tiers/responsable', element.responsableId];
      if (element.tiersId)
        return ['/tiers', element.tiersId];
    }
    return ['/tiers'];
  }

  searchOrders() {
    if (this.orderingSearchForm.valid) {
      if (!this.isForDashboard && !this.isForTiersIntegration && !this.isForPaymentAssocationIntegration)
        this.userPreferenceService.setUserSearchBookmark(this.orderingSearch, "customerOrders");
      if (this.orderingSearch.startDate)
        this.orderingSearch.startDate = new Date(toIsoString(this.orderingSearch.startDate));
      if (this.orderingSearch.endDate)
        this.orderingSearch.endDate = new Date(toIsoString(this.orderingSearch.endDate));
      if (this.searchedTiers) {
        this.orderingSearch.customerOrders = [];
        this.orderingSearch.customerOrders.push({ id: this.searchedTiers.entityId } as Tiers)
      }
      this.orderingSearchResultService.getOrders(this.orderingSearch).subscribe(response => {
        this.orders = response;
      })
    }
  }
}
