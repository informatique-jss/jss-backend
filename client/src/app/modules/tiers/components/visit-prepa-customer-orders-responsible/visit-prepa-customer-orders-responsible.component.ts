import { Component, Input, OnInit} from '@angular/core';
import { Responsable } from '../../model/Responsable';
import { CustomerOrder } from '../../../quotation/model/CustomerOrder';
import { OrderingSearch } from 'src/app/modules/quotation/model/OrderingSearch';
import { QuotationSearch } from 'src/app/modules/quotation/model/QuotationSearch';
import { InvoiceSearch } from 'src/app/modules/invoicing/model/InvoiceSearch';
import { AffaireSearch } from 'src/app/modules/quotation/model/AffaireSearch';
import { Tiers } from '../../model/Tiers';

@Component({
  selector: 'visit-prepa-customer-orders-responsible',
  templateUrl: './visit-prepa-customer-orders-responsible.component.html',
  styleUrls: ['./visit-prepa-customer-orders-responsible.component.css']
})

export class VisitPrepaCustomerOrdersResponsibleComponent implements OnInit {

  @Input() responsable: Responsable = {} as Responsable;
  @Input() editMode: boolean = false;
  @Input() tiers: Tiers = {} as Tiers;
  customerOrders: CustomerOrder[] | undefined;
  orderingSearch: OrderingSearch = {} as OrderingSearch;
  quotationSearch: QuotationSearch = {} as QuotationSearch;
  provisionSearch: AffaireSearch = {} as AffaireSearch;
  invoiceSearch: InvoiceSearch = {} as InvoiceSearch;
  responsableAccountSearch: Tiers | undefined;

  constructor(
  ) { }


  ngOnInit() {

    this.loadQuotationFilter();
  }
  loadQuotationFilter() {
    this.orderingSearch.customerOrders = [this.tiers];
    this.invoiceSearch.customerOrders = [this.tiers];
    this.quotationSearch.customerOrders = [this.tiers];
    this.provisionSearch.customerOrders = [this.tiers];

    if (this.tiers.responsables) {
      this.orderingSearch.customerOrders.push(...this.tiers.responsables);
      this.invoiceSearch.customerOrders.push(...this.tiers.responsables);
      this.quotationSearch.customerOrders.push(...this.tiers.responsables);
      this.provisionSearch.customerOrders.push(...this.tiers.responsables);
    }

    this.responsableAccountSearch = this.tiers;
  }

}


