import { CompteBilan } from "./CompteBilan";
import { CompteResultat } from "./CompteResultat";

export interface ComptesAnnuels {
  cotation?: any;
  interventionCAC: boolean;
  comptesApprouves: boolean;
  comptesConsolides: boolean;
  associeUniqueSeulGerant?: any;
  petiteEntreprise: boolean;
  presenceConseilSurveillance?: any;
  presenceConseilAdministration?: any;
  associeUniquePresident: boolean;
  activiteCreditOuAssurance: boolean;
  associesSA?: any;
  montantCAAnneePrecedente: number;
  montantTotalBilanAnneePrecedente: number;
  activiteProspection: boolean;
  activiteSansConfidentialite: boolean;
  nbSalarie: number;
  dateCloture: Date;
  dateDebutExerciceComptable: Date;
  dateFinExerciceComptable: Date;
  dispenseDepotAnnexes: boolean;
  declarationAffectationPatrimoine: boolean;
  depotSimplifie: boolean;
  nouveauDepot: boolean;
  niveauConfidentialite: string;
  depotModeExpert: boolean;
  modeExpert?: any;
  compteBilan: CompteBilan;
  compteResultat: CompteResultat;
}
