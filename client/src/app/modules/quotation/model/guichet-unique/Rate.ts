import { Partenaire } from "./Partenaire";

export interface Rate {
  amount: number;
  htAmount: number;
  code: string;
  label: string;
  partenaire: Partenaire;
}
