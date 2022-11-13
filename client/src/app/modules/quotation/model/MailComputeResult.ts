import { Mail } from "../../miscellaneous/model/Mail";

export interface MailComputeResult {
  recipientsMailTo: Mail[];
  recipientsMailCc: Mail[];
  isSendToClient: boolean;
  isSendToAffaire: boolean;
  mailToClientOrigin: string;
  mailToAffaireOrigin: string;
  mailCcAffaireOrigin: string;
  mailCcClientOrigin: string;
}
