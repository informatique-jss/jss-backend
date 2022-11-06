import { Contractant } from "./Contractant";
import { Entreprise } from "./Entreprise";
import { Immeuble } from "./Immeuble";
import { RoleContrat } from "./referentials/RoleContrat";
import { StatutContrat } from "./referentials/StatutContrat";
import { TaciteReconduction } from "./referentials/TaciteReconduction";
import { TypePersonneContractante } from "./referentials/TypePersonneContractante";

export interface Contrat {
  roleContrat: RoleContrat;
  statutContrat: StatutContrat;
  dateDebutContrat: Date;
  dateFinContrat: Date;
  dateEffetContrat: Date;
  taciteReconduction: TaciteReconduction;
  typePersonneContractante: TypePersonneContractante;
  resiliationContrat: boolean;
  indicateurRenouvellementContrat: boolean;
  contractant: Contractant;
  entreprise: Entreprise;
  adresse: Immeuble;
  dateEffet: Date;
}

