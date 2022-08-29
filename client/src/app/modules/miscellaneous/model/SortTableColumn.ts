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
  displayAsGrade: boolean | undefined;
}
