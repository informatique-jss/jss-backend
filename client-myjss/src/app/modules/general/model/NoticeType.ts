import { NoticeTypeFamily } from "./NoticeTypeFamily";

export interface NoticeType {
  id: number;
  code: string;
  label: string;
  noticeTypeFamily: NoticeTypeFamily;
}
