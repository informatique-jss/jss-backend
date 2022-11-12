export interface SortTableColumn {
  id: string;
  fieldName: string;
  label: string;
  /**
   * Function that return a string of the value to display
   * The raw element and element list is provided as input
   * If not provided, field value is used
   */
  valueFonction: any | undefined;
  /**
   * Function that return a string of the value to order items
   * The raw element and element list is provided as input
   * If not provided, label field is used
   */
  sortFonction: any | undefined;
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
   * Indicate to use ellipsis on the column
   */
  isShrinkColumn: boolean | undefined;
  /**
   * Link function : link function to call on link icon click
   */
  actionLinkFunction: any | undefined;
  /**
   * Link icon to display
   */
  actionIcon: string;
  /**
   * Tooltip to display on action
   */
  actionTooltip: string;
}
