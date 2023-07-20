import { Pagination } from "./Pagination";

export interface CompteResultat {
  pagination: Pagination;
  confidentiel: boolean;
  montantCA: number;
  resultatNet: number;
}
