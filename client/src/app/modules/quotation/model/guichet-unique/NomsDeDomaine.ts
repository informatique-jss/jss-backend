import { NatureDomaine } from "./referentials/NatureDomaine";
import { StatutDomaine } from "./referentials/StatutDomaine";

export interface NomsDeDomaine {
  natureDomaine: NatureDomaine;
  statutDomaine: StatutDomaine;
  nomDomaine: string;
  dateEffet: Date;
  is55PMTriggered: boolean;
  is14MTriggered: boolean;
  dateEffet14M: Date;
}

