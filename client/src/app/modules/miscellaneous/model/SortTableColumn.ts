import { Employee } from "../../profile/model/Employee";
import { SortTableElement } from "./SortTableElement";

export interface SortTableColumn<T> {
  id: string;
  fieldName: string;
  label: string;
  /**
   * Function that return a string of the value to display
   * The raw element and element list is provided as input
   * If not provided, field value is used
   */
  valueFonction: ((element: T, column: SortTableColumn<T>) => string | Date | Employee | number) | undefined;
  /**
   * Function that return a string of the status to display
   * The raw element and element list is provided as input
   */
  statusFonction: ((element: T) => string) | undefined;
  /**
   * Function that return a string of the value to order items
   * The raw element and element list is provided as input
   * If not provided, label field is used
   */
  sortFonction: ((element: SortTableElement, column: SortTableColumn<T>, rawValue: string | Date | Employee | number) => string | Date | Employee | number) | undefined;
  /**
   * Function to display line as warn color
   * Return true or false
   */
  colorWarnFunction: ((element: T) => boolean) | undefined;
  display: boolean | undefined;
  /**
   * Display element as star
   */
  displayAsGrade: boolean | undefined;
  /**
   * Display element as employee (value fonction must provide an Employee object !)
   */
  displayAsEmployee: boolean | undefined;
  /**
   * Display element as a status chips (status function must provider a valid status code !)
   */
  displayAsStatus: boolean | undefined;
  /**
   * Indicate to use ellipsis on the column
   */
  isShrinkColumn: boolean | undefined;
  /**
   * Link function : link function to call on link icon click
   */
  actionLinkFunction: ((column: SortTableColumn<T>, element: T) => string[]) | undefined
  /**
   * Action function : action function to call on link icon click
   */
  actionFunction: ((element: T) => void) | undefined
  /**
   * Link icon to display
   */
  actionIcon: string;
  /**
   * Tooltip to display on action
   */
  actionTooltip: string;
}
