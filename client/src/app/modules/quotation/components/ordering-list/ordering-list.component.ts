import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateTimeForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { AppService } from 'src/app/services/app.service';
import { CustomerOrder } from '../../model/CustomerOrder';
import { IQuotation } from '../../model/IQuotation';
import { OrderingSearch } from '../../model/OrderingSearch';
import { QuotationService } from '../../services/quotation.service';
import { QuotationComponent } from '../quotation/quotation.component';

@Component({
  selector: 'ordering-list',
  templateUrl: './ordering-list.component.html',
  styleUrls: ['./ordering-list.component.css']
})
export class OrderingListComponent implements OnInit {
  orderingSearch: OrderingSearch = {} as OrderingSearch;
  orders: IQuotation[] | undefined;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];

  @Output() actionBypass: EventEmitter<CustomerOrder> = new EventEmitter<CustomerOrder>();
  @Input() overrideIconAction: string = "";
  @Input() overrideTooltipAction: string = "";
  @Input() defaultStatusFilter: string[] | undefined;

  constructor(
    private appService: AppService,
    private quotationService: QuotationService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() {
    this.putDefaultPeriod();
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N° de la commande" } as SortTableColumn);
    this.displayedColumns.push({ id: "customerOrderName", fieldName: "tiers", label: "Donneur d'ordre", valueFonction: this.getCustomerOrderName, actionLinkFunction: this.getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la fiche du donneur d'ordre" } as SortTableColumn);
    this.displayedColumns.push({ id: "quotationStatus", fieldName: "quotationStatus.label", label: "Statut" } as SortTableColumn);
    this.displayedColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date de création", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "salesEmployee", fieldName: "salesEmployee", label: "Commercial", displayAsEmployee: true, valueFonction: this.getSalesEmployee } as SortTableColumn);
    this.displayedColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Prix total", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string => { return QuotationComponent.computePriceTotal(element) + " €"; } } as SortTableColumn);

    if (this.overrideIconAction == "") {
      this.tableAction.push({
        actionIcon: "settings", actionName: "Voir la commande", actionLinkFunction: (action: SortTableAction, element: any) => {
          if (element)
            return ['/order', element.id];
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
  }


  orderingSearchForm = this.formBuilder.group({
  });

  getSalesEmployee(element: any): Employee | undefined {
    if (element) {
      if (element.confrere && element.confrere.salesEmployee)
        return element.confrere.salesEmployee;
      if (element.responsable && element.responsable && element.responsable.salesEmployee)
        return element.responsable.salesEmployee;
      if (element.responsable && element.responsable.tiers && element.responsable.tiers.salesEmployee)
        return element.responsable.tiers.salesEmployee;
      if (element.tiers && element.tiers.salesEmployee)
        return element.tiers.salesEmployee;
    }
    return undefined;
  }

  getColumnLink(column: SortTableColumn, element: any) {
    if (element && column.id == "customerOrderName") {
      if (element.responsable)
        return ['/tiers/responsable/', element.responsable.id];
      if (element.tiers)
        return ['/tiers/', element.tiers.id];
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
      this.orderingSearch.startDate = new Date(toIsoString(this.orderingSearch.startDate));
      this.orderingSearch.endDate = new Date(toIsoString(this.orderingSearch.endDate));
      this.quotationService.getOrders(this.orderingSearch).subscribe(response => {
        this.orders = response;
      })
    }
  }
}
