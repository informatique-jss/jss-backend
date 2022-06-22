import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";
import { LegalForm } from "../../miscellaneous/model/LegalForm";

export interface BodaccFusion {
  id: number;
  mergingCompanyDenomination: string;
  mergingCompanySiren: string;
  mergingCompanyShareCapital: number;
  mergingCompanyLegalForm: LegalForm;
  mergingCompanyAddress: string;
  mergingCompanyRcsDeclarationDate: Date;
  mergingCompanyRcsCompetentAuthority: CompetentAuthority;
  absorbedCompanyDenomination: string;
  absorbedCompanySiren: string;
  absorbedCompanyShareCapital: number;
  absorbedCompanyLegalForm: LegalForm;
  absorbedCompanyAddress: string;
  absorbedCompanyRcsDeclarationDate: Date;
  absorbedCompanyRcsCompetentAuthority: CompetentAuthority;
  assets: number;
  liabilities: number;
  mergerBonus: number;
  mergingProjectDate: Date;
  exchangeRatioReport: string;
}
