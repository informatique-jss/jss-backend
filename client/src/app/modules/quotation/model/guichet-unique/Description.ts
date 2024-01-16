import { DeviseCapital } from "./referentials/DeviseCapital";
import { NatureDesActivite } from "./referentials/NatureDesActivite";
import { NatureGerance } from "./referentials/NatureGerance";
import { OrigineFusionScission } from "./referentials/OrigineFusionScission";
import { TypeDeStatuts } from "./referentials/TypeDeStatuts";

export interface Description {
  objet: string;
  sigle: string;
  duree: number;
  dateClotureExerciceSocial: String;
  datePremiereCloture: Date;
  ess: boolean;
  societeMission: boolean;
  capitalVariable: boolean;
  montantCapital: number;
  capitalMinimum: number;
  deviseCapital: DeviseCapital;
  statutLegalParticulier: string; // TODO : réféentiel ou chanmp libre ?
  dateAgrementGAEC: Date;
  typeDeStatuts: TypeDeStatuts;
  indicateurOrigineFusionScission: boolean;
  indicateurEtablissementsEtrangers: boolean;
  indicateurAssocieUnique: boolean;
  depotDemandeAcre: boolean;
  indicateurAssocieUniqueDirigeant: boolean;
  natureGerance: NatureGerance;
  natureDesActivite: NatureDesActivite;
  operationEntrainantUneAugmentationDeCapital: boolean;
  formeCooperative: boolean;
  numeroAgrementGAEC: string;
  capitalDisponible: boolean;
  prorogationDuree: boolean;
  continuationAvecActifNetInferieurMoitieCapital: boolean;
  reconstitutionCapitauxPropres: boolean;
  origineFusionScission: OrigineFusionScission;
  dateEffet: Date;
  is12MTriggered: boolean; // TODO : comment remplor ça ?
  is10MTriggered: boolean;
  is16MTriggered: boolean;
  is18MTriggered: boolean;
  is15MTriggered: boolean;
  is17MTriggered: boolean;
  is19MTriggered: boolean;
  dateEffet12M: Date;
  dateEffet10M: Date;
  dateEffet16M: Date;
  dateEffet18M: Date;
  dateEffet15M: Date;
  dateEffet17M: Date;
  dateEffet19M: Date;
}

