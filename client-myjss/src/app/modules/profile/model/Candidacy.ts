import { IAttachment } from "../../my-account/model/IAttachment";

export interface Candidacy extends IAttachment {
  id: number;
  mail: string;
  searchedJob: string;
  message: string;
}
