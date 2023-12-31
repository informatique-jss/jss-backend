import { Point } from '@angular/cdk/drag-drop';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SortTableColumn } from '../modules/miscellaneous/model/SortTableColumn';

@Injectable({
  providedIn: 'root'
})
export class UserPreferenceService {

  constructor(http: HttpClient
  ) {
  }

  // Total draggable table position
  setUserTotalDivPosition(point: Point) {
    localStorage.setItem('total-table-position', JSON.stringify(point));
  }

  getUserTotalDivPosition(): Point {
    if (localStorage.getItem('total-table-position') != null) {
      let a = localStorage.getItem('total-table-position');
      let point = JSON.parse(a!) as Point;
      if (point)
        return point;
    }
    return { x: 0, y: 0 };
  }

  // User note draggable table position
  setUserNoteTablePosition(point: Point) {
    localStorage.setItem('user-note-table-position', JSON.stringify(point));
  }

  getUserNoteTablePosition(): Point {
    if (localStorage.getItem('user-note-table-position') != null) {
      let a = localStorage.getItem('user-note-table-position');
      let point = JSON.parse(a!) as Point;
      if (point)
        return point;
    }
    return { x: 0, y: 0 };
  }

  // User column display
  setUserDisplayColumnsForTable<T>(columns: SortTableColumn<T>[], tableName: string) {
    if (columns && tableName)
      localStorage.setItem('table-columns' + tableName, JSON.stringify(columns));
  }

  getUserDisplayColumnsForTable<T>(tableName: string): SortTableColumn<T>[] {
    if (tableName) {
      let value = localStorage.getItem('table-columns' + tableName);
      if (value) {
        let list = JSON.parse(value!) as SortTableColumn<T>[];
        if (list)
          return list;
      }
    }
    return [];
  }

  // User search
  setUserSearchBookmark(search: any, searchName: string) {
    if (search && searchName)
      localStorage.setItem('search-' + searchName, JSON.stringify(search));
  }

  getUserSearchBookmark(searchName: string): any {
    if (searchName) {
      let value = localStorage.getItem('search-' + searchName);
      if (value) {
        let list = JSON.parse(value!);
        if (list)
          return list;
      }
    }
    return undefined;
  }

  // User tab selection index display
  setUserTabsSelectionIndex(tabsName: string, index: number) {
    if (tabsName)
      localStorage.setItem('tabs-index' + tabsName, index + "");
  }

  getUserTabsSelectionIndex(tabsName: string): number {
    if (tabsName) {
      let value = localStorage.getItem('tabs-index' + tabsName);
      if (value) {
        return parseInt(value);
      }
    }
    return 0;
  }

  // User provision selection display
  setUserExpensionPanelSelectionId(expensionPanelName: string, id: number) {
    if (expensionPanelName)
      localStorage.setItem('expension-panel' + expensionPanelName, id + "");
  }

  getUserExpensionPanelSelectionId(expensionPanelName: string): number {
    if (expensionPanelName) {
      let value = localStorage.getItem('expension-panel' + expensionPanelName);
      if (value) {
        return parseInt(value);
      }
    }
    return 0;
  }

  // Dark mode
  setDarkMode(darkMode: boolean) {
    if (darkMode != undefined && darkMode != null)
      localStorage.setItem("darkMode", JSON.stringify(darkMode));
  }

  getDarkMode(): boolean {
    let value = localStorage.getItem('darkMode');
    if (value) {
      let darkMode = JSON.parse(value!);
      if (darkMode)
        return darkMode;
    }
    return false;
  }
}



