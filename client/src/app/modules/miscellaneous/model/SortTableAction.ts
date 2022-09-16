export interface SortTableAction {
  actionIcon: string;
  actionName: string;
  actionClick: any | undefined;
  /**
   * Define a function that return a routerLink content to map the link
   * SortTable action and element are provided as parameters
   */
  actionLinkFunction: any | undefined;
  display: boolean;
}
