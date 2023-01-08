import { Department } from "../../miscellaneous/model/Department";
import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { IDocument } from "../../miscellaneous/model/IDocument";
import { Journal } from '../../pao/model/Journal';
import { AnnouncementStatus } from './AnnouncementStatus';
import { Confrere } from "./Confrere";
import { NoticeType } from "./NoticeType";
import { NoticeTypeFamily } from "./NoticeTypeFamily";

export interface Announcement extends IAttachment, IDocument {
  id: number;
  department: Department;
  confrere: Confrere | undefined;
  publicationDate: Date;
  notice: string;
  noticeHeader: string;
  noticeTypes: NoticeType[];
  noticeTypeFamily: NoticeTypeFamily;
  isHeader: boolean;
  isHeaderFree: boolean;
  isProofReadingDocument: boolean;
  isPublicationFlagAlreadySent: boolean;
  isPublicationReciptAlreadySent: boolean;
  announcementStatus: AnnouncementStatus;
  journal: Journal;
  journalPages: string;
}
