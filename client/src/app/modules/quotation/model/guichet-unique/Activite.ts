import { AdresseDomicile } from "./AdresseDomicile";
import { Building } from "./Building";
import { EffectifSalarie } from "./EffectifSalarie";
import { Jqpa } from "./Jqpa";
import { LocataireGerantMandataire } from "./LocataireGerantMandataire";
import { LocationGeranceMandat } from "./LocationGeranceMandat";
import { Origine } from "./Origine";
import { ActiviteReguliere } from "./referentials/ActiviteReguliere";
import { ExerciceActivite } from "./referentials/ExerciceActivite";
import { FormeExercice } from "./referentials/FormeExercice";
import { PrecisionActivite } from "./referentials/PrecisionActivite";
import { QualiteNonSedentaire } from "./referentials/QualiteNonSedentaire";
import { StatutFormalite } from "./referentials/StatutFormalite";
import { StatutPraticien } from "./referentials/StatutPraticien";
import { TotalitePartie } from "./referentials/TotalitePartie";

export interface Activite {
  rolePourEtablissement: string;
  dateEffet: Date;
  statutFormalite: StatutFormalite;
  indicateurPrincipal: boolean;
  indicateurProlongement: boolean;
  dateDebut: Date;
  dateFin: Date;
  exerciceActivite: ExerciceActivite;
  dateDebutPeriode: Date;
  dateFinPeriode: Date;
  indicateurNonSedentaire: boolean;
  formeExercice: FormeExercice;
  categorisationActivite1: string; // TODO : référentiel manquant
  precisionAutreCategorie1: string;
  categorisationActivite2: string; // TODO : référentiel manquant
  precisionAutreCategorie2: string;
  categorisationActivite3: string; // TODO : référentiel manquant
  precisionAutreCategorie3: string;
  categorisationActivite4: string; // TODO : référentiel manquant
  precisionAutreCategorie4: string;
  codeAprm: string;
  descriptionDetaillee: string;
  precisionActivite: PrecisionActivite;
  precisionAutre: string;
  surface: number;
  qualiteNonSedentaire: QualiteNonSedentaire;
  autreMotifModification: string;
  totalitePartie: TotalitePartie;
  locationDpb: boolean;
  indicateurArtisteAuteur: boolean;
  soumissionAuPrecompte: boolean;
  indicateurMarinProfessionnel: boolean;
  rolePrincipalPourEntreprise: boolean;
  codeApe: string;
  numPraticien: string;
  statutPraticien: StatutPraticien;
  activiteRattacheeEirl: boolean;
  denominationEirlRattachee: string;
  activiteReguliere: ActiviteReguliere;
  indicateurPremiereActivite: boolean;
  dateEffetTransfert: Date;
  identifiantTemporaireEtablissementDestination: string;
  ancienneAdresse: AdresseDomicile;
  origine: Origine;
  jqpa: Jqpa;
  locationGeranceMandat: LocationGeranceMandat;
  locataireGerantMandataire: LocataireGerantMandataire;
  adresseGerantMandataire: AdresseDomicile;
  is20PTriggered: boolean; // TODO : doc + suivants
  is61PMFTriggered: boolean;
  is62PMTriggered: boolean;
  is67PMTriggered: boolean;
  dateEffet20P: Date;
  dateEffetAjoutActivite: Date;
  dateEffet67PM: Date;
  is24Or27PMTriggered: boolean;
  dateEffet24Or27PM: Date;
  effectifSalarie: EffectifSalarie;
  buildings: Building[];
  is20MTriggered: boolean;
  dateEffet20M: Date;
  destinationActivite: string; // TODO : doc
}
