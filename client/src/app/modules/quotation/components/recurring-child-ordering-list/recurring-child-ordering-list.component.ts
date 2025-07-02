import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateForSortTable, formatDateTimeForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { OrderingSearch } from '../../model/OrderingSearch';
import { OrderingSearchResult } from '../../model/OrderingSearchResult';
import { AffaireService } from '../../services/affaire.service';
import { OrderingSearchResultService } from '../../services/ordering.search.result.service';

@Component({
  selector: 'recurring-child-ordering-list',
  templateUrl: './recurring-child-ordering-list.component.html',
  styleUrls: ['./recurring-child-ordering-list.component.css']
})
export class RecurringChildOrderingListComponent implements OnInit {



  orderingSearch: OrderingSearch = {} as OrderingSearch;
  orders: OrderingSearchResult[] | undefined;
  displayedColumns: SortTableColumn<OrderingSearchResult>[] = [];
  tableAction: SortTableAction<OrderingSearchResult>[] = [];
  bookmark: OrderingSearch | undefined;

  allEmployees: Employee[] | undefined;

  constructor(
    private appService: AppService,
    private orderingSearchResultService: OrderingSearchResultService,
    private employeeService: EmployeeService,
    private userPreferenceService: UserPreferenceService,
    private formBuilder: FormBuilder,
    private affaireService: AffaireService,
  ) { }

  ngOnInit() {
    this.employeeService.getEmployees().subscribe(response => {

      this.allEmployees = response;

      this.appService.changeHeaderTitle("Commandes récurrentes")
      this.displayedColumns = [];
      this.displayedColumns.push({ id: "id", fieldName: "customerOrderId", label: "N°" } as SortTableColumn<OrderingSearchResult>);
      this.displayedColumns.push({ id: "customerOrderParentRecurringId", fieldName: "customerOrderParentRecurringId", label: "N° parente" } as SortTableColumn<OrderingSearchResult>);
      this.displayedColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Création", valueFonction: formatDateForSortTable } as SortTableColumn<OrderingSearchResult>);
      this.displayedColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire(s)", isShrinkColumn: true } as SortTableColumn<OrderingSearchResult>);
      this.displayedColumns.push({ id: "serviceTypeLabel", fieldName: "serviceTypeLabel", label: "Service(s)", isShrinkColumn: true } as SortTableColumn<OrderingSearchResult>);
      this.displayedColumns.push({ id: "customerOrderStatus", fieldName: "customerOrderStatus", label: "Statut" } as SortTableColumn<OrderingSearchResult>);
      this.displayedColumns.push({ id: "recurringStartDate", fieldName: "recurringStartDate", label: "Début", valueFonction: formatDateForSortTable } as SortTableColumn<OrderingSearchResult>);
      this.displayedColumns.push({ id: "recurringEndDate", fieldName: "recurringEndDate", label: "Fin", valueFonction: formatDateForSortTable } as SortTableColumn<OrderingSearchResult>);
      this.displayedColumns.push({ id: "lastStatusUpdate", fieldName: "lastStatusUpdate", label: "Mise à jour", valueFonction: formatDateTimeForSortTable } as SortTableColumn<OrderingSearchResult>);

      this.tableAction.push({
        actionIcon: "shopping_cart", actionName: "Voir la commande", actionLinkFunction: (action: SortTableAction<OrderingSearchResult>, element: OrderingSearchResult) => {
          if (element)
            return ['/order', element.customerOrderId];
          return undefined;
        }, display: true,
      } as SortTableAction<OrderingSearchResult>);

      this.tableAction.push({
        actionIcon: "repeat", actionName: "Voir la commande parente", actionLinkFunction: (action: SortTableAction<OrderingSearchResult>, element: OrderingSearchResult) => {
          if (element && element.customerOrderParentRecurringId)
            return ['/order', element.customerOrderParentRecurringId];
          return undefined;
        }, display: true,
      } as SortTableAction<OrderingSearchResult>);

      this.bookmark = this.userPreferenceService.getUserSearchBookmark("customerOrdersRecurringChild") as OrderingSearch;

      if (this.bookmark) {
        this.orderingSearch = this.bookmark;
        if (this.orderingSearch.recurringValidityDate)
          this.orderingSearch.recurringValidityDate = new Date(this.orderingSearch.recurringValidityDate);
      } else {
        this.orderingSearch.isDisplayOnlyRecurringCustomerOrder = true;
      }
      this.searchOrders();
      this.orderingSearch.recurringValidityDate = new Date();
    });
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

  fillAffaire(entity: IndexEntity) {
    if (entity) {
      let obj = JSON.parse((entity.text as string));
      this.affaireService.getAffaire(entity.entityId).subscribe(affaire => {
        this.orderingSearch.affaire = affaire;
      });
    }
  }

  searchOrders() {
    if (this.orderingSearchForm.valid) {
      this.userPreferenceService.setUserSearchBookmark(this.orderingSearch, "customerOrdersRecurringChild");
      this.orderingSearch.isDisplayOnlyRecurringCustomerOrder = true;
      if (this.orderingSearch.recurringValidityDate)
        this.orderingSearch.recurringValidityDate = new Date(toIsoString(this.orderingSearch.recurringValidityDate));
      this.orderingSearchResultService.getOrders(this.orderingSearch).subscribe(response => {
        this.orders = response;
      })
    }
  }

}
