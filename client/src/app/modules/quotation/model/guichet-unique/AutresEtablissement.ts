import { Activite } from "./Activite";
import { AdresseDomicile } from "./AdresseDomicile";
import { DescriptionEtablissement } from "./DescriptionEtablissement";
import { DetailCessationEtablissement } from "./DetailCessationEtablissement";
import { EffectifSalarie } from "./EffectifSalarie";
import { Entreprise } from "./Entreprise";
import { NomsDeDomaine } from "./NomsDeDomaine";

export interface AutresEtablissement {
  descriptionEtablissement: DescriptionEtablissement;
  domiciliataire: Entreprise;
  adresseDomiciliataire: AdresseDomicile;
  effectifSalarie: EffectifSalarie;
  adresse: AdresseDomicile;
  activites: Activite[];
  nomsDeDomaine: NomsDeDomaine[];
  detailCessationEtablissement: DetailCessationEtablissement;
  dateEffetOuvertureEtablissement: Date;
}

