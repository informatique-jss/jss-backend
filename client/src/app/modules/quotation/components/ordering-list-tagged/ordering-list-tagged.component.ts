import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { CUSTOMER_ORDER_STATUS_BEING_PROCESSED, CUSTOMER_ORDER_STATUS_OPEN, CUSTOMER_ORDER_STATUS_TO_BILLED, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT } from 'src/app/libs/Constants';
import { formatDateForSortTable, formatDateTimeForSortTable, formatEurosForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { CustomerOrderStatus } from '../../model/CustomerOrderStatus';
import { OrderingSearchTagged } from '../../model/OrderingSearchTagged';
import { OrderingSearchTaggedResult } from '../../model/OrderingSearchTaggedResult';
import { CustomerOrderStatusService } from '../../services/customer.order.status.service';
import { OrderingSearchTaggedResultService } from '../../services/ordering.search.tagged.result.service';

@Component({
  selector: 'ordering-list-tagged',
  templateUrl: './ordering-list-tagged.component.html',
  styleUrls: ['./ordering-list-tagged.component.css']
})
export class OrderingListTaggedComponent implements OnInit {

  @Input() orderingSearchTagged: OrderingSearchTagged = {} as OrderingSearchTagged;
  @Input() isForDashboard: boolean = false;
  @Input() isForPaymentAssocationIntegration: boolean = false;
  orders: OrderingSearchTaggedResult[] | undefined;
  availableColumns: SortTableColumn<OrderingSearchTaggedResult>[] = [];
  columnToDisplayOnDashboard: string[] = ["id", "customerOrderLabel", "customerOrderStatus", "affaireLabel", "serviceTypeLabel", "createdDate", "lastStatusUpdate", "activeDirectoryGroupLabel"];
  displayedColumns: SortTableColumn<OrderingSearchTaggedResult>[] = [];
  tableAction: SortTableAction<OrderingSearchTaggedResult>[] = [];
  bookmark: OrderingSearchTagged | undefined;

  @Output() actionBypass: EventEmitter<OrderingSearchTaggedResult> = new EventEmitter<OrderingSearchTaggedResult>();
  @Input() overrideIconAction: string = "";
  @Input() overrideTooltipAction: string = "";
  @Input() defaultStatusFilter: string[] | undefined;
  searchedTiers: IndexEntity | undefined;
  allEmployees: Employee[] | undefined;

  constructor(
    private orderingSearchTaggedResultService: OrderingSearchTaggedResultService,
    private employeeService: EmployeeService,
    private userPreferenceService: UserPreferenceService,
    private formBuilder: FormBuilder,
    private customerOrderStatusService: CustomerOrderStatusService
  ) { }

  ngOnInit() {
    this.employeeService.getEmployees().subscribe(response => {

      this.allEmployees = response;

      this.availableColumns = [];
      this.availableColumns.push({ id: "id", fieldName: "customerOrderId", label: "N°" } as SortTableColumn<OrderingSearchTaggedResult>);
      this.availableColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Création", valueFonction: formatDateForSortTable } as SortTableColumn<OrderingSearchTaggedResult>);
      this.availableColumns.push({ id: "origin", fieldName: "customerOrderOriginLabel", label: "Origine" } as SortTableColumn<OrderingSearchTaggedResult>);
      this.availableColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire(s)", isShrinkColumn: true } as SortTableColumn<OrderingSearchTaggedResult>);
      this.availableColumns.push({ id: "serviceTypeLabel", fieldName: "serviceTypeLabel", label: "Service(s)", isShrinkColumn: true } as SortTableColumn<OrderingSearchTaggedResult>);
      this.availableColumns.push({ id: "customerOrderStatus", fieldName: "customerOrderStatus", label: "Statut" } as SortTableColumn<OrderingSearchTaggedResult>);
      this.availableColumns.push({ id: "customerOrderDescription", fieldName: "customerOrderDescription", label: "Description", isShrinkColumn: true } as SortTableColumn<OrderingSearchTaggedResult>);
      this.availableColumns.push({ id: "tiersLabel", fieldName: "tiersLabel", label: "Tiers" } as SortTableColumn<OrderingSearchTaggedResult>);
      this.availableColumns.push({ id: "customerOrderLabel", fieldName: "customerOrderLabel", label: "Donneur d'ordre" } as SortTableColumn<OrderingSearchTaggedResult>);
      this.availableColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Prix TTC", valueFonction: formatEurosForSortTable } as SortTableColumn<OrderingSearchTaggedResult>);
      this.availableColumns.push({ id: "depositTotalAmount", fieldName: "depositTotalAmount", label: "Acompte versé", valueFonction: formatEurosForSortTable } as SortTableColumn<OrderingSearchTaggedResult>);
      this.availableColumns.push({ id: "salesEmployee", fieldName: "salesEmployeeId", label: "Commercial", displayAsEmployee: true } as SortTableColumn<OrderingSearchTaggedResult>);
      this.availableColumns.push({ id: "assignedToEmployee", fieldName: "assignedToEmployeeId", label: "Assignée à", displayAsEmployee: true } as SortTableColumn<OrderingSearchTaggedResult>);
      this.availableColumns.push({ id: "lastStatusUpdate", fieldName: "lastStatusUpdate", label: "Mise à jour", valueFonction: formatDateTimeForSortTable } as SortTableColumn<OrderingSearchTaggedResult>);
      this.availableColumns.push({ id: "activeDirectoryGroupLabel", fieldName: "activeDirectoryGroupLabel", label: "Groupe taggé" } as SortTableColumn<OrderingSearchTaggedResult>);
      this.setColumns();

      if (this.overrideIconAction == "") {
        this.tableAction.push({
          actionIcon: "shopping_cart", actionName: "Voir la commande", actionLinkFunction: (action: SortTableAction<OrderingSearchTaggedResult>, element: OrderingSearchTaggedResult) => {
            if (element)
              return ['/order', element.customerOrderId];
            return undefined;
          }, display: true,
        } as SortTableAction<OrderingSearchTaggedResult>);
      } else {
        this.tableAction.push({
          actionIcon: this.overrideIconAction, actionName: this.overrideTooltipAction, actionClick: (column: SortTableAction<OrderingSearchTaggedResult>, element: OrderingSearchTaggedResult, event: any) => {
            this.actionBypass.emit(element);
          }, display: true,
        } as SortTableAction<OrderingSearchTaggedResult>);
      };
      if ((this.isForDashboard) && !this.orders && this.orderingSearchTagged) {
        this.customerOrderStatusService.getCustomerOrderStatus().subscribe(res => {
          if (!this.orderingSearchTagged.customerOrderStatus) {
            let status = [] as Array<CustomerOrderStatus>;
            status.push(this.customerOrderStatusService.getCustomerStatusByCode(res, CUSTOMER_ORDER_STATUS_OPEN)!);
            status.push(this.customerOrderStatusService.getCustomerStatusByCode(res, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT)!);
            status.push(this.customerOrderStatusService.getCustomerStatusByCode(res, CUSTOMER_ORDER_STATUS_TO_BILLED)!);
            status.push(this.customerOrderStatusService.getCustomerStatusByCode(res, CUSTOMER_ORDER_STATUS_BEING_PROCESSED)!);
            this.orderingSearchTagged.customerOrderStatus = status;
          }
          this.searchOrders();
        })
      } else {
        this.bookmark = this.userPreferenceService.getUserSearchBookmark("customerOrders") as OrderingSearchTagged;

        if (this.bookmark && !this.isForDashboard) {
          this.orderingSearchTagged = this.bookmark;
          if (this.orderingSearchTagged.startDate)
            this.orderingSearchTagged.startDate = new Date(this.orderingSearchTagged.startDate);
          if (this.orderingSearchTagged.endDate)
            this.orderingSearchTagged.endDate = new Date(this.orderingSearchTagged.endDate);
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

  orderingSearchTaggedForm = this.formBuilder.group({
  });

  searchOrders() {
    if (this.orderingSearchTaggedForm.valid) {
      if (!this.isForDashboard && !this.isForPaymentAssocationIntegration)
        this.userPreferenceService.setUserSearchBookmark(this.orderingSearchTagged, "customerOrders");
      if (this.orderingSearchTagged.startDate)
        this.orderingSearchTagged.startDate = new Date(toIsoString(this.orderingSearchTagged.startDate));
      if (this.orderingSearchTagged.endDate)
        this.orderingSearchTagged.endDate = new Date(toIsoString(this.orderingSearchTagged.endDate));
      if (this.searchedTiers) {
        this.orderingSearchTagged.customerOrders = [];
        this.orderingSearchTagged.customerOrders.push({ id: this.searchedTiers.entityId } as Tiers)
      }
      this.orderingSearchTaggedResultService.getOrdersTagged(this.orderingSearchTagged).subscribe(response => {
        this.orders = response;
      })
    }
  }
}
