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
import { TiersSearchResultService } from '../../services/tiers.search.result.service';
import { TiersSearchResult } from '../../model/TiersSearchResult';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { forkJoin } from 'rxjs';

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
  rffResponse:  Rff[] | undefined;;
  tiersRffResponse: boolean = true;

  tiersRff: TiersRff[] | undefined;
  responsablesRff: ResponsablesRff[] | undefined;

  tiersList: Tiers[] | undefined;;
  rffSearchList: RffSearch[] | undefined;
  responsablesList: Responsable[] | undefined;;

  responsableSearch: TiersSearch = {} as TiersSearch;
  responsableSearchResult: ResponsableSearchResult[] | undefined;;
  tiersSearchResult: TiersSearchResult[] | undefined;;

  onlyResponsable: Responsable | undefined;

  orderingSearch: OrderingSearch = {} as OrderingSearch;
  quotationSearch: QuotationSearch = {} as QuotationSearch;
  provisionSearch: AffaireSearch = {} as AffaireSearch;
  invoiceSearch: InvoiceSearch = {} as InvoiceSearch;
  responsableAccountSearch: Tiers | undefined;

  tableActions: SortTableAction<Responsable>[] = [] as Array<SortTableAction<Responsable>>;

  displayedColumnsTiersRff: SortTableColumn<TiersRff>[] = [];
  displayedColumnsResponsablesRff: SortTableColumn<ResponsablesRff>[] = [];
  availableColumnsTiersRff: SortTableColumn<TiersRff>[] = [];
  availableColumnsResponsablesRff: SortTableColumn<ResponsablesRff>[] = [];

  constructor(private rffService: RffService,
    private changeDetectorRef: ChangeDetectorRef,
    protected tiersService: TiersService,
    protected responsableSearchResultService :ResponsableSearchResultService,
    protected tiersSearchResultService :TiersSearchResultService,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }


  ngOnInit() {

    this.tiersList = [this.tiers];

    this.displayedColumnsTiersRff = [];
    this.displayedColumnsResponsablesRff = [];
    this.responsablesList = this.tiers.responsables;
    this.rff = [{} as Rff];
    this.rffSearch = {} as RffSearch;
    this.rffSearchList = [{} as RffSearch];
    this.rffResponse =  [{} as Rff];

    this.displayedColumnsTiersRff.push({ id: "denomination", fieldName: "denomination", label: "Dénomination"  } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "address", fieldName: "address", label: "address" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "mails", fieldName: "mails", label: "Mails", valueFonction: (element: TiersRff, column: SortTableColumn<TiersRff>) => { return ((element.mails) ? element.mails.map((e: { mail: any; }) => e.mail).join(", ") : "") } } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "phones", fieldName: "phones", label: "Téléphones", valueFonction: (element: TiersRff, column: SortTableColumn<TiersRff>) => { return ((element.phones) ? element.phones.map((e: { phoneNumber: any; }) => e.phoneNumber).join(", ") : "") } } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "rffInsertion", fieldName: "rffInsertion", label: "RFF AL" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "rffFormalite", fieldName: "rffFormalite", label: "RFF Formalités" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "rffTotal", fieldName: "rffTotal", label: "Total HT", valueFonction: formatEurosForSortTable } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "turnoverAmountWithTax", fieldName: "turnoverAmountWithTax", label: "RFF AL" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "announcementNbr", fieldName: "announcementNbr", label: "RFF Formalités" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "formalityNbr", fieldName: "formalityNbr", label: "Total HT"} as SortTableColumn<TiersRff>);

    this.displayedColumnsResponsablesRff.push({ id: "id", fieldName: "id", label: "N° du responsable" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "lastName", fieldName: "lastName", label: "Nom" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "phones", fieldName: "phones", label: "Téléphones", valueFonction: (element: ResponsablesRff, column: SortTableColumn<ResponsablesRff>) => { return ((element.phones) ? element.phones.map((e: { phoneNumber: any; }) => e.phoneNumber).join(", ") : "") } } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "function", fieldName: "function", label: "Function" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "mails", fieldName: "mails", label: "Mails", valueFonction: (element: ResponsablesRff, column: SortTableColumn<ResponsablesRff>) => { return ((element.mails) ? element.mails.map((e: { mail: any; }) => e.mail).join(", ") : "") } } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "turnoverAmountWithTax", fieldName: "turnoverAmountWithTax", label: "Chiffre d'affaire", valueFonction: formatEurosForSortTable} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "announcementNbr", fieldName: "announcementNbr", label: "Nbre AL"} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "formalityNbr", fieldName: "formalityNbr", label: "Nbre Form"} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "rffInsertion", fieldName: "rffInsertion", label: "RFF AL" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "rffFormalite", fieldName: "rffFormalite", label: "RFF Formalités" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "rffTotal", fieldName: "rffTotal", label: "Total HT", valueFonction: formatEurosForSortTable } as SortTableColumn<ResponsablesRff>);

    this.searchRff();
  }

  searchRff() {

    this.setRffSearch();

    if(this.tiers.responsables.length>1){
      for(let i = 0; i<this.tiers.responsables.length; i++){
        this.setRffSearchResponsable(this.tiers.responsables[i]);
      }
    }else{
        this.onlyResponsable = this.tiers.responsables[0];
        this.setRffSearchResponsable(this.onlyResponsable);
    }

    if (this.rffSearchList !== undefined && this.rffSearchList.length > 1) {
      const observables = this.rffSearchList.slice(1).map(item => this.rffService.getRffs(item));

      forkJoin(observables).subscribe(
        responses => {
          for (const response of responses) {
            this.rffResponse = this.rffResponse?.concat(response);
          }

          this.setTiersAndResponsableData(this.rffResponse ?? []);
        }
      );
    } else if (this.rffSearchList !== undefined && this.rffSearchList.length === 1) {
      this.rffService.getRffs(this.rffSearchList[1]).subscribe(
        response => {
          this.rffResponse = response;
          this.setTiersAndResponsableData(this.rffResponse ?? []);
        }
      );
    }

  this.setTiersAndResponsableData(this.rffResponse?? []);

  }

  setRffSearchResponsable(responsable: Responsable) {
    this.rffSearch.responsable = { entityId: responsable.id } as IndexEntity;
    this.setRffSearch();
  }

  setRffSearch(){
    this.rffSearch.isHideCancelledRff = false;
    this.rffSearch.tiers = { entityId: this.tiers.id } as IndexEntity;

    let currentDate = new Date();
    let start = new Date();

    if (currentDate.getMonth() > 10 || (currentDate.getMonth() === 10 && currentDate.getDate() > 30)) {
      start.setFullYear(currentDate.getFullYear() - 1);
    } else {
      start.setFullYear(currentDate.getFullYear() - 2);
    }

  start.setMonth(10);
  start.setDate(30);
  this.rffSearch.startDate = start;

  let end = new Date();
  end.setFullYear(end.getFullYear() - 1);
  end.setMonth(11);
  end.setDate(1);
  this.rffSearch.endDate = end;

  this.rffSearchList?.push({ ...this.rffSearch });
}

  setTiersAndResponsableData(rff: Rff[] ){

    this.tiersRff = [{} as TiersRff];
    this.responsablesRff = [{} as ResponsablesRff];
    this.responsableSearch = {} as TiersSearch
    this.responsableSearch.tiers = { entityId: this.tiers.id } as IndexEntity;

      if(!this.tiers.isIndividual && this.tiers.denomination!=null)
      this.tiersRff[0].denomination = this.tiers.denomination;
      else{
      this.tiersRff[0].denomination = this.tiers.firstname + " " + this.tiers.lastname;
      }
      if(this.tiers.address!=null)
      this.tiersRff[0].address = this.tiers.address + " - " + this.tiers.postalCode
      + " "+ this.tiers.city.label + " "+ this.tiers.country.label;
      if(this.tiers.mails!=null)
      this.tiersRff[0].mails = this.tiers.mails;
      if(this.tiers.phones!=null)
      this.tiersRff[0].phones = this.tiers.phones;

    if(rff && rff!= undefined && rff.length>0){
      this.tiersRff[0].rffInsertion = rff[1]?.rffInsertion;
      this.tiersRff[0].rffFormalite = rff[1]?.rffFormalite;
      this.tiersRff[0].rffTotal = rff[1]?.rffTotal;

    for(let i=0; i<rff.length; i++){
      if(rff && rff[i].responsableId!=undefined && this.responsablesRff[i]!=undefined && rff[i].responsableId!=null){
        this.responsablesRff[i].id = this.tiers.responsables[i].id;
        this.responsablesRff[i].rffInsertion = rff[i+2].rffInsertion;
        this.responsablesRff[i].rffFormalite = rff[i+2].rffFormalite;
        this.responsablesRff[i].rffTotal = rff[i+2].rffTotal;
      }else{
        this.responsablesRff[i].id = this.tiers.responsables[i].id;
        this.responsablesRff[i].lastName = this.tiers.responsables[i].lastname;
        this.responsablesRff[i].firstName = this.tiers.responsables[i].firstname;
        this.responsablesRff[i].function = this.tiers.responsables[i].function;
        this.responsablesRff[i].mails = this.tiers.responsables[i].mails;
        this.responsablesRff[i].phones = this.tiers.responsables[i].phones;
      }
    }
    }


    for(let i =0; i<this.tiers.responsables.length; i++){
      this.responsableSearch.responsable = { entityId: this.tiers.responsables[i].id } as IndexEntity;

    this.responsableSearchResultService.getResponsableSearch(this.responsableSearch).subscribe(response => {
      this.responsableSearchResult = response;
      if(this.responsableSearchResult && this.responsableAccountSearch!=undefined && this.responsablesRff){
          for(let i=0; i<this.responsablesRff.length; i++){
            if(this.responsableSearch.responsable.entityId == this.responsablesRff[i].id){
              if(this.responsableSearchResult[i].turnoverAmountWithTax!=undefined)
              this.responsablesRff[i].turnoverAmountWithTax = this.responsableSearchResult[i].turnoverAmountWithTax;
              if(this.responsableSearchResult[i].announcementNbr!=undefined)
              this.responsablesRff[i].announcementNbr = this.responsableSearchResult[i].announcementNbr;
              if(this.responsableSearchResult[i].formalityNbr!=undefined)
              this.responsablesRff[i].formalityNbr = this.responsableSearchResult[i].formalityNbr;
            }
          }
          }
      });
    }
    if(this.responsableSearchResult && this.responsableSearchResult.length>0){
      if(this.responsableSearchResult[0].turnoverAmountWithTax)
      this.tiersRff[0].turnoverAmountWithTax = this.responsableSearchResult[0].turnoverAmountWithTax;
      if(this.responsableSearchResult[0].announcementNbr)
      this.tiersRff[0].announcementNbr = this.responsableSearchResult[0].announcementNbr;
      if(this.responsableSearchResult[0].formalityNbr)
      this.tiersRff[0].formalityNbr = this.responsableSearchResult[0].formalityNbr;
    }
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


