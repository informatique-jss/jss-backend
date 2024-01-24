import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Tiers } from '../../model/Tiers';
import { RffSearch } from '../../model/RffSearch';
import { RffService } from '../../services/rff.service';
import { Rff } from '../../model/Rff';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { Responsable } from '../../model/Responsable';
import { TiersService } from '../../services/tiers.service';
import { formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { TiersRff } from '../../model/TiersRff';
import { ResponsablesRff } from '../../model/ResponsablesRff';
import { TiersSearch } from '../../model/TiersSearch';
import { ResponsableSearchResultService } from '../../services/responsable.search.result.service';
import { ResponsableSearchResult } from '../../model/ResponsableSearchResult';
import { OrderingSearch } from 'src/app/modules/quotation/model/OrderingSearch';
import { QuotationSearch } from 'src/app/modules/quotation/model/QuotationSearch';
import { AffaireSearch } from 'src/app/modules/quotation/model/AffaireSearch';
import { InvoiceSearch } from 'src/app/modules/invoicing/model/InvoiceSearch';

@Component({
  selector: 'visit-prepa-tiers-responsible-info',
  templateUrl: './visit-prepa-tiers-responsible-info.component.html',
  styleUrls: ['./visit-prepa-tiers-responsible-info.component.css']
})

export class VisitPrepaTiersResponsibleInfoComponent implements OnInit, AfterContentChecked {

  @Input() tiers: Tiers = {} as Tiers;
  @Input() editMode: boolean = false;

  rffSearch: RffSearch = {} as RffSearch;

  rff: Rff[] | undefined;

  tiersRff: TiersRff[] | undefined;
  responsablesRff: ResponsablesRff[] | undefined;

  tiersList: Tiers[] = [];
  rffSearchList: RffSearch[] = [];
  goNext: boolean = false;
  responsablesList: Responsable[] = [];

  responsableSearch: TiersSearch = {} as TiersSearch;
  responsableSearchResult: ResponsableSearchResult[] = [];

  orderingSearch: OrderingSearch = {} as OrderingSearch;
  quotationSearch: QuotationSearch = {} as QuotationSearch;
  provisionSearch: AffaireSearch = {} as AffaireSearch;
  invoiceSearch: InvoiceSearch = {} as InvoiceSearch;
  responsableAccountSearch: Tiers | undefined;

  displayedColumnsTiersRff: SortTableColumn<TiersRff>[] = [];
  displayedColumnsResponsablesRff: SortTableColumn<ResponsablesRff>[] = [];

  constructor(private rffService: RffService,
    private changeDetectorRef: ChangeDetectorRef,
    protected tiersService: TiersService,
    protected responsableSearchResultService :ResponsableSearchResultService,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }


  ngOnInit() {

    this.tiersList = [this.tiers];
    this.responsablesList = this.tiers.responsables;
    this.searchRff();

    this.displayedColumnsTiersRff = [];
    this.displayedColumnsResponsablesRff = [];

    this.displayedColumnsTiersRff.push({ id: "denomination", fieldName: "denomination", label: "Dénomination"  } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "address", fieldName: "address", label: "address" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "mail", fieldName: "mail", label: "mail" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "phone", fieldName: "phone", label: "phone" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "rffInsertion", fieldName: "rffInsertion", label: "RFF AL" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "rffFormalite", fieldName: "rffFormalite", label: "RFF Formalités" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "rffTotal", fieldName: "rffTotal", label: "Total HT", valueFonction: formatEurosForSortTable } as SortTableColumn<TiersRff>);

    this.displayedColumnsResponsablesRff.push({ id: "id", fieldName: "id", label: "N° du responsable" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "lastName", fieldName: "lastName", label: "Nom" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "phone", fieldName: "phone", label: "Tel" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "function", fieldName: "function", label: "Function" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "mail", fieldName: "mail", label: "Mails"} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "turnoverAmountWithTax", fieldName: "turnoverAmountWithTax", label: "Chiffre d'affaire", valueFonction: formatEurosForSortTable} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "announcementNbr", fieldName: "announcementNbr", label: "Nbre AL"} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "formalityNbr", fieldName: "formalityNbr", label: "Nbre Form"} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "rffInsertion", fieldName: "rffInsertion", label: "RFF AL" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "rffFormalite", fieldName: "rffFormalite", label: "RFF Formalités" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "rffTotal", fieldName: "rffTotal", label: "Total HT", valueFonction: formatEurosForSortTable } as SortTableColumn<ResponsablesRff>);

  }

  searchRff() {


    this.tiers.responsables.forEach(responsable => {
      this.rffSearch = {} as RffSearch;
      this.rffSearch.tiers = { entityId: this.tiers.id } as IndexEntity;
      this.rffSearch.isHideCancelledRff = false;
      this.rffSearch.responsable = { entityId: responsable.id } as IndexEntity;
      this.rffSearchList.push(this.rffSearch);

      let start = new Date();
      let d = new Date(start.getTime());
      d.setFullYear(d.getFullYear() - 2);
      d.setMonth(10);
      d.setDate(30);
      this.rffSearch.startDate = d;

      let end = new Date();
      let d2 = new Date(end.getTime());
      d2.setFullYear(d2.getFullYear() - 1);
      d2.setMonth(11);
      d2.setDate(1);
      this.rffSearch.endDate = d2;
    })

    this.rffSearchList.forEach(rffSearch => {
      this.rffService.getRffs(rffSearch).subscribe(response => {
        this.rff = response.sort((a, b) => a.responsableLabel.localeCompare(b.responsableLabel));
      });
      if(this.rff)
      this.setTiersAndResponsableRffData(this.rff);
    })
  }

  setTiersAndResponsableData(tiersRff: TiersRff[] , responsablesRff: ResponsablesRff[] ){
    if(tiersRff[0].denomination == null){
      if(!this.tiers.isIndividual)
        tiersRff[0].denomination = this.tiers.denomination;
      else
        tiersRff[0].denomination = this.tiers.firstname + " " + this.tiers.lastname;

      tiersRff[0].address = this.tiers.address + " - " + this.tiers.postalCode
      + " "+ this.tiers.city.label + " "+ this.tiers.country.label;
      tiersRff[0].mails = this.tiers.mails;
      tiersRff[0].phones = this.tiers.phones;
    }
    this.tiers.responsables.forEach(responsable => {
      this.responsablesRff?.forEach(responsablesRff =>{
        responsablesRff.id = responsable.id;
        responsablesRff.lastName = responsable.lastname;
        responsablesRff.firstName = responsable.firstname;
        responsablesRff.function = responsable.function;
        responsablesRff.mails = responsable.mails;
        responsablesRff.phones = responsable.phones;
     })
    })
     this.setResponsableSearchResultData(responsablesRff);
  }

  setTiersAndResponsableRffData(rff: Rff[]) {

    this.tiersRff = [{} as TiersRff];
    this.responsablesRff = [{} as ResponsablesRff];

    this.tiersRff[0].rffInsertion = rff[0]?.rffInsertion;
    this.tiersRff[0].rffFormalite = rff[0]?.rffFormalite;
    this.tiersRff[0].rffTotal = rff[0]?.rffTotal

    rff.forEach(rff => {
      this.responsablesRff?.forEach(responsablesRff =>{
        responsablesRff.rffInsertion = rff.rffInsertion;
        responsablesRff.rffFormalite = rff.rffFormalite;
        responsablesRff.rffTotal = rff.rffTotal
      })
    })
    this.setTiersAndResponsableData(this.tiersRff, this.responsablesRff);
  }

  setResponsableSearchResultData(responsablesRff: ResponsablesRff[]) {
    this.responsableSearch = {} as TiersSearch

    this.tiers.responsables.forEach(responsable => {
      this.responsableSearch.responsable = { entityId: responsable.id } as IndexEntity;

        responsablesRff.forEach(responsablesRff =>{
        this.responsableSearchResultService.getResponsableSearch(this.responsableSearch).subscribe(response => {
          this.responsableSearchResult = response;
          if(response){
            this.responsableSearchResult.forEach(responsableSearchResult => {
              responsablesRff.turnoverAmountWithTax = responsableSearchResult.turnoverAmountWithTax;
              responsablesRff.announcementNbr = responsableSearchResult.announcementNbr;
              responsablesRff.formalityNbr = responsableSearchResult.formalityNbr;
            })
          }
        })
        })
    })

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


