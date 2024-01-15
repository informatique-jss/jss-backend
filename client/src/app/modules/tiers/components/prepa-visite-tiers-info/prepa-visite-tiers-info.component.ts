import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Tiers } from '../../model/Tiers';
import { UntypedFormBuilder } from '@angular/forms';
import { RffSearch } from '../../model/RffSearch';
import { RffService } from '../../services/rff.service';
import { Rff } from '../../model/Rff';

@Component({
  selector: 'prepa-visite-tiers-info',
  templateUrl: './prepa-visite-tiers-info.component.html',
  styleUrls: ['./prepa-visite-tiers-info.component.css']
})

export class PrepaVisiteTiersComponent implements OnInit, AfterContentChecked {

  @Input() tiers: Tiers = {} as Tiers;
  @Input() rffSearch: RffSearch = {} as RffSearch;
  @Input() editMode: boolean = false;

  rff: Rff[] | undefined;
  availableColumnsTiers: SortTableColumn<Tiers>[] = [];
  displayedColumnsTiers: SortTableColumn<Tiers>[] = [];
  tableAction: SortTableAction<Tiers>[] = [];
  tableActionTiers: SortTableAction<Tiers>[] = [];
  tiersToDisplay: Tiers[] | undefined;
  tableActions: SortTableAction<Tiers>[] = [] as Array<SortTableAction<Tiers>>;

  constructor(private rffService: RffService,
    private changeDetectorRef: ChangeDetectorRef,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {

    this.rffService.getRffs(this.rffSearch).subscribe(response => {
      this.rff = response;
    })

    this.tiersToDisplay = [this.tiers];

    this.displayedColumnsTiers = [];
    this.displayedColumnsTiers.push({ id: "denomination", fieldName: "denomination", label: "Dénomination"  } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "address", fieldName: "address", label: "address" } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "mails[0].mail", fieldName: "mails[0].mail", label: "mail" } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "phones[0].phoneNumber", fieldName: "phones[0].phoneNumber", label: "phone" } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "rffInsertionRate", fieldName: "rffInsertionRate", label: "rff Insertion Rate" } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "rffFormaliteRate", fieldName: "rffFormaliteRate", label: "rff Formalite Rate" } as SortTableColumn<Tiers>);
  }
}


