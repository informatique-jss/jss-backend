import { EntrepriseMandataire } from "./EntrepriseMandataire";
import { LienEntreprise } from "./LienEntreprise";
import { Repreneur } from "./Repreneur";

export interface Declarant {
  lienEntreprise: LienEntreprise;
  individu: Repreneur;
  entrepriseMandataire: EntrepriseMandataire;
}

