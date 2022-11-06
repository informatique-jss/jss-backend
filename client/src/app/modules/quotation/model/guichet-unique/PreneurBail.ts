import { TypePersonne } from "./referentials/TypePersonne";

export interface PreneurBail {
  typePersonne: TypePersonne;
  siren: string;
  denomination: string;
  nom: string;
  prenoms: string[];
  nomUsage: string;
  dateEffet: Date;
}

