import { GenericForm } from "./GenericForm";

export interface GenericSearchForm<U> {
  accessorKey: keyof U;
  accessorIndex?: string;
  form: GenericForm<U>;
}
