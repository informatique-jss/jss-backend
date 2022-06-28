import { IReferential } from "../../administration/model/IReferential";
import { NoticeTypeFamily } from "./NoticeTypeFamily";

export interface NoticeType extends IReferential {
  noticeTypeFamily: NoticeTypeFamily;
}
