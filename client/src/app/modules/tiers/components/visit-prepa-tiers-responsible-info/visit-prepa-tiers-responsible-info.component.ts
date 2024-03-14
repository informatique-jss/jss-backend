import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Tiers } from '../../model/Tiers';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { TiersService } from '../../services/tiers.service';
import { formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { TiersSearch } from '../../model/TiersSearch';
import { ResponsableSearchResultService } from '../../services/responsable.search.result.service';
import { ResponsableSearchResult } from '../../model/ResponsableSearchResult';
import { OrderingSearch } from 'src/app/modules/quotation/model/OrderingSearch';
import { InvoiceSearch } from 'src/app/modules/invoicing/model/InvoiceSearch';
import { TiersSearchResultService } from '../../services/tiers.search.result.service';
import { TiersSearchResult } from '../../model/TiersSearchResult';
import { TiersRff } from '../../model/TiersRff';
import { ResponsablesRff } from '../../model/ResponsablesRff';
import { OrderingSearchResultService } from 'src/app/modules/quotation/services/ordering.search.result.service';
import { OrderingSearchResult } from 'src/app/modules/quotation/model/OrderingSearchResult';
import { InvoiceSearchResultService } from 'src/app/modules/invoicing/services/invoice.search.result.service';
import { InvoiceSearchResult } from 'src/app/modules/invoicing/model/InvoiceSearchResult';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';

@Component({
  selector: 'visit-prepa-tiers-responsible-info',
  templateUrl: './visit-prepa-tiers-responsible-info.component.html',
  styleUrls: ['./visit-prepa-tiers-responsible-info.component.css']
})

export class VisitPrepaTiersResponsibleInfoComponent implements OnInit, AfterContentChecked {

  @Input() tiers: Tiers = {} as Tiers;
  searchTextLastYear: string | undefined;
  searchTextThisYear: string | undefined;
  customerOrderNbrLastYear: number | undefined;
  customerOrderNbrThisYear: number | undefined;
  remainingToPayInvoice: number| undefined;

  responsableSearchListLastYear:  TiersSearch[] | undefined;
  responsableSearchListThisYear:  TiersSearch[] | undefined;

  tiersRff: TiersRff[] | undefined;
  orders: OrderingSearchResult[] | undefined;
  ordersLastYear: OrderingSearchResult[] | undefined;

  responsablesRff: ResponsablesRff[] | undefined;

  tiersSearch: TiersSearch = {} as TiersSearch;
  tiersSearchResultLastYear: TiersSearchResult[] = [];
  tiersSearchResultThisYear: TiersSearchResult[] = [];

  responsableSearch: TiersSearch = {} as TiersSearch;
  responsableSearchThisYear: TiersSearch = {} as TiersSearch;

  responsableSearchResultLastYear: ResponsableSearchResult[] | undefined;
  responsableSearchResultThisYear: ResponsableSearchResult[] | undefined;

  responsableSearchResultListThisYear: ResponsableSearchResult[] | undefined;
  responsableSearchResultListLastYear: ResponsableSearchResult[] | undefined;

  orderingSearch: OrderingSearch = {} as OrderingSearch;
  invoiceSearch: InvoiceSearch = {} as InvoiceSearch;
  invoices: InvoiceSearchResult[] | undefined;

  displayedColumnsTiersRff: SortTableColumn<TiersRff>[] = [];

  displayedColumnsResponsablesRff: SortTableColumn<ResponsablesRff>[] = [];

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    protected tiersService: TiersService,
    protected responsableSearchResultService :ResponsableSearchResultService,
    protected tiersSearchResultService :TiersSearchResultService,
    protected orderingSearchResultService: OrderingSearchResultService,
    protected invoiceSearchResultService: InvoiceSearchResultService,
    private constantService: ConstantService,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {

    this.displayedColumnsTiersRff = [];
    this.displayedColumnsResponsablesRff = [];
    this.tiersSearchResultLastYear= [{} as TiersSearchResult];
    this.tiersSearchResultThisYear= [{} as TiersSearchResult];
    this.responsableSearchListLastYear = [{} as TiersSearch];
    this.responsableSearchResultLastYear = [{} as ResponsableSearchResult];
    this.responsableSearchResultListLastYear = [{} as ResponsableSearchResult];
    this.responsableSearchListThisYear = [{} as TiersSearch];
    this.responsableSearchResultThisYear = [{} as ResponsableSearchResult];
    this.responsableSearchResultListThisYear = [{} as ResponsableSearchResult];
    this.responsablesRff = [{} as ResponsablesRff];

    this.displayedColumnsTiersRff.push({ id: "denomination", fieldName: "denomination", label: "Dénomination"  } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "address", fieldName: "address", label: "Addresse" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "mails", fieldName: "mails", label: "Mails", valueFonction: (element: TiersRff, column: SortTableColumn<TiersRff>) => { return ((element.mails) ? element.mails.map((e: { mail: any; }) => e.mail).join(", ") : "") } } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "phones", fieldName: "phones", label: "Téléphones", valueFonction: (element: TiersRff, column: SortTableColumn<TiersRff>) => { return ((element.phones) ? element.phones.map((e: { phoneNumber: any; }) => e.phoneNumber).join(", ") : "") } } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "turnoverAmountWithTax", fieldName: "turnoverAmountWithTax", label: "Chiffre d'affaires", valueFonction: formatEurosForSortTable} as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "turnoverAmountWithTaxLastYear", fieldName: "turnoverAmountWithTaxLastYear", label: "Chiffre d'affaires (N-1)", valueFonction: formatEurosForSortTable} as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "announcementNbr", fieldName: "announcementNbr", label: "Nbr AL" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "announcementNbrLastYear", fieldName: "announcementNbrLastYear", label: "Nbr AL (N-1)" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "formalityNbr", fieldName: "formalityNbr", label: "Nbr Formalités"} as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "formalityNbrLastYear", fieldName: "formalityNbrLastYear", label: "Nbr Formalités (N-1)"} as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "customerOrderNbr", fieldName: "customerOrderNbr", label: "Nbr Commandes"} as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "customerOrderNbrLastYear", fieldName: "customerOrderNbrLastYear", label: "Nbr Commandes (N-1)"} as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "remainingToPayInvoice", fieldName: "remainingToPayInvoice", label: "Reste à payer", valueFonction: formatEurosForSortTable} as SortTableColumn<TiersRff>);


    this.displayedColumnsResponsablesRff.push({ id: "id", fieldName: "id", label: "N° du responsable" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "isActive", fieldName: "isActive", label: "est actif?" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "name", fieldName: "name", label: "Nom" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "phones", fieldName: "phones", label: "Téléphones", valueFonction: (element: ResponsablesRff, column: SortTableColumn<ResponsablesRff>) => { return ((element.phones) ? element.phones.map((e: { phoneNumber: any; }) => e.phoneNumber).join(", ") : "") } } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "function", fieldName: "function", label: "Fonction" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "mails", fieldName: "mails", label: "Mails", valueFonction: (element: ResponsablesRff, column: SortTableColumn<ResponsablesRff>) => { return ((element.mails) ? element.mails.map((e: { mail: any; }) => e.mail).join(", ") : "") } } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "turnoverAmountWithTax", fieldName: "turnoverAmountWithTax", label: "Chiffre d'affaires", valueFonction: formatEurosForSortTable} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "turnoverAmountWithTaxLastYear", fieldName: "turnoverAmountWithTaxLastYear", label: "Chiffre d'affaires (N - 1)", valueFonction: formatEurosForSortTable} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "announcementNbr", fieldName: "announcementNbr", label: "Nbre AL"} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "announcementNbrLastYear", fieldName: "announcementNbrLastYear", label: "Nbre AL (N-1)"} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "formalityNbr", fieldName: "formalityNbr", label: "Nbre Form"} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "formalityNbrLastYear", fieldName: "formalityNbrLastYear", label: "Nbre Form (N-1)"} as SortTableColumn<ResponsablesRff>);

    this.searchResponsablesSearchResultLastYear();
    this.searchCustomerOrderNbrThisYear();
    this.searchCustomerOrderNbrLastYear();
    this.searchTiersSearchResultThisYear();

  }

  applyFilterResponsableLastYear(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchTextLastYear = filterValue.toLowerCase();
  }

  applyFilterResponsableThisYear(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchTextThisYear = filterValue.toLowerCase();
  }


  loadInvoiceFilter(){
    this.invoiceSearch.customerOrders = [this.tiers];
    this.invoiceSearchResultService.getInvoicesList(this.invoiceSearch).subscribe(response => {
      this.invoices = response;
      this.remainingToPayInvoice = 0;
      if(response.length>0)
      for(let invoice of response){
        if(invoice.invoiceStatus!= this.constantService.getInvoiceStatusCancelled().label)
        this.remainingToPayInvoice += invoice.remainingToPay;
      }
      this.remainingToPayInvoice = parseFloat(this.remainingToPayInvoice.toFixed(2));
      this.setTiersAndResponsableData();
    })
  }

  setTiersAndResponsableData(){
    this.tiersRff = [{} as TiersRff];
    this.ordersLastYear = [{} as OrderingSearchResult];
    this.orders = [{} as OrderingSearchResult];
    this.tiersRff = [{} as TiersRff];
    this.responsablesRff = [{} as ResponsablesRff];

    if(this.customerOrderNbrLastYear!=undefined)
    this.tiersRff[0].customerOrderNbrLastYear = this.customerOrderNbrLastYear;
    if(this.customerOrderNbrThisYear!=undefined)
    this.tiersRff[0].customerOrderNbr = this.customerOrderNbrThisYear;
    if(this.remainingToPayInvoice!=undefined && this.remainingToPayInvoice!=0)
    this.tiersRff[0].remainingToPayInvoice = this.remainingToPayInvoice;
    else
    this.tiersRff[0].remainingToPayInvoice = 0;

    this.tiersRff[0].denomination = (!this.tiers.isIndividual && this.tiers.denomination !== null)
    ? this.tiers.denomination
    : this.tiers.firstname + " " + this.tiers.lastname;

    this.tiersRff[0].address = (this.tiers.address !== null)
    ? this.tiers.address + " - " + this.tiers.postalCode + " " + this.tiers.city.label + " " + this.tiers.country.label
    : "";

    this.tiersRff[0].mails = (this.tiers.mails !== null) ? this.tiers.mails : [];

    this.tiersRff[0].phones = (this.tiers.phones !== null) ? this.tiers.phones : [];

    if(this.tiersRff !== undefined && (this.tiersSearchResultLastYear[0].turnoverAmountWithTax || this.tiersSearchResultLastYear[0].announcementNbr || this.tiersSearchResultLastYear[0].formalityNbr)!=undefined)
       {
      this.tiersRff[0].turnoverAmountWithTaxLastYear = this.tiersSearchResultLastYear[0].turnoverAmountWithTax;
      this.tiersRff[0].announcementNbrLastYear = this.tiersSearchResultLastYear[0].announcementNbr;
      this.tiersRff[0].formalityNbrLastYear = this.tiersSearchResultLastYear[0].formalityNbr;
    }

    if(this.tiersRff !== undefined && (this.tiersSearchResultThisYear[0].turnoverAmountWithTax || this.tiersSearchResultThisYear[0].announcementNbr || this.tiersSearchResultThisYear[0].formalityNbr)!=undefined)
       {
      this.tiersRff[0].turnoverAmountWithTax = this.tiersSearchResultThisYear[0].turnoverAmountWithTax;
      this.tiersRff[0].announcementNbr = this.tiersSearchResultThisYear[0].announcementNbr;
      this.tiersRff[0].formalityNbr = this.tiersSearchResultThisYear[0].formalityNbr;
    }

    if(this.responsableSearchResultListLastYear && this.responsableSearchResultListLastYear!= undefined && this.responsableSearchResultListLastYear.length>1){
    for(let i = 1; i<this.responsableSearchResultListLastYear.length; i++) {
      for(let j = 0; j<this.tiers.responsables.length; j++){
        if(this.tiers.responsables[j].id == this.responsableSearchResultListLastYear[i].responsableId ){
          if(this.responsableSearchResultListLastYear[i].responsableId!=undefined)
          this.responsablesRff[i-1].id = this.responsableSearchResultListLastYear[i].responsableId;
          if(this.responsableSearchResultListLastYear[i].responsableLabel!=undefined)
          this.responsablesRff[i-1].name = this.responsableSearchResultListLastYear[i].responsableLabel;
          if(this.tiers.responsables[j].phones!=undefined)
          this.responsablesRff[i-1].phones = this.tiers.responsables[j].phones;
          if(this.tiers.responsables[j].mails!=undefined)
          this.responsablesRff[i-1].mails = this.tiers.responsables[j].mails;
          if(this.tiers.responsables[j].function!=undefined)
          this.responsablesRff[i-1].function = this.tiers.responsables[j].function;
          if(this.responsableSearchResultListLastYear[i].turnoverAmountWithTax!=undefined)
          this.responsablesRff[i-1].turnoverAmountWithTaxLastYear = this.responsableSearchResultListLastYear[i].turnoverAmountWithTax;
          else
          this.responsablesRff[i-1].turnoverAmountWithTaxLastYear = 0;
          if(this.responsableSearchResultListLastYear[i].formalityNbr!=undefined)
          this.responsablesRff[i-1].formalityNbrLastYear = this.responsableSearchResultListLastYear[i].formalityNbr;
          else
          this.responsablesRff[i-1].formalityNbrLastYear = 0;
          if(this.responsableSearchResultListLastYear[i].announcementNbr!=undefined)
          this.responsablesRff[i-1].announcementNbrLastYear = this.responsableSearchResultListLastYear[i].announcementNbr;
          else
          this.responsablesRff[i-1].announcementNbrLastYear = 0;
          if(this.tiers.responsables[j].isActive!=undefined)
          this.responsablesRff[i-1].isActive = this.tiers.responsables[j].isActive;
          this.responsablesRff.push({...this.responsablesRff[i-1]});
        }
      }
    }
  }

if(this.responsableSearchResultListThisYear && this.responsableSearchResultListThisYear!= undefined && this.responsableSearchResultListThisYear.length>1){
  for(let i = 1; i<this.responsableSearchResultListThisYear.length; i++) {
    for(let j = 0; j<this.tiers.responsables.length; j++){
      if(this.tiers.responsables[j].id == this.responsableSearchResultListThisYear[i].responsableId){
        if(this.responsableSearchResultListThisYear[i].responsableId!=undefined)
        if(this.responsableSearchResultListThisYear[i].turnoverAmountWithTax!=undefined)
        this.responsablesRff[i-1].turnoverAmountWithTax = this.responsableSearchResultListThisYear[i].turnoverAmountWithTax;
        if(this.responsableSearchResultListThisYear[i].formalityNbr!=undefined)
        this.responsablesRff[i-1].formalityNbr = this.responsableSearchResultListThisYear[i].formalityNbr;
        if(this.responsableSearchResultListThisYear[i].announcementNbr!=undefined)
        this.responsablesRff[i-1].announcementNbr = this.responsableSearchResultListThisYear[i].announcementNbr;
        this.responsablesRff.push({...this.responsablesRff[i]});
      }
    }
  }

}
  }

searchTiersSearchResultLastYear() {
  this.tiersSearch.tiers = { entityId: this.tiers.id } as IndexEntity;
  let start = new Date();

  start.setMonth(0);
  start.setFullYear(start.getFullYear() - 1);
  start.setDate(1);
  this.tiersSearch.startDate = start;

  let end = new Date();
  end.setFullYear(end.getFullYear() - 1);
  end.setMonth(11);
  end.setDate(31);
  this.tiersSearch.endDate = end;

  this.tiersSearchResultService.getTiersSearch(this.tiersSearch).subscribe(
    (resp) => {
      this.tiersSearchResultLastYear = resp;
      this.loadInvoiceFilter();
    },
  );
}

searchTiersSearchResultThisYear() {
  this.tiersSearch.tiers = { entityId: this.tiers.id } as IndexEntity;
  let start = new Date();

  start.setMonth(0);
  start.setDate(1);
  start.setFullYear(start.getFullYear());
  this.tiersSearch.startDate = start;

  let end = new Date();
  this.tiersSearch.endDate = end;

  this.tiersSearchResultService.getTiersSearch(this.tiersSearch).subscribe(
    (resp) => {
      this.tiersSearchResultThisYear = resp;
      this.searchTiersSearchResultLastYear();

    },
  );
}

searchResponsablesSearchResultLastYear() {

  for(let i = 0; i<this.tiers.responsables.length; i++){
  this.responsableSearch.responsable = { entityId: this.tiers.responsables[i].id } as IndexEntity;
  let currentDate = new Date();
  let start = new Date();

  start.setFullYear(currentDate.getFullYear() - 1);
  start.setMonth(0);
  start.setDate(1);
  this.responsableSearch.startDate = start;

  let end = new Date();
  end.setFullYear(end.getFullYear() - 1);
  end.setMonth(11);
  end.setDate(31);
  this.responsableSearch.endDate = end;

  this.responsableSearchListLastYear?.push({...this.responsableSearch});
}

if (this.responsableSearchListLastYear) {
  this.responsableSearchListLastYear.slice(1).forEach(responsable => {
    this.responsableSearchResultService.getResponsableSearch(responsable).subscribe(
      (resp) => {
        this.responsableSearchResultLastYear = resp;
        this.responsableSearchResultListLastYear?.push(...this.responsableSearchResultLastYear)
      },
    );
  });
}
}

searchResponsablesSearchResultThisYear() {

  for(let i = 0; i<this.tiers.responsables.length; i++){
  this.responsableSearchThisYear.responsable = { entityId: this.tiers.responsables[i].id } as IndexEntity;
  let currentDate = new Date();
  let start = new Date();

  start.setFullYear(currentDate.getFullYear());
  start.setMonth(0);
  start.setDate(1);
  this.responsableSearchThisYear.startDate = start;

  let end = new Date();
  end.setFullYear(end.getFullYear());
  end.setMonth(11);
  end.setDate(31);
  this.responsableSearchThisYear.endDate = end;
  this.responsableSearchListThisYear?.push({...this.responsableSearchThisYear});
}

if (this.responsableSearchListThisYear) {
  this.responsableSearchListThisYear.slice(1).forEach(responsable => {
    this.responsableSearchResultService.getResponsableSearch(responsable).subscribe(
      (resp) => {
        this.responsableSearchResultThisYear = resp;
        this.responsableSearchResultListThisYear?.push(...this.responsableSearchResultThisYear)
      },
    );
  });
}
}

searchCustomerOrderNbrLastYear() {
  this.orderingSearch.customerOrders = [this.tiers];
  let start = new Date();

  start.setMonth(0);
  start.setFullYear(start.getFullYear() - 1);
  start.setDate(1);
  this.orderingSearch.startDate = start;

  let end = new Date();
  end.setFullYear(end.getFullYear() - 1);
  end.setMonth(11);
  end.setDate(31);
  this.orderingSearch.endDate = end;

  this.orderingSearchResultService.getOrders(this.orderingSearch).subscribe(response => {
    this.ordersLastYear = response;
    const uniqueCustomerOrderIdsLastYear = new Set(this.ordersLastYear.map(order => order.customerOrderId).filter(id => id !== undefined));
      this.customerOrderNbrLastYear = [...uniqueCustomerOrderIdsLastYear].length;
  })
}

searchCustomerOrderNbrThisYear() {
  this.orderingSearch.customerOrders = [this.tiers];
  let start = new Date();

  start.setMonth(0);
  start.setFullYear(start.getFullYear());
  start.setDate(1);
  this.orderingSearch.startDate = start;

  let end = new Date();
  end.setFullYear(end.getFullYear());
  end.setMonth(11);
  end.setDate(31);
  this.orderingSearch.endDate = end;

  this.orderingSearchResultService.getOrders(this.orderingSearch).subscribe(response => {
    this.orders = response;
    const uniqueCustomerOrderIdsThisYear = new Set(this.orders.map(order => order.customerOrderId).filter(id => id !== undefined));
    this.customerOrderNbrThisYear = [...uniqueCustomerOrderIdsThisYear].length;
    this.searchCustomerOrderNbrLastYear();
  })
}
}
