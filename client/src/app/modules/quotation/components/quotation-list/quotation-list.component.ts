import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateTimeForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { Employee } from '../../../profile/model/Employee';
import { IQuotation } from '../../model/IQuotation';
import { QuotationSearch } from '../../model/QuotationSearch';
import { QuotationService } from '../../services/quotation.service';
import { QuotationComponent } from '../quotation/quotation.component';

@Component({
  selector: 'quotation-list',
  templateUrl: './quotation-list.component.html',
  styleUrls: ['./quotation-list.component.css']
})
export class QuotationListComponent implements OnInit {
  quotationSearch: QuotationSearch = {} as QuotationSearch;
  quotations: IQuotation[] | undefined;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];

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
    this.displayedColumns.push({ id: "salesEmployee", fieldName: "salesEmployee", label: "Commercial", displayAsEmployee: true, valueFonction: this.getSalesEmployee } as SortTableColumn);
    this.displayedColumns.push({ id: "total", fieldName: "total", label: "Montant TTC", valueFonction: this.getTotalPrice } as SortTableColumn);
    this.displayedColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date de création", valueFonction: formatDateTimeForSortTable } as SortTableColumn);

    this.tableAction.push({ actionIcon: "settings", actionName: "Voir le devis", actionLinkFunction: this.getActionLink, display: true, } as SortTableAction);
  }

  quotationSearchForm = this.formBuilder.group({
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


  putDefaultPeriod() {
    if (!this.quotationSearch.startDate && !this.quotationSearch.endDate) {
      this.quotationSearch.startDate = new Date();
      this.quotationSearch.endDate = new Date();
      this.quotationSearch.startDate.setDate(this.quotationSearch.endDate.getDate() - 30);
    }
  }

  searchOrders() {
    if (this.quotationSearchForm.valid && this.quotationSearch.startDate && this.quotationSearch.endDate) {
      this.quotationSearch.startDate = new Date(toIsoString(this.quotationSearch.startDate));
      this.quotationSearch.endDate = new Date(toIsoString(this.quotationSearch.endDate));
      this.quotationService.getQuotations(this.quotationSearch).subscribe(response => {
        this.quotations = response;
      })
    }
  }
}
