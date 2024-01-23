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

@Component({
  selector: 'prepa-visite-tiers-responsable-info',
  templateUrl: './prepa-visite-tiers-responsable-info.component.html',
  styleUrls: ['./prepa-visite-tiers-responsable-info.component.css']
})

export class PrepaVisiteTiersResponsableInfoComponent implements OnInit, AfterContentChecked {

  @Input() tiers: Tiers = {} as Tiers;
  @Input() rffSearch: RffSearch = {} as RffSearch;
  @Input() editMode: boolean = false;

  rff: Rff[] | undefined;
  tiersRff: TiersRff[] | undefined;
  responsablesRff: ResponsablesRff[] | undefined;
  tiersList: Tiers[] = [];
  responsableList: Responsable[] = [];

  responsableSearch: TiersSearch = {} as TiersSearch;
  responsableSearchResult: ResponsableSearchResult[] = [];

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
    this.responsableList = this.tiers.responsables;
    this.searchRff();
  }

  showTable(){
    this.displayedColumnsTiersRff = [];
    this.displayedColumnsResponsablesRff = [];

    this.displayedColumnsTiersRff.push({ id: "tiersDenomination", fieldName: "tiersDenomination", label: "Dénomination"  } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "tiersAddress", fieldName: "tiersAddress", label: "address" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "tiersMail", fieldName: "tiersMail", label: "mail" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "tiersPhone", fieldName: "tiersPhone", label: "phone" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "tiersRffInsertion", fieldName: "tiersRffInsertion", label: "RFF AL", valueFonction: formatEurosForSortTable } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "tiersRffFormalite", fieldName: "tiersRffFormalite", label: "RFF Formalités", valueFonction: formatEurosForSortTable } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "tiersRffTotal", fieldName: "tiersRffTotal", label: "Total HT", valueFonction: formatEurosForSortTable } as SortTableColumn<TiersRff>);

    this.displayedColumnsResponsablesRff.push({ id: "id", fieldName: "id", label: "N° du responsable" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "responsableLastName", fieldName: "responsableLastName", label: "Nom" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "responsablePhone", fieldName: "responsablePhone", label: "Tel" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "function", fieldName: "function", label: "Function" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "responsableMail", fieldName: "responsableMail", label: "Mails"} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "responsableTurnoverAmountWithTax", fieldName: "responsableTurnoverAmountWithTax", label: "Chiffre d'affaire", valueFonction: formatEurosForSortTable} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "responsableAnnouncementNbr", fieldName: "responsableAnnouncementNbr", label: "Nbre AL", valueFonction: formatEurosForSortTable} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "responsableFormalityNbr", fieldName: "responsableFormalityNbr", label: "Nbre Form", valueFonction: formatEurosForSortTable} as SortTableColumn<ResponsablesRff>);

  }

  searchRff() {
    this.rffSearch = {} as RffSearch;
    this.rffSearch.tiers = { entityId: this.tiers.id } as IndexEntity;
    this.rffSearch.isHideCancelledRff = false;

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

    this.setTiersAndResponsableData();

    this.rffService.getRffs(this.rffSearch).subscribe(response => {
      this.rff = response;
      this.setTiersRffData();
      this.showTable();
    });


  }

  setTiersAndResponsableData(){
    this.tiersRff = [{} as TiersRff];
    this.responsablesRff = [{} as ResponsablesRff];

    if(this.tiersList[0].denomination)
    this.tiersRff[0].tiersDenomination = this.tiersList[0].denomination;
    if(this.tiersList[0].address.length>0)
    this.tiersRff[0].tiersAddress = this.tiersList[0].address;
    if(this.tiersList[0].mails.length>0)
    this.tiersRff[0].tiersMail = this.tiersList[0].mails[0].mail;
    if(this.tiersList[0].phones.length>0)
    this.tiersRff[0].tiersPhone = this.tiersList[0].phones[0].phoneNumber;

    for(let i = 0; i < this.responsableList.length; i++){

      let responsablesRffItem: ResponsablesRff = {} as ResponsablesRff;

      if(this.responsableList[i].id && this.responsableList[i].id!=undefined)
        this.responsablesRff[i].id = this.responsableList[i].id;
      if(this.responsableList[i].lastname && this.responsableList[i].lastname!=undefined)
        this.responsablesRff[i].responsableLastName = this.responsableList[i].lastname;
      if(this.responsableList[i].firstname && this.responsableList[i].firstname!=undefined)
        this.responsablesRff[i].responsableFirstName = this.responsableList[i].firstname;
      if(this.responsableList[i].function && this.responsableList[i].function!=undefined)
        this.responsablesRff[i].function = this.responsableList[i].function;
      if (this.responsableList[i].mails && this.responsableList[i].mails.length > 0)
        this.responsablesRff[i].responsableMail = this.responsableList[i].mails[0].mail;
        if (this.responsableList[i].phones && this.responsableList[i].phones.length > 0)
          this.responsablesRff[i].responsablePhone = this.responsableList[i].phones[0].phoneNumber;

      this.responsablesRff.push(responsablesRffItem);
     }
     this.setResponsableSearchResultData(this.responsablesRff);
  }

  setTiersRffData() {
    if (this.tiersRff && this.tiersRff.length > 0 && this.tiersList && this.tiersList.length > 0 && this.rff) {
      this.tiersRff[0].tiersRffInsertion = this.rff[0]?.rffInsertion;
      this.tiersRff[0].tiersRffFormalite = this.rff[0]?.rffFormalite;
      this.tiersRff[0].tiersRffTotal = this.rff[0]?.rffTotal
    }
  }

  //a reviser
  setResponsableSearchResultData(responsablesRff: ResponsablesRff[]) {
    this.responsableSearch = {} as TiersSearch

    for(let i=0; i<this.responsableList.length; i++){

      this.responsableSearch.responsable = { entityId: this.tiers.responsables[i].id } as IndexEntity;

      this.responsableSearchResultService.getResponsableSearch(this.responsableSearch).subscribe(response => {

      this.responsableSearchResult = response;
      if(responsablesRff[i] && this.responsableSearchResult[i].turnoverAmountWithTax!=undefined)
        responsablesRff[i].responsableTurnoverAmountWithTax = this.responsableSearchResult[i].turnoverAmountWithTax;
      if(responsablesRff[i] && this.responsableSearchResult[i].announcementNbr!=undefined)
        responsablesRff[i].responsableAnnouncementNbr = this.responsableSearchResult[i].announcementNbr;
      if(responsablesRff[i] && this.responsableSearchResult[i].announcementNbr!=undefined)
        responsablesRff[i].responsableFormalityNbr = this.responsableSearchResult[i].formalityNbr;

      })

   }
  }

}


