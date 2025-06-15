import { ProvisionType } from "./ProvisionType";
import { FormeJuridique } from "./guichet-unique/referentials/FormeJuridique";

export interface AssoServiceProvisionType {
  id: number;
  provisionType: ProvisionType;
  apeCodes: string;
  formeJuridiques: FormeJuridique[];
  minEmployee: number;
  maxEmployee: number;
  customerMessageWhenAdded: string;
  complexity: number;
}
