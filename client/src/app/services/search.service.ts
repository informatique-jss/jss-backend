import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { EntityType } from '../routing/search/EntityType';
import { SearchComponent } from '../routing/search/search.component';

@Injectable({
  providedIn: 'root'
})

export class SearchService {
  constructor(public searchDialog: MatDialog) { }

  searchDialogRef: MatDialogRef<SearchComponent> | undefined;

  openSearch() {
    this.openSearchOnModule(null);
  }

  openSearchOnModule(module: EntityType | null) {
    if (this.searchDialogRef == undefined || this.searchDialogRef?.getState() != 0) {
      this.searchDialogRef = this.searchDialog.open(SearchComponent, {
        width: '100%',
        height: '90%'
      });
      if (module != null)
        this.searchDialogRef.componentInstance.userSelectedModule = module;
    }
  }
}



