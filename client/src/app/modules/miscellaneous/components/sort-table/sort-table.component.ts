import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { SortTableColumn } from '../../model/SortTableColumn';

@Component({
  selector: 'sort-table',
  templateUrl: './sort-table.component.html',
  styleUrls: ['./sort-table.component.css']
})
export class SortTableComponent implements OnInit {

  @Input() columns: SortTableColumn[] | undefined;
  @Input() values: any[] | undefined;
  @Input() filterText: string | undefined;
  @Input() tableName: string = "table";
  /**
 * Fired when row is clicked is modified by user
 */
  @Output() onRowClick: EventEmitter<any> = new EventEmitter();

  dataSource = new MatTableDataSource<any>();
  @ViewChild(MatSort) sort!: MatSort;

  constructor(protected userPreferenceService: UserPreferenceService) { }

  ngOnInit() {
    if (this.values)
      this.dataSource.data = this.values;

    setTimeout(() => {
      this.dataSource.sort = this.sort;
      this.dataSource.sortingDataAccessor = (item: any, property) => {
        if (this.columns) {
          for (let column of this.columns) {
            if (column.id && column.id == property) {
              if (column.sortFonction)
                return column.sortFonction(item, this.values, column, this.columns);
              return this.getColumnValue(column, item);
            }
          }
        }
      };

      this.dataSource.filterPredicate = (data: any, filter) => {
        const dataStr = JSON.stringify(data).toLowerCase();
        return dataStr.indexOf(filter) != -1;
      }
    });

    // Restore displayed columns
    let prefColumns = this.userPreferenceService.getUserDisplayColumnsForTable(this.tableName);
    if (prefColumns && this.columns)
      for (let prefColumn of prefColumns)
        for (let column of this.columns) {
          if (column.display == undefined)
            column.display = true;
          if (column.id == prefColumn.id)
            column.display = prefColumn.display;
        }
  }

  applyFilter() {
    if (this.filterText != undefined && this.filterText != null) {
      let filterValue = this.filterText.trim();
      filterValue = filterValue.toLowerCase();
      this.dataSource.filter = filterValue;
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    console.log(changes);
    if (changes.values != undefined && this.values)
      this.dataSource.data = this.values;
    if (changes.filterText != undefined && this.values) {
      this.applyFilter();
    }
  }

  getColumnLabel(column: SortTableColumn): string {
    if (column && column.label)
      return column.label;
    return "Not found";
  }

  getColumnValue(column: SortTableColumn, element: any): string {
    if (column) {
      if (column.valueFonction) {
        return column.valueFonction(element, this.values, column, this.columns);
      }
      if (column.fieldName) {
        if (column.fieldName.indexOf(".") >= 0)
          try {
            return this.getObjectPropertybyString(element, column.fieldName);
          } catch {
          }
        return element[column.fieldName];
      }
    }
    return "Not found";
  }

  getDisplayedColumns() {
    let columnList = [];
    if (this.columns)
      for (let column of this.columns) {
        if (column.display == undefined)
          column.display = true;
        if (column.display)
          columnList.push(column.id);
      }
    return columnList;
  }

  selectColumn(column: SortTableColumn, $event: any) {
    $event.stopPropagation();
    $event.preventDefault();

    column.display = !column.display;
    this.saveColumnsDisplay();
  }

  selectColumnAll($event: any) {
    $event.stopPropagation();
    $event.preventDefault();

    if (this.columns)
      for (let column of this.columns)
        column.display = true;

    this.saveColumnsDisplay();
  }

  unselectColumnAll($event: any) {
    $event.stopPropagation();
    $event.preventDefault();

    if (this.columns)
      for (let column of this.columns)
        column.display = false;
  }

  saveColumnsDisplay() {
    if (this.columns && this.tableName)
      this.userPreferenceService.setUserDisplayColumnsForTable(this.columns, this.tableName);
  }

  rowClicked(element: any) {
    this.onRowClick.emit(element);
  }

  getObjectPropertybyString(element: any, propertyPath: string) {
    propertyPath = propertyPath.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
    propertyPath = propertyPath.replace(/^\./, '');           // strip a leading dot
    var a = propertyPath.split('.');
    for (var i = 0, n = a.length; i < n; ++i) {
      var k = a[i];
      if (k in element) {
        element = element[k];
      } else {
        return;
      }
    }
    return element;
  }
}
