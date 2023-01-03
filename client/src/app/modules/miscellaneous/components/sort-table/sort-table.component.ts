import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Observable, Subscription } from 'rxjs';
import { getObjectPropertybyString } from 'src/app/libs/FormatHelper';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { AppService } from '../../../../services/app.service';
import { Employee } from '../../../profile/model/Employee';
import { SortTableAction } from '../../model/SortTableAction';
import { SortTableColumn } from '../../model/SortTableColumn';

@Component({
  selector: 'sort-table',
  templateUrl: './sort-table.component.html',
  styleUrls: ['./sort-table.component.css']
})
export class SortTableComponent implements OnInit {

  @Input() columns: SortTableColumn[] | undefined;
  @Input() values: any[] | undefined;
  @Input() actions: SortTableAction[] | undefined;
  @Input() filterText: string | undefined;
  @Input() tableName: string = "table";
  @Input() idRowSelected: number | undefined;
  @Input() filterPredicate: any;
  /**
 * Fired when row is clicked is modified by user
 */
  @Output() onRowClick: EventEmitter<any> = new EventEmitter();

  @Input() refreshTable: Observable<void> | undefined;
  private refreshTableSubscription: Subscription | undefined;

  dataSource = new MatTableDataSource<any>();
  @ViewChild(MatSort) sort!: MatSort;

  internalActions: SortTableAction[] | undefined = [] as Array<SortTableAction>;

  constructor(protected userPreferenceService: UserPreferenceService,
    private appService: AppService

  ) { }

  ngOnInit() {
    if (this.refreshTable)
      this.refreshTableSubscription = this.refreshTable.subscribe(() => {
        if (this.values) {
          this.dataSource.data = this.values;
        }
      });
    this.internalActions = this.actions;
    if (this.values)
      this.dataSource.data = this.values;

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

  ngOnDestroy() {
    if (this.refreshTableSubscription)
      this.refreshTableSubscription.unsubscribe();
  }

  actionTrigger(action: SortTableAction, element: any) {
    // find in internal
    if (this.internalActions) {
      for (let internalAction of this.internalActions)
        if (action == internalAction) {
          internalAction.actionClick(action, element);
          if (this.values)
            this.dataSource.data = this.values;
        }
    }
  }

  applyFilter() {
    if (this.filterText && this.filterText.length > 0) {
      let filterValue = this.filterText.trim();
      filterValue = filterValue.toLowerCase();
      this.dataSource.filter = filterValue;
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.values != undefined && this.values) {
      this.dataSource.data = this.values;
      this.setSorter();
    }
    if (changes.filterText != undefined && this.values) {
      this.applyFilter();
    }
  }

  setSorter() {
    let a = {} as Employee;
    setTimeout(() => {
      this.dataSource.sort = this.sort;
      this.dataSource.sortingDataAccessor = (item: any, property) => {
        if (this.columns) {
          for (let column of this.columns) {
            if (column.id && column.id == property) {
              if (column.sortFonction)
                return column.sortFonction(item, this.values, column, this.columns);

              // Handle date or date string
              if (item instanceof Date)
                return item;

              // Handle employees
              let columnValue = this.getColumnValue(column, item);
              if (columnValue && columnValue instanceof Object && columnValue.firstname && columnValue.lastname)
                return columnValue.firstname + columnValue.lastname;
              return columnValue;
            }
          }
        }
      };

      this.dataSource.filterPredicate = (data: any, filter) => {
        if (!this.filterPredicate) {
          const dataStr = JSON.stringify(data).toLowerCase();
          return dataStr.indexOf(filter) != -1;
        } else {
          return this.filterPredicate(data, filter);
        }
      }
    });
  }

  getColumnLabel(column: SortTableColumn): string {
    if (column && column.label)
      return column.label;
    return "Not found";
  }

  getColumnValue(column: SortTableColumn, element: any): any {
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

  getIsElementWarnColor(column: SortTableColumn, element: any): any {
    if (column) {
      if (column.colorWarnFunction) {
        return column.colorWarnFunction(element);
      }
    }
    return false;
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
    if (this.internalActions)
      columnList.push('actions');
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

  getObjectPropertybyString = getObjectPropertybyString;

  getActionLink(action: SortTableAction, element: any) {
    if (action.actionLinkFunction)
      return action.actionLinkFunction(action, element);
  }

  getColumnLink(column: SortTableColumn, element: any) {
    if (column.actionLinkFunction)
      return column.actionLinkFunction(column, element);
  }

  openColumnLink(event: any, column: SortTableColumn, element: any,) {
    this.appService.openRoute(event, this.getColumnLink(column, element).join("/"), null);
  }

  openActionLink(event: any, action: SortTableAction, element: any,) {
    this.appService.openRoute(event, this.getActionLink(action, element).join("/"), null);
  }

}
