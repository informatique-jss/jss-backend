import { Mail } from "./Mail";
import { Tiers } from "./Tiers";
import { TiersDocumentType } from "./TiersDocumentType";

export interface TiersDocument {
  id: number;
  tiers: Tiers[];
  code: string;
  tiersDocumentType: TiersDocumentType;
  isRecipientClient: boolean;
  isRecipientAffaire: boolean;
  affaireAddress: string;
  clientAddress: string;
  affaireRecipient: string;
  clientRecipient: string;
  mails: Mail[];
  isMailingPaper: boolean;
  isMailingPdf: boolean;
  numberMailingAffaire: number;
  numberMailingClient: number;
  mailsClient: Mail[];
  mailsAffaire: Mail[];
}
