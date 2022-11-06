import { Immeuble } from "./Immeuble";
import { ConditionVersementTVA } from "./referentials/ConditionVersementTVA";
import { DeviseCapital } from "./referentials/DeviseCapital";
import { OptionParticuliereRegimeBenefi } from "./referentials/OptionParticuliereRegimeBenefi";
import { PeriodiciteEtOptionsParticulie } from "./referentials/PeriodiciteEtOptionsParticulie";
import { PeriodiciteVersement } from "./referentials/PeriodiciteVersement";
import { RegimeImpositionBenefices } from "./referentials/RegimeImpositionBenefices";
import { RegimeImpositionBenefices2 } from "./referentials/RegimeImpositionBenefices2";
import { RegimeImpositionTVA } from "./referentials/RegimeImpositionTVA";

export interface OptionsFiscales {
  indicateurMembreExploitation: boolean;
  regimeImpositionBenefices: RegimeImpositionBenefices;
  optionParticuliereRegimeBenefice: OptionParticuliereRegimeBenefi;
  regimeImpositionTVA: RegimeImpositionTVA;
  periodiciteEtOptionsParticulieresTVA: PeriodiciteEtOptionsParticulie;
  conditionVersementTVA: ConditionVersementTVA;
  dateClotureExerciceComptable: Date;
  optionVersementLiberatoire: boolean;
  lieuImposition: string;
  dateEnregistrementStatuts: Date;
  dateEffetFiscalite: Date;
  regimeImpositionBenefices2: RegimeImpositionBenefices2;
  immeubles: Immeuble[];
  optionParticuliereRegimeBenefice2: OptionParticuliereRegimeBenefi;
  regimeImpositionTVA2: RegimeImpositionTVA;
  dateEffet: Date;
  periodiciteEtOptionsParticulieresTVA2: PeriodiciteEtOptionsParticulie;
  taxeTroisPourcent: boolean;
  detentionParticipationSocieteFrancaise: boolean;
  periodiciteVersement: PeriodiciteVersement;
  chiffreAffairePrevisionnelVente: number;
  chiffreAffairePrevisionnelService: number;
  deviseChiffreAffaire: DeviseCapital;
  numeroTVAIntra: string;
  redevableTVA: boolean;
  redevablePAS: boolean;
  numeroTVAFrance: string;
  clienteleIdentifieesTVA: boolean;
  clienteleParticuliers: boolean;
  clienteleAutre: boolean;
  ali: boolean;
  aic: boolean;
  lic: boolean;
  indicateurAutreOptionFiscale: boolean;
  autreOptionFiscale: string;
  conditionVersementTVA2: ConditionVersementTVA;
}

