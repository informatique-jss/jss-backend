import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";
import { LegalForm } from "../../miscellaneous/model/LegalForm";

export interface BodaccFusionAbsorbedCompany {
  id: number;
  absorbedCompanyDenomination: string;
  absorbedCompanySiren: string;
  absorbedCompanyShareCapital: number;
  absorbedCompanyLegalForm: LegalForm;
  absorbedCompanyAddress: string;
  absorbedCompanyRcsDeclarationDate: Date;
  absorbedCompanyRcsCompetentAuthority: CompetentAuthority;
}
