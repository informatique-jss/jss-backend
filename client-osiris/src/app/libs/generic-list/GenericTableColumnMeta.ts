import { Row } from "@tanstack/angular-table";
import { Subject } from "rxjs";

export interface GenericTableColumnMeta<T> {
  eventOnDoubleClick?: Subject<Row<T>>
}
