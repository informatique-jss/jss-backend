import { Row } from "@tanstack/angular-table";
import { Subject } from "rxjs";

export interface GenericTableAction<T> {
  label?: string;
  eventOnClick?: Subject<Row<T>[]>,
  minNumberOfElementsRequiredToDisplay?: number,
  maxNumberOfElementsRequiredToDisplay?: number,
  isDisplayOutOfMenu?: boolean
  isDivider?: boolean;
  buttonClass?: string;
}
