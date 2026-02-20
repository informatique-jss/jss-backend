import { Department } from "../../profile/model/Department";
import { NoticeType } from "../../quotation/model/NoticeType";
import { NoticeTypeFamily } from "../../quotation/model/NoticeTypeFamily";

export interface Announcement {
  id: number;
  department: Department;
  publicationDate: Date;
  notice: string;
  isProofReadingDocument: boolean;
  isReReadByJss: boolean;
  isUsingTemplate: boolean;
  noticeTypes: NoticeType[];
  noticeTypeFamily: NoticeTypeFamily;
}
