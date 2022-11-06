import { EtablissementConcerne } from "./EtablissementConcerne";
import { CapaciteEngagement } from "./referentials/CapaciteEngagement";
import { Perimetre } from "./referentials/Perimetre";
import { RoleEntreprise } from "./referentials/RoleEntreprise";
import { SecondRoleEntreprise } from "./referentials/SecondRoleEntreprise";
import { StatutPourLaFormalite } from "./referentials/StatutPourLaFormalite";
import { TypeDePersonne } from "./referentials/TypeDePersonne";

export interface LienEntreprise {
  roleEntreprise: RoleEntreprise;
  statutPourLaFormalite: StatutPourLaFormalite;
  dateEffet: Date;
  etablissementConcerne: EtablissementConcerne[];
  typeDePersonne: TypeDePersonne;
  capaciteEngagement: CapaciteEngagement;
  exonerationDesDettesAnterieures: boolean;
  montantsDesParticipation: number;
  perimetre: Perimetre;
  beneficiaireEffectif: boolean;
  indicateurSecondRoleEntreprise: boolean;
  secondRoleEntreprise: SecondRoleEntreprise;
  isSAOrSASMajorityManager: boolean;
  autreRoleEntreprise: string;
  is31PTriggered: boolean;
  dateEffet31P: Date;
}

