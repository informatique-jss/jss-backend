import { IAttachment } from "../../miscellaneous/model/IAttachment";

export interface Candidacy extends IAttachment {
  id: number;
  mail: string;
  searchedJob: string;
  message: string;
}
