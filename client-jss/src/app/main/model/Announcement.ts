import { Department } from "./Department";
import { NoticeType } from "./NoticeType";
import { NoticeTypeFamily } from "./NoticeTypeFamily";

export interface Announcement {
  id: number;
  department: Department;
  publicationDate: Date;
  noticeTypeFamily: NoticeTypeFamily;
  noticeTypes: NoticeType[];
  notice: string;
  noticeHeader: string;
  affaireLabel: string;
  affaireSiren: string;
  isLegacy: boolean;
}

