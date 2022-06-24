import { BodaccFusionAbsorbedCompany } from "./BodaccFusionAbsorbedCompany";
import { BodaccFusionMergingCompany } from "./BodaccFusionMergingCompany";

export interface BodaccFusion {
  id: number;
  bodaccFusionMergingCompanies: BodaccFusionMergingCompany[];
  bodaccFusionAbsorbedCompanies: BodaccFusionAbsorbedCompany[];
  assets: number;
  liabilities: number;
  mergerBonus: number;
  mergingProjectDate: Date;
  exchangeRatioReport: string;
}
