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
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { forkJoin } from 'rxjs';
import { TiersSearchResult } from '../../model/TiersSearchResult';

@Component({
  selector: 'visit-prepa-tiers-responsible-info',
  templateUrl: './visit-prepa-tiers-responsible-info.component.html',
  styleUrls: ['./visit-prepa-tiers-responsible-info.component.css']
})

export class VisitPrepaTiersResponsibleInfoComponent implements OnInit, AfterContentChecked {

  @Input() tiers: Tiers = {} as Tiers;
  rffSearch: RffSearch = {} as RffSearch;

  rff: Rff[] | undefined;
  rffSearchResult:  Rff[] | undefined;
  rffSearchResultList:  Rff[] | undefined;

  responsableSearchList:  TiersSearch[] | undefined;
  tiersRffResponse: boolean = true;
  noResponsable: Responsable = {} as Responsable;

  tiersRff: TiersRff[] | undefined;
  responsablesRff: ResponsablesRff[] | undefined;

  tiersList: Tiers[] | undefined;
  rffSearchList: RffSearch[] | undefined;
  responsablesList: Responsable[] | undefined;;

  tiersSearch: TiersSearch = {} as TiersSearch;
  tiersSearchResult: TiersSearchResult[] = [];
  responsableSearch: TiersSearch = {} as TiersSearch;
  responsableSearchResult: ResponsableSearchResult[] | undefined;
  responsableSearchResultList: ResponsableSearchResult[] | undefined;
  onlyResponsable: Responsable | undefined;

  orderingSearch: OrderingSearch = {} as OrderingSearch;
  quotationSearch: QuotationSearch = {} as QuotationSearch;
  provisionSearch: AffaireSearch = {} as AffaireSearch;
  invoiceSearch: InvoiceSearch = {} as InvoiceSearch;
  responsableAccountSearch: Tiers | undefined;

  tableActions: SortTableAction<Responsable>[] = [] as Array<SortTableAction<Responsable>>;

  displayedColumnsTiersRff: SortTableColumn<TiersRff>[] = [];
  displayedColumnsResponsablesRff: SortTableColumn<ResponsablesRff>[] = [];

  constructor(
    private rffService: RffService,
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
    this.rffSearchResult =  [{} as Rff];
    this.rffSearchResultList =  [{} as Rff];
    this.tiersSearchResult= [{} as TiersSearchResult];
    this.responsableSearchList = [{} as TiersSearch];
    this.responsableSearchResult = [{} as ResponsableSearchResult];
    this.responsableSearchResultList = [{} as ResponsableSearchResult];
    this.responsablesRff = [{} as ResponsablesRff];

    this.displayedColumnsTiersRff.push({ id: "denomination", fieldName: "denomination", label: "Dénomination"  } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "address", fieldName: "address", label: "address" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "mails", fieldName: "mails", label: "Mails", valueFonction: (element: TiersRff, column: SortTableColumn<TiersRff>) => { return ((element.mails) ? element.mails.map((e: { mail: any; }) => e.mail).join(", ") : "") } } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "phones", fieldName: "phones", label: "Téléphones", valueFonction: (element: TiersRff, column: SortTableColumn<TiersRff>) => { return ((element.phones) ? element.phones.map((e: { phoneNumber: any; }) => e.phoneNumber).join(", ") : "") } } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "turnoverAmountWithTax", fieldName: "turnoverAmountWithTax", label: "Chiffre d'affaire" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "announcementNbr", fieldName: "announcementNbr", label: "Nbr AL" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "formalityNbr", fieldName: "formalityNbr", label: "Nbr Formalités"} as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "rffInsertion", fieldName: "rffInsertion", label: "RFF AL", valueFonction: formatEurosForSortTable } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "rffFormalite", fieldName: "rffFormalite", label: "RFF Formalités", valueFonction: formatEurosForSortTable } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "rffTotal", fieldName: "rffTotal", label: "RFF Total HT", valueFonction: formatEurosForSortTable } as SortTableColumn<TiersRff>);

    this.displayedColumnsResponsablesRff.push({ id: "id", fieldName: "id", label: "N° du responsable" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "name", fieldName: "name", label: "Nom" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "phones", fieldName: "phones", label: "Téléphones", valueFonction: (element: ResponsablesRff, column: SortTableColumn<ResponsablesRff>) => { return ((element.phones) ? element.phones.map((e: { phoneNumber: any; }) => e.phoneNumber).join(", ") : "") } } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "function", fieldName: "function", label: "Function" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "mails", fieldName: "mails", label: "Mails", valueFonction: (element: ResponsablesRff, column: SortTableColumn<ResponsablesRff>) => { return ((element.mails) ? element.mails.map((e: { mail: any; }) => e.mail).join(", ") : "") } } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "turnoverAmountWithTax", fieldName: "turnoverAmountWithTax", label: "Chiffre d'affaire", valueFonction: formatEurosForSortTable} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "announcementNbr", fieldName: "announcementNbr", label: "Nbre AL"} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "formalityNbr", fieldName: "formalityNbr", label: "Nbre Form"} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "rffInsertion", fieldName: "rffInsertion", label: "RFF AL" , valueFonction: formatEurosForSortTable} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "rffFormalite", fieldName: "rffFormalite", label: "RFF Formalités", valueFonction: formatEurosForSortTable } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "rffTotal", fieldName: "rffTotal", label: "RFF Total HT", valueFonction: formatEurosForSortTable } as SortTableColumn<ResponsablesRff>);
    this.searchTiersSearchResult();
    this.searchResponsablesSearchResult();
    this.searchRff();
  }

  searchRff() {
    if(this.tiers.responsables.length>1){
      for(let i = 0; i<this.tiers.responsables.length; i++){
        this.setRffSearch(this.tiers.responsables[i]);
      }

    }else{
        this.setRffSearch(this.noResponsable);
    }

    if (this.rffSearchList !== undefined && this.rffSearchList.length > 1) {
      const observables = this.rffSearchList.slice(1).map(item => this.rffService.getRffs(item));
      forkJoin(observables).subscribe(
        responses => {
          for (const response of responses) {
            if (Array.isArray(response) && response.length > 0){
            this.rffSearchResult = response;
            this.rffSearchResultList?.push(...this.rffSearchResult);
            }
          }
          this.setTiersAndResponsableData(this.rffSearchResultList ?? []);
        }
      );
    }
  this.setTiersAndResponsableData(this.rffSearchResultList?? []);
  }


  setRffSearch(responsable: Responsable){
    this.rffSearch.isHideCancelledRff = false;
    this.rffSearch.responsable = { entityId: responsable.id } as IndexEntity;

    this.rffSearch.tiers = { entityId: this.tiers.id } as IndexEntity;

    let currentDate = new Date();
    let start = new Date();

    if (currentDate.getMonth() > 10 || (currentDate.getMonth() === 10 && currentDate.getDate() > 30)) {
      start.setFullYear(currentDate.getFullYear() - 1);
    } else {
      start.setFullYear(currentDate.getFullYear() - 2);
    }

    start.setMonth(11);
    start.setDate(1);
    this.rffSearch.startDate = start;

    let end = new Date();
    end.setFullYear(end.getFullYear() - 1);
    end.setMonth(10);
    end.setDate(30);
    this.rffSearch.endDate = end;

    this.rffSearchList?.push({ ...this.rffSearch });
}

  setTiersAndResponsableData(rffSearchResultList: Rff[] ){

    this.tiersRff = [{} as TiersRff];
    this.responsablesRff = [{} as ResponsablesRff];
    this.responsableSearch = {} as TiersSearch
    this.responsableSearch.tiers = { entityId: this.tiers.id } as IndexEntity;

    this.tiersRff[0].denomination = (!this.tiers.isIndividual && this.tiers.denomination !== null)
    ? this.tiers.denomination
    : this.tiers.firstname + " " + this.tiers.lastname;

    this.tiersRff[0].address = (this.tiers.address !== null)
    ? this.tiers.address + " - " + this.tiers.postalCode + " " + this.tiers.city.label + " " + this.tiers.country.label
    : "";

    this.tiersRff[0].mails = (this.tiers.mails !== null) ? this.tiers.mails : [];

    this.tiersRff[0].phones = (this.tiers.phones !== null) ? this.tiers.phones : [];

    if(rffSearchResultList && rffSearchResultList!= undefined && rffSearchResultList.length>1){
      let rffInsertionTiers : number = 0;
      let rffIFormaliteTiers : number = 0;
      let rffTotalTiers : number = 0;

      for(let i = 1; i<rffSearchResultList.length; i++){
        rffInsertionTiers += rffSearchResultList[i]?.rffInsertion;
        rffIFormaliteTiers += rffSearchResultList[i]?.rffFormalite;
        rffTotalTiers += rffSearchResultList[i]?.rffTotal;
      }
      rffInsertionTiers = Number(rffInsertionTiers.toFixed(2));
      rffIFormaliteTiers = Number(rffIFormaliteTiers.toFixed(2));
      rffTotalTiers = Number(rffTotalTiers.toFixed(2));

      this.tiersRff[0].rffInsertion = rffInsertionTiers;
      this.tiersRff[0].rffFormalite = rffIFormaliteTiers;
      this.tiersRff[0].rffTotal = rffTotalTiers;
    }

    if(this.tiersRff !== undefined && (this.tiersSearchResult[0].turnoverAmountWithTax || this.tiersSearchResult[0].announcementNbr || this.tiersSearchResult[0].formalityNbr)!=undefined)
       {
      this.tiersRff[0].turnoverAmountWithTax = this.tiersSearchResult[0].turnoverAmountWithTax;
      this.tiersRff[0].announcementNbr = this.tiersSearchResult[0].announcementNbr;
      this.tiersRff[0].formalityNbr = this.tiersSearchResult[0].formalityNbr;
    }

    if(this.responsableSearchResultList && this.responsableSearchResultList!= undefined && this.responsableSearchResultList.length>1){
    for(let i = 1; i<this.responsableSearchResultList.length; i++) {
      for(let j = 0; j<this.tiers.responsables.length; j++){
        if(this.tiers.responsables[j].id == this.responsableSearchResultList[i].responsableId){
          if(this.responsableSearchResultList[i].responsableId!=undefined)
          this.responsablesRff[i-1].id = this.responsableSearchResultList[i].responsableId;
          if(this.responsableSearchResultList[i].responsableLabel!=undefined)
          this.responsablesRff[i-1].name = this.responsableSearchResultList[i].responsableLabel;
          if(this.tiers.responsables[j].phones!=undefined)
          this.responsablesRff[i-1].phones = this.tiers.responsables[j].phones;
          if(this.tiers.responsables[j].mails!=undefined)
          this.responsablesRff[i-1].mails = this.tiers.responsables[j].mails;
          if(this.tiers.responsables[j].function!=undefined)
          this.responsablesRff[i-1].function = this.tiers.responsables[j].function;
          if(this.responsableSearchResultList[i].turnoverAmountWithTax!=undefined)
          this.responsablesRff[i-1].turnoverAmountWithTax = this.responsableSearchResultList[i].turnoverAmountWithTax;
          if(this.responsableSearchResultList[i].formalityNbr!=undefined)
          this.responsablesRff[i-1].formalityNbr = this.responsableSearchResultList[i].formalityNbr;
          if(this.responsableSearchResultList[i].announcementNbr!=undefined)
          this.responsablesRff[i-1].announcementNbr = this.responsableSearchResultList[i].announcementNbr;
          this.responsablesRff.push({...this.responsablesRff[i]});
        }
      }
    }

    if(this.rffSearchResultList)
      for(let i = 1; i<this.rffSearchResultList.length; i++){
        for(let j = 0; j<this.responsablesRff.length; j++){
          if(this.responsablesRff[j].id == this.rffSearchResultList[i].responsableId){
            this.responsablesRff[j].rffInsertion = this.rffSearchResultList[i].rffInsertion;
              this.responsablesRff[j].rffFormalite = this.rffSearchResultList[i].rffFormalite;
              this.responsablesRff[j].rffTotal = this.rffSearchResultList[i].rffTotal;
          }
        }
      }
  }}


  searchTiersSearchResult() {
    this.tiersSearch.tiers = { entityId: this.tiers.id } as IndexEntity;

    let currentDate = new Date();
    let start = new Date();

      if (currentDate.getMonth() > 10 || (currentDate.getMonth() === 10 && currentDate.getDate() > 30)) {
        start.setFullYear(currentDate.getFullYear() - 1);
      } else {
        start.setFullYear(currentDate.getFullYear() - 2);
      }
      start.setMonth(11);
      start.setDate(1);
      this.tiersSearch.startDate = start;

      let end = new Date();
      end.setFullYear(end.getFullYear() - 1);
      end.setMonth(10);
      end.setDate(30);
      this.tiersSearch.endDate = end;

      this.tiersSearchResultService.getTiersSearch(this.tiersSearch).subscribe(
        (resp) => {
          this.tiersSearchResult = resp;
        },
      );
  }

  searchResponsablesSearchResult() {

    for(let i = 0; i<this.tiers.responsables.length; i++){
    this.responsableSearch.responsable = { entityId: this.tiers.responsables[i].id } as IndexEntity;
    let currentDate = new Date();
    let start = new Date();

    if (currentDate.getMonth() > 10 || (currentDate.getMonth() === 10 && currentDate.getDate() > 30)) {
      start.setFullYear(currentDate.getFullYear() - 1);
    } else {
      start.setFullYear(currentDate.getFullYear() - 2);
    }
    start.setMonth(11);
    start.setDate(1);
    this.responsableSearch.startDate = start;

    let end = new Date();
    end.setFullYear(end.getFullYear() - 1);
    end.setMonth(10);
    end.setDate(30);
    this.responsableSearch.endDate = end;

    this.responsableSearchList?.push({...this.responsableSearch});
  }

  if (this.responsableSearchList) {
    this.responsableSearchList.slice(1).forEach(responsable => {
      this.responsableSearchResultService.getResponsableSearch(responsable).subscribe(
        (resp) => {
          this.responsableSearchResult = resp;
          this.responsableSearchResultList?.push(...this.responsableSearchResult)
        },
      );
    });
  }
  }


}


