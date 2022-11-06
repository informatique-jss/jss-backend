import { Modalite } from "./Modalite";
import { Repreneur } from "./Repreneur";

export interface BeneficiairesEffectif {
  indexPouvoir: boolean;
  beneficiaire: Repreneur;
  modalite: Modalite;
}

