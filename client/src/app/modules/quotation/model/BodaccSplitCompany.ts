import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";
import { LegalForm } from "../../miscellaneous/model/LegalForm";

export interface BodaccSplitCompany {
  id: number;
  splitCompanyDenomination: string;
  splitCompanySiren: string;
  splitCompanyShareCapital: number;
  splitCompanyLegalForm: LegalForm;
  splitCompanyAddress: string;
  splitCompanyRcsDeclarationDate: Date;
  splitCompanyRcsCompetentAuthority: CompetentAuthority;
}
