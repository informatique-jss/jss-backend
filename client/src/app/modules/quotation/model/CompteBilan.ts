import { Pagination } from "./Pagination";

export interface CompteBilan {
  pagination: Pagination;
  confidentiel: boolean;
  montantCapitauxPropres: number;
}
