import { Affaire } from "./Affaire";
import { Service } from "./Service";

export interface AssoAffaireOrder {
  id: number;
  affaire: Affaire;
  services: Service[];
}
