import { FormeJuridique } from "./referentials/FormeJuridique";
import { TypeExploitation } from "./referentials/TypeExploitation";

export interface NatureCreation {
  societeEtrangere: boolean;
  formeJuridique: FormeJuridique;
  typeExploitation: TypeExploitation;
  microEntreprise: boolean;
  etablieEnFrance: boolean;
  salarieEnFrance: boolean;
  relieeEntrepriseAgricole: boolean;
  entrepriseAgricole: boolean;
  eirl: boolean;
  indicateurEtablissementFictif: boolean;
  seulsBeneficiairesModifies: boolean;
  dateDepotCreation: Date;
}
