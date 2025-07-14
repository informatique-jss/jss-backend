import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { Mail } from "../../miscellaneous/model/Mail";

export interface Candidacy extends IAttachment {
  id: number;
  mail: Mail;
  searchedJob: string;
  message: string;
  isTreated: boolean;
}
