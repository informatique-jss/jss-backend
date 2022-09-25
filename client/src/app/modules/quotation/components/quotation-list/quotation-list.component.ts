import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { PAYEMENT_WAY_INBOUND_CODE } from 'src/app/libs/Constants';
import { formatDateTimeForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { IQuotation } from '../../model/IQuotation';
import { OrderingSearch } from '../../model/OrderingSearch';
import { QuotationService } from '../../services/quotation.service';

@Component({
  selector: 'quotation-list',
  templateUrl: './quotation-list.component.html',
  styleUrls: ['./quotation-list.component.css']
})
export class QuotationListComponent implements OnInit {
  orderingSearch: OrderingSearch = {} as OrderingSearch;
  quotations: IQuotation[] | undefined;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];
  PAYEMENT_WAY_INBOUND_CODE = PAYEMENT_WAY_INBOUND_CODE;

  constructor(
    private appService: AppService,
    private quotationService: QuotationService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() {
    this.putDefaultPeriod();
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N° de la commande" } as SortTableColumn);
    this.displayedColumns.push({ id: "customerOrderName", fieldName: "tiers", label: "Donneur d'ordre", valueFonction: this.getCustomerOrderName, actionLinkFunction: this.getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la fiche du donneur d'ordre" } as SortTableColumn);
    this.displayedColumns.push({ id: "quotationStatus", fieldName: "quotationStatus.label", label: "Statut" } as SortTableColumn);
    this.displayedColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date de création", valueFonction: formatDateTimeForSortTable } as SortTableColumn);

    this.tableAction.push({ actionIcon: "settings", actionName: "Voir le devis", actionLinkFunction: this.getActionLink, display: true, } as SortTableAction);
  }

  orderingSearchForm = this.formBuilder.group({
  });

  getActionLink(action: SortTableAction, element: any) {
    if (element)
      return ['/quotation', element.id];
    return undefined;
  }
  getColumnLink(column: SortTableColumn, element: any) {
    if (element && column.id == "customerOrderName") {
      if (element.responsable)
        return ['/tiers/responsable/', element.responsable.id];
      if (element.tiers)
        return ['/tiers/', element.tiers.id];
    }
    return ['/tiers', element.tiers.id];
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
      this.quotationService.getQuotations(this.orderingSearch).subscribe(response => {
        this.quotations = response;
      })
    }
  }
}
