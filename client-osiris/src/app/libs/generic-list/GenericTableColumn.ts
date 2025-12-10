import { AccessorFn, CellContext, ColumnDefTemplate } from "@tanstack/angular-table";
import { GenericTableColumnMeta } from "./GenericTableColumnMeta";

export interface GenericTableColumn<T> {
  accessorKey?: keyof T,
  accessorFn?: AccessorFn<T, string>;
  header: string,
  enableSorting: boolean,
  cell?: ColumnDefTemplate<CellContext<T, any>>,
  meta: GenericTableColumnMeta<T>
}
