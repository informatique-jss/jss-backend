import { GenericForm } from "./GenericForm";

export type StringKeys<T> = Exclude<keyof T, symbol | number>;

export interface GenericSearchForm<U> {
  accessorKey: StringKeys<U>;
  accessorIndex?: string;
  form: GenericForm<U>;
}
