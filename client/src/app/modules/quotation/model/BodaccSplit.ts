import { BodaccSplitBeneficiary } from "./BodaccSplitBeneficiary";
import { BodaccSplitCompany } from "./BodaccSplitCompany";

export interface BodaccSplit {
  id: number;
  bodaccSplitBeneficiaries: BodaccSplitBeneficiary[];
  bodaccSplitCompanies: BodaccSplitCompany[];
  assets: number;
  liabilities: number;
  splitBonus: number;
  splitProjectDate: Date;
  exchangeRatioReport: string;
}
