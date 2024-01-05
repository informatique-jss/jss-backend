export interface SortTableAction<T> {
  actionIcon: string;
  actionName: string;
  actionClick: ((column: SortTableAction<T>, record: T, event: any) => void) | undefined;
  /**
   * Define a function that return a routerLink content to map the link
   * SortTable action and element are provided as parameters
   */
  actionLinkFunction: ((column: SortTableAction<T>, record: T) => string[]) | undefined;
  display: boolean;
}
