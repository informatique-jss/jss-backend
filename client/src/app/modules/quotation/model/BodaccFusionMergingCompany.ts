import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";
import { LegalForm } from "../../miscellaneous/model/LegalForm";

export interface BodaccFusionMergingCompany {
  id: number;
  mergingCompanyDenomination: string;
  mergingCompanySiren: string;
  mergingCompanyShareCapital: number;
  mergingCompanyLegalForm: LegalForm;
  mergingCompanyAddress: string;
  mergingCompanyRcsDeclarationDate: Date;
  mergingCompanyRcsCompetentAuthority: CompetentAuthority;
}
