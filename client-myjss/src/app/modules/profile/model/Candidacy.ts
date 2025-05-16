import { IAttachment } from "../../my-account/model/IAttachment";
import { Mail } from "./Mail";

export interface Candidacy extends IAttachment {
  id: number;
  mail: Mail;
  searchedJob: string;
  message: string;
}
