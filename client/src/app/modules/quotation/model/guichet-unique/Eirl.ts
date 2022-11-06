import { AncienneEIRL } from "./AncienneEIRL";
import { OptionFiscale } from "./OptionFiscale";
import { MotifFinEirl } from "./referentials/MotifFinEirl";
import { OptionEirl } from "./referentials/OptionEirl";
import { RegistreEirl } from "./referentials/RegistreEirl";
import { Repreneur } from "./Repreneur";

export interface Eirl {
  optionEirl: OptionEirl;
  registreEirl: RegistreEirl;
  dateEffet: Date;
  indicateurDeclarationComplementaireAffectationPatrimoine: boolean;
  motifFinEirl: MotifFinEirl;
  intentionDePoursuite: boolean;
  denomination: string;
  objet: string;
  ancienneEIRL: AncienneEIRL;
  optionFiscale: OptionFiscale;
  repreneur: Repreneur;
}

