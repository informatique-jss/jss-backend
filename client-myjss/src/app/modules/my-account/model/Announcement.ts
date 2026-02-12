import { Department } from "../../profile/model/Department";
import { NoticeType } from "../../quotation/model/NoticeType";
import { NoticeTypeFamily } from "../../quotation/model/NoticeTypeFamily";
import { Confrere } from "./Confrere";

export interface Announcement {
  id: number;
  department: Department;
  confrere: Confrere;
  publicationDate: Date;
  notice: string;
  isProofReadingDocument: boolean;
  isReReadByJss: boolean;
  isUsingTemplate: boolean;
  noticeTypes: NoticeType[];
  noticeTypeFamily: NoticeTypeFamily;
}
