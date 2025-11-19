import { GenericSearchForm } from "./GenericSearchForm";

export interface GenericSearchTab<U> {
  label: string;
  icon: string;
  forms: GenericSearchForm<U>[];
}
