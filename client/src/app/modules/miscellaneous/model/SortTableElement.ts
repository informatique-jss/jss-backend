import { Employee } from "../../profile/model/Employee";

export interface SortTableElementColumns {
  [index: string]: string | Employee;
}
export interface SortTableElementColumnsLink {
  [index: string]: string;
}
export interface SortTableElementColumnsStatus {
  [index: string]: string;
}
export interface SortTableElementActions {
  [index: string]: string;
}
export interface SortTableElementWarn {
  [index: string]: boolean;
}

export interface SortTableElement<T> {
  columns: SortTableElementColumns;
  rawColumns: SortTableElementColumns;
  actionsLink: SortTableElementActions;
  columnsLink: SortTableElementColumnsLink;
  columnsStatus: SortTableElementColumnsStatus;
  isElementWarn: SortTableElementWarn;
  rawValue: any;
}
