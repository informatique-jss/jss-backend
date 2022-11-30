import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { Employee } from "../../profile/model/Employee";
import { Announcement } from "./Announcement";
import { AssoAffaireOrder } from "./AssoAffaireOrder";
import { Bodacc } from "./Bodacc";
import { Domiciliation } from "./Domiciliation";
import { Formalite } from './guichet-unique/Formalite';
import { InvoiceItem } from "./InvoiceItem";
import { ProvisionFamilyType } from "./ProvisionFamilyType";
import { ProvisionType } from "./ProvisionType";

export interface Provision extends IAttachment {
  id: number;
  domiciliation: Domiciliation | undefined;
  announcement: Announcement | undefined;
  bodacc: Bodacc | undefined;
  formalite: Formalite | undefined;
  provisionFamilyType: ProvisionFamilyType;
  provisionType: ProvisionType;
  invoiceItems: InvoiceItem[]
  assoAffaireOrder: AssoAffaireOrder;
  assignedTo: Employee;
  isLogo: boolean;
  isRedactedByJss: boolean;
  isBaloPackage: boolean;
  isPublicationReceipt: boolean;
  isPublicationPaper: boolean;
  publicationPaperAffaireNumber: number;
  publicationPaperClientNumber: number;
  isPublicationFlag: boolean;
  isBodaccFollowup: boolean;
  isBodaccFollowupAndRedaction: boolean;
  isNantissementDeposit: boolean;
  isSocialShareNantissementRedaction: boolean;
  isBusinnessNantissementRedaction: boolean;
  isSellerPrivilegeRedaction: boolean;
  isTreatmentMultipleModiciation: boolean;
  isVacationMultipleModification: boolean;
  isRegisterPurchase: boolean;
  isRegisterInitials: boolean;
  isRegisterShippingCosts: boolean;
  isDisbursement: boolean;
  isFeasibilityStudy: boolean;
  isChronopostFees: boolean;
  isBankCheque: boolean;
  isComplexeFile: boolean;
  isDocumentScanning: boolean;
  isEmergency: boolean;

}
