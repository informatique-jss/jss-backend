import { Department } from "../../miscellaneous/model/Department";
import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { IDocument } from "../../miscellaneous/model/IDocument";
import { AnnouncementStatus } from './AnnouncementStatus';
import { Confrere } from "./Confrere";
import { NoticeType } from "./NoticeType";
import { NoticeTypeFamily } from "./NoticeTypeFamily";

export interface Announcement extends IAttachment, IDocument {
  id: number;
  department: Department;
  confrere: Confrere | undefined;
  publicationDate: Date;
  isRedactedByJss: boolean;
  notice: string;
  noticeHeader: string;
  noticeTypes: NoticeType[];
  noticeTypeFamily: NoticeTypeFamily;
  isHeader: boolean;
  isHeaderFree: boolean;
  isLegalDisplay: boolean;
  posterProductionPrice: number;
  posterProductionJSSPrice: number;
  billPostingPrice: number;
  billPostingJSSPrice: number;
  bailiffReportPrice: number;
  bailiffReportJSSPrice: number;
  isProofReadingDocument: boolean;
  isPublicationCertificateDocument: boolean;
  announcementStatus: AnnouncementStatus;
}
