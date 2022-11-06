import { DestinationEtablissement } from "./referentials/DestinationEtablissement";
import { RolePourEntreprise } from "./referentials/RolePourEntreprise";
import { StatutPourFormalite } from "./referentials/StatutPourFormalite";

export interface DescriptionEtablissement {
  rolePourEntreprise: RolePourEntreprise;
  indicateurEtranger: boolean;
  pays: string;
  siret: string;
  autreIdentifiantEtranger: string;
  indicateurDomiciliataire: boolean;
  identifiantTemporaire: string;
  activiteNonSedentaire: boolean;
  enseigne: string;
  nomCommercial: string;
  autonomieJuridique: boolean;
  greffeImmatriculation: string;
  lieuImmatriculation: string;
  dateFinToutEffectifSalarie: Date;
  destinationEtablissement: DestinationEtablissement;
  autreDestination: string;
  sansActiviteAutreActiviteSiege: boolean;
  indicateurEtablissementPrincipal: boolean;
  statutPourFormalite: StatutPourFormalite;
  dateEffet: Date;
  dateEffetFermeture: Date;
  dateEffetTransfert: Date;
  nomEtablissement: string;
  statutOuvertFerme: string; // TODO : référentiel associé ?
}

