import { IAttachment } from "../../miscellaneous/model/IAttachment";

export interface Journal extends IAttachment {
  id: number;
  label: string;
  journalDate: Date;
}
