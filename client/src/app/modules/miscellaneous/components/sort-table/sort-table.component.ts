import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Observable, Subscription } from 'rxjs';
import { getObjectPropertybyString } from 'src/app/libs/FormatHelper';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { AppService } from '../../../../services/app.service';
import { Employee } from '../../../profile/model/Employee';
import { EmployeeService } from '../../../profile/services/employee.service';
import { SortTableAction } from '../../model/SortTableAction';
import { SortTableColumn } from '../../model/SortTableColumn';
import { SortTableElement, SortTableElementActions, SortTableElementColumns, SortTableElementColumnsLink, SortTableElementColumnsStatus, SortTableElementWarn } from '../../model/SortTableElement';

@Component({
  selector: 'sort-table',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './sort-table.component.html',
  styleUrls: ['./sort-table.component.css']
})

export class SortTableComponent<T> implements OnInit {
  @Input() columns: SortTableColumn<T>[] | undefined;
  displayedColumns: string[] | undefined;
  @Input() values: T[] | undefined;
  @Input() actions: SortTableAction<T>[] | undefined;
  @Input() filterText: string | undefined;
  @Input() tableName: string = "table";
  @Input() idRowSelected: number | undefined;
  @Input() filterPredicate: ((record: SortTableElement, filter: string) => boolean) | undefined;
  @Input() displayTotalLines: boolean = false;
  @Input() isSmallerHeight: boolean = false;
  @Input() isSmallestHeight: boolean = false;
  @Input() noPaddingBottom: boolean = false;
  @Input() noMaxHeight: boolean = false;
  /**
 * Fired when row is clicked is modified by user
 */
  @Output() onRowClick: EventEmitter<T> = new EventEmitter();

  @Input() refreshTable: Observable<void> | undefined;
  private refreshTableSubscription: Subscription | undefined;

  dataSource = new MatTableDataSource<SortTableElement>();
  @ViewChild(MatSort) sort!: MatSort;

  internalActions: SortTableAction<T>[] | undefined = [] as Array<SortTableAction<T>>;
  allEmployees: Employee[] | undefined;

  constructor(protected userPreferenceService: UserPreferenceService,
    private appService: AppService,
    private employeeService: EmployeeService
  ) { }

  ngOnInit() {
    if (this.refreshTable)
      this.refreshTableSubscription = this.refreshTable.subscribe(() => {
        if (this.values) {
          this.dataSource.data = this.getSortTableElementsFromValues(this.values);
          this.internalActions = this.actions;
        }
      });
    this.internalActions = this.actions;

    this.employeeService.getEmployees().subscribe(res => {
      this.allEmployees = res;
      this.refreshValues();
    })

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

  refreshValues() {
    this.setDisplayedColumns();
    if (this.values)
      this.dataSource.data = this.getSortTableElementsFromValues(this.values);
  }

  getSortTableElementsFromValues(values: T[]): SortTableElement[] {
    let outValues = [] as Array<SortTableElement>;
    if (values)
      for (let value of values)
        outValues.push(this.getSortTableElementFromValue(value));
    return outValues;
  }

  getSortTableElementFromValue(value: T): SortTableElement {
    let outValue = {} as SortTableElement;
    if (value) {
      outValue.rawValue = value;
      outValue.columns = {} as SortTableElementColumns;
      outValue.rawColumns = {} as SortTableElementColumns;
      outValue.isElementWarn = {} as SortTableElementWarn;
      outValue.actionsLink = {} as SortTableElementActions;
      outValue.columnsLink = {} as SortTableElementColumnsLink;
      outValue.columnsStatus = {} as SortTableElementColumnsStatus;
      if (this.columns) {
        for (let column of this.columns) {
          outValue.columns[column.id] = this.getColumnValue(column, value, false);

          if (column.displayAsEmployee) {
            let employee = this.getEmployee(column, value);
            if (employee)
              outValue.columns[column.id] = employee;
          }


          outValue.rawColumns[column.id] = this.getColumnValue(column, value, true);
          if (column.colorWarnFunction) {
            outValue.isElementWarn[column.id] = column.colorWarnFunction(value);
          } else {
            outValue.isElementWarn[column.id] = false;
          }
          let actionLink = this.getColumnLink(column, value);
          if (actionLink)
            outValue.columnsLink[column.id] = actionLink;
          let columnStatus = this.getColumnStatus(column, value);
          if (columnStatus)
            outValue.columnsStatus[column.id] = columnStatus;
        }
      }
      if (this.actions) {
        for (let action of this.actions) {
          let link = this.getActionLink(action, value);
          if (link)
            outValue.actionsLink[action.actionName] = link;
        }
      }
    }
    return outValue;
  }

  ngOnDestroy() {
    if (this.refreshTableSubscription)
      this.refreshTableSubscription.unsubscribe();
  }

  actionTrigger(action: SortTableAction<T>, element: T, event: any) {
    // find in internal
    if (this.internalActions) {
      for (let internalAction of this.internalActions)
        if (action == internalAction && internalAction.actionClick) {
          internalAction.actionClick(action, element, event);
          this.refreshValues();
        }
    }
  }

  columnActionTrigger(column: SortTableColumn<T>, element: T) {
    // find in internal
    if (column && column.actionFunction) {
      column.actionFunction(element);
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
      this.refreshValues();
      this.setSorter();
    }
    if (changes.filterText != undefined && this.values) {
      this.applyFilter();
    }
  }

  setSorter() {
    setTimeout(() => {
      this.dataSource.sort = this.sort;
      this.dataSource.sortingDataAccessor = (item: SortTableElement, property) => {
        if (this.columns) {
          for (let column of this.columns) {
            if (column.id && column.id == property) {
              let sortValue: string | Date | Employee | number;

              if (column.sortFonction)
                sortValue = column.sortFonction(item, column, item.rawColumns[column.id]);
              else if (item.rawColumns[column.id])
                sortValue = item.rawColumns[column.id];
              else
                sortValue = item.columns[column.id];

              if (sortValue instanceof Date)
                return sortValue.getTime();
              if (sortValue && column.displayAsEmployee)
                return (item.columns[column.id] as Employee).firstname + (item.columns[column.id] as Employee).lastname;
              if (typeof sortValue === "number")
                return parseFloat(sortValue + "");
              return sortValue + '';
            }
          }
        }
        return "";
      };

      this.dataSource.filterPredicate = (data: SortTableElement, filter) => {
        if (!this.filterPredicate) {
          const dataStr = JSON.stringify(data).toLowerCase();
          return dataStr.indexOf(filter) != -1;
        } else {
          return this.filterPredicate(data, filter);
        }
      }
    });
  }

  getColumnValue(column: SortTableColumn<T>, element: T, byPassValueFunction: boolean): any {
    if (column) {
      if (column.valueFonction && !byPassValueFunction) {
        return column.valueFonction(element, column);
      }
      if (column.fieldName) {
        if (column.fieldName.indexOf(".") >= 0)
          try {
            return this.getObjectPropertybyString(element, column.fieldName);
          } catch {
          }

        type ObjectKey = keyof typeof element;
        return element[column.fieldName as ObjectKey];
      }
    }
    return "Not found";
  }

  getEmployee(column: SortTableColumn<T>, element: T): Employee | null {
    let employeeId = this.getColumnValue(column, element, false);
    if (employeeId && this.allEmployees)
      for (let employee of this.allEmployees)
        if (employee.id == employeeId)
          return employee;
    return null;
  }

  getColumnStatus(column: SortTableColumn<T>, element: T): string {
    if (column) {
      if (column.statusFonction) {
        return column.statusFonction(element);
      }
    }
    return "Not found";
  }

  setDisplayedColumns() {
    this.displayedColumns = [];
    if (this.internalActions && this.internalActions.length > 0)
      this.displayedColumns.push('actions');
    if (this.columns)
      for (let column of this.columns) {
        if (column.display == undefined)
          column.display = true;
        if (column.display)
          this.displayedColumns.push(column.id);
      }

  }

  selectColumn(column: SortTableColumn<T>, $event: any) {
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
    this.onRowClick.emit(element.rawValue);
  }

  getObjectPropertybyString = getObjectPropertybyString;

  getActionLink(action: SortTableAction<T>, element: T): string | undefined {
    if (action.actionLinkFunction) {
      let link = action.actionLinkFunction(action, element);
      if (link)
        return action.actionLinkFunction(action, element).join("/");
    }
    return undefined;
  }

  getColumnLink(column: SortTableColumn<T>, element: T): string | undefined {
    if (column.actionLinkFunction) {
      let link = column.actionLinkFunction(column, element);
      if (link)
        return column.actionLinkFunction(column, element).join("/");
    }
    return undefined;
  }

  openColumnLink(event: any, column: SortTableColumn<T>, element: T,) {
    let link = this.getColumnLink(column, element);
    if (link)
      this.appService.openRoute(event, link, null);
  }

  openActionLink(event: any, action: SortTableAction<T>, element: SortTableElement) {
    let link = element.actionsLink[action.actionName];
    if (link)
      this.appService.openRoute(event, link, null);
  }

}
