import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { NgbPagination, NgbPaginationNext, NgbPaginationPrevious } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from "@ng-icons/core";
import { LucideAngularModule, LucideSearch, LucideShield, LucideUserCheck } from 'lucide-angular';
import { Observable } from 'rxjs';
import { NgbdSortableHeader } from '../../../../libs/inspinia/directive/sortable.directive';
import { TableService } from '../../../../libs/inspinia/services/table.service';
import { toTitleCase } from '../../../../libs/inspinia/utils/string-utils';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { Tiers } from '../../../profile/model/Tiers';
import { TiersService } from '../../services/tiers.service';

@Component({
  selector: 'tiers-selection',
  templateUrl: './tiers-selection.component.html',
  standalone: true,
  providers: [TableService],
  imports: [
    SHARED_IMPORTS,
    LucideAngularModule,
    NgIcon,
    RouterLink,
    NgbdSortableHeader,
    FormsModule,
    NgbPagination,
    NgbPaginationNext,
    NgbPaginationPrevious,
  ]
})
export class TiersSelectionComponent implements OnInit {


  filterStatus = 'All'
  filterEmail = 'All'
  selectAll = false;

  tiers: Tiers[] = [];
  protected readonly toTitleCase = toTitleCase;

  fetchingItemsSize = 50;

  denominationSearchText: string = "";

  protected readonly LucideSearch = LucideSearch;
  protected readonly LucideShield = LucideShield;
  protected readonly LucideUserCheck = LucideUserCheck;

  tiers$: Observable<Tiers[]> = new Observable();
  total$: Observable<number> = new Observable<0>;

  constructor(
    public tableService: TableService<Tiers>,
    private tiersService: TiersService,
    private appService: AppService,
  ) { }

  ngOnInit(): void {
    this.tiersService.getTiers(this.denominationSearchText, 0, this.fetchingItemsSize).subscribe(pagedTiers => {
      if (pagedTiers.content) {
        this.tableService.setItems(pagedTiers.content, 10);
        this.tiers$ = this.tableService.items$
        this.total$ = this.tableService.total$
      }
    });
  }

  toggleAllSelection() {
    this.tableService.setAllSelection(this.selectAll);
  }

  toggleSingleSelection() {
    this.tableService.items$.subscribe(items => {
      this.selectAll = items.every((item: any) => item.selected);
    }).unsubscribe();
  }

  deleteSelected() {
    this.tableService.deleteSelectedItems();
    this.selectAll = false;
  }

  get hasSelection(): boolean {
    return this.tableService.hasSelectedItems();
  }

  openTiers(tiers: Tiers) {
    this.appService.openRoute(undefined, "/tiers/" + tiers.id, undefined);
  }
}
