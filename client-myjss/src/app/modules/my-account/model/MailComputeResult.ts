import { Mail } from "../../general/model/Mail";

export interface MailComputeResult {
  recipientsMailTo: Mail[];
  recipientsMailCc: Mail[];
}
