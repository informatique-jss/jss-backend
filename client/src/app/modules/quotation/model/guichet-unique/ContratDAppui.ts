import { AdresseDomicile } from "./AdresseDomicile";
import { Entreprise } from "./Entreprise";
import { RoleContrat } from "./referentials/RoleContrat";
import { StatutContrat } from "./referentials/StatutContrat";
import { TaciteReconduction } from "./referentials/TaciteReconduction";
import { TypePersonneContractante } from "./referentials/TypePersonneContractante";
import { Repreneur } from "./Repreneur";

export interface ContratDAppui {
  roleContrat: RoleContrat;
  statutContrat: StatutContrat;
  dateDebutContrat: Date;
  dateFinContrat: Date;
  dateEffetContrat: Date;
  taciteReconduction: TaciteReconduction;
  typePersonneContractante: TypePersonneContractante;
  resiliationContrat: boolean;
  indicateurRenouvellementContrat: boolean;
  contractant: Repreneur;
  entreprise: Entreprise;
  adresse: AdresseDomicile;
  dateEffet: Date;
}

