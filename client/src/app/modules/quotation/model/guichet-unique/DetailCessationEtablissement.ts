import { AdresseDomicile } from "./AdresseDomicile";
import { PreneurBail } from "./PreneurBail";
import { Publication } from "./Publication";
import { Destination } from "./referentials/Destination";
import { LieuDeLiquidation } from "./referentials/LieuDeLiquidation";
import { MotifCessation } from "./referentials/MotifCessation";
import { MotifDisparition } from "./referentials/MotifDisparition";
import { TypeDissolution } from "./referentials/TypeDissolution";
import { Repreneur } from "./Repreneur";

export interface DetailCessationEtablissement {
  repreneurs: Repreneur[];
  adresse: AdresseDomicile;
  preneurBail: PreneurBail;
  adressePreneurBail: AdresseDomicile;
  publiciteNominationLiquidateur: Publication;
  motifCessation: MotifCessation;
  maintienRcs: boolean;
  maintienRm: boolean;
  dateCessationActiviteSalariee: Date;
  dateEffet: Date;
  lieuDeLiquidation: LieuDeLiquidation;
  dateCessationTotaleActivite: Date;
  dateClotureLiquidation: Date;
  dateTransfertPatrimoine: Date;
  dateDissolutionDisparition: Date;
  indicateurCessationTemporaire: boolean;
  indicateurDecesEntrepreneur: boolean;
  indicateurPoursuiteActivite: boolean;
  indicateurMaintienImmatriculationRegistre: boolean;
  destination: Destination;
  indicateurDissolution: boolean;
  typeDissolution: TypeDissolution;
  indicateurDisparitionPM: boolean;
  motifDisparition: MotifDisparition;
  indicateurPresenceSalarie: boolean;
  dateMiseEnSommeil: Date;
}

