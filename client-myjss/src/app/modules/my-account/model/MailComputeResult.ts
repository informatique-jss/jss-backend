import { Mail } from "../../profile/model/Mail";

export interface MailComputeResult {
  recipientsMailTo: Mail[];
  recipientsMailCc: Mail[];
}
