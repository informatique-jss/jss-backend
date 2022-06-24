import { CompetentAuthority } from "../../miscellaneous/model/CompetentAuthority";
import { LegalForm } from "../../miscellaneous/model/LegalForm";

export interface BodaccSplitBeneficiary {
  id: number;
  beneficiaryCompanyDenomination: string;
  beneficiaryCompanySiren: string;
  beneficiaryCompanyShareCapital: number;
  beneficiaryCompanyLegalForm: LegalForm;
  beneficiaryCompanyAddress: string;
  beneficiaryCompanyRcsDeclarationDate: Date;
  beneficiaryCompanyRcsCompetentAuthority: CompetentAuthority;
}
