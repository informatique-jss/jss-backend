import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";
import { LegalForm } from "../../miscellaneous/model/LegalForm";

export interface BodaccSplit {
  id: number;
  beneficiaryCompanyDenomination: string;
  beneficiaryCompanySiren: string;
  beneficiaryCompanyShareCapital: number;
  beneficiaryCompanyLegalForm: LegalForm;
  beneficiaryCompanyAddress: string;
  beneficiaryCompanyRcsDeclarationDate: Date;
  beneficiaryCompanyRcsCompetentAuthority: CompetentAuthority;
  splitCompanyDenomination: string;
  splitCompanySiren: string;
  splitCompanyShareCapital: number;
  splitCompanyLegalForm: LegalForm;
  splitCompanyAddress: string;
  splitCompanyRcsDeclarationDate: Date;
  splitCompanyRcsCompetentAuthority: CompetentAuthority;
  assets: number;
  liabilities: number;
  splitBonus: number;
  splitProjectDate: Date;
  exchangeRatioReport: string;
}
