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
  displayedColumnsTiersRff: SortTableColumn<TiersRff>[] = [];
  displayedColumnsResponsables: SortTableColumn<Responsable>[] = [];

  tiersList: Tiers[] = [];

  constructor(private rffService: RffService,
    private changeDetectorRef: ChangeDetectorRef,
    protected tiersService: TiersService,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }


  ngOnInit() {
    this.tiersList = [this.tiers];
    this.searchRff();
  }

  showTable(){
    this.displayedColumnsTiersRff = [];
    // this.displayedColumnsResponsables = [];
    // this.setDataTable();

    this.displayedColumnsTiersRff.push({ id: "tiersDenomination", fieldName: "tiersDenomination", label: "Dénomination"  } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "tiersAddress", fieldName: "tiersAddress", label: "address" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "tiersMail", fieldName: "tiersMail", label: "mail" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "tiersPhone", fieldName: "tiersPhone", label: "phone" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "tiersRffInsertion", fieldName: "tiersRffInsertion", label: "RFF AL", valueFonction: formatEurosForSortTable } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "tiersRffFormalite", fieldName: "tiersRffFormalite", label: "RFF Formalités", valueFonction: formatEurosForSortTable } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "tiersRffTotal", fieldName: "tiersRffTotal", label: "Total HT", valueFonction: formatEurosForSortTable } as SortTableColumn<TiersRff>);

    // this.displayedColumnsResponsables.push({ id: "id", fieldName: "id", label: "N° du responsable" } as SortTableColumn<Responsable>);
    // this.displayedColumnsResponsables.push({ id: "lastname", fieldName: "lastname", label: "Nom" } as SortTableColumn<Responsable>);
    // this.displayedColumnsResponsables.push({ id: "firstname", fieldName: "firstname", label: "Prénom" } as SortTableColumn<Responsable>);
    // this.displayedColumnsResponsables.push({ id: "function", fieldName: "function", label: "Fonction" } as SortTableColumn<Responsable>);
    // this.displayedColumnsResponsables.push({ id: "mails", fieldName: "mails", label: "Mails", valueFonction: (element: Responsable, column: SortTableColumn<Responsable>) => { return ((element.mails) ? element.mails.map((e: { mail: any; }) => e.mail).join(", ") : "") } } as SortTableColumn<Responsable>);
    // this.displayedColumnsResponsables.push({ id: "phones", fieldName: "phones", label: "Téléphones", valueFonction: (element: Responsable, column: SortTableColumn<Responsable>) => { return ((element.phones) ? element.phones.map((e: { phoneNumber: any; }) => e.phoneNumber).join(", ") : "") } } as SortTableColumn<Responsable>);

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
    this.tiersRff = [{} as TiersRff];

    if(this.tiersList[0].denomination)
    this.tiersRff[0].tiersDenomination = this.tiersList[0].denomination;
    if(this.tiersList[0].address.length>0)
    this.tiersRff[0].tiersAddress = this.tiersList[0].address;
    if(this.tiersList[0].mails.length>0)
    this.tiersRff[0].tiersMail = this.tiersList[0].mails[0].mail;
    if(this.tiersList[0].phones.length>0)
    this.tiersRff[0].tiersPhone = this.tiersList[0].phones[0].phoneNumber;

    this.rffService.getRffs(this.rffSearch).subscribe(response => {
      this.rff = response;
      this.setTiersRffData();
      this.showTable();
    });


  }

  setTiersRffData() {
    if (this.tiersRff && this.tiersRff.length > 0 && this.tiersList && this.tiersList.length > 0 && this.rff) {
      this.tiersRff[0].tiersRffInsertion = this.rff[0]?.rffInsertion;
      this.tiersRff[0].tiersRffFormalite = this.rff[0]?.rffFormalite;
      this.tiersRff[0].tiersRffTotal = this.rff[0]?.rffTotal
    }
  }

  // setDataTable() {
  //   this.tiers.responsables.sort(function (a: Responsable, b: Responsable) {
  //     return (a.lastname + "" + a.firstname).localeCompare(b.lastname + "" + a.firstname);
  //   });
  // }

}


