import { Department } from "../../miscellaneous/model/Department";
import { IAttachment } from "../../miscellaneous/model/IAttachment";
import { IDocument } from "../../miscellaneous/model/IDocument";
import { Confrere } from "./Confrere";
import { JournalType } from "./JournalType";
import { NoticeType } from "./NoticeType";
import { NoticeTypeFamily } from "./NoticeTypeFamily";

export interface Shal extends IAttachment, IDocument {
  id: number;
  department: Department;
  confrere: Confrere | null;
  journalType: JournalType;
  publicationDate: Date;
  isRedactedByJss: boolean;
  notice: string;
  noticeHeader: string;
  noticeTypes: NoticeType[];
  noticeTypeFamily: NoticeTypeFamily;
  isLogo: boolean;
  isHeader: boolean;
  isHeaderFree: boolean;
  isPictureBaloPackage: boolean;
  isLegalDisplay: boolean;
  posterProductionPrice: number;
  posterProductionJSSPrice: number;
  billPostingPrice: number;
  billPostingJSSPrice: number;
  bailiffReportPrice: number;
  bailiffReportJSSPrice: number;
  isProofReadingDocument: boolean;
  isPublicationCertificateDocument: boolean;
}
