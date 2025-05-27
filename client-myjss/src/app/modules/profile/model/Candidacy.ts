import { Mail } from "../../general/model/Mail";
import { IAttachment } from "../../my-account/model/IAttachment";

export interface Candidacy extends IAttachment {
  id: number;
  mail: Mail;
  searchedJob: string;
  message: string;
}
