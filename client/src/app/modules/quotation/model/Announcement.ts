import { Department } from "../../miscellaneous/model/Department";
import { IDocument } from "../../miscellaneous/model/IDocument";
import { AnnouncementStatus } from './AnnouncementStatus';
import { Confrere } from "./Confrere";
import { NoticeType } from "./NoticeType";
import { NoticeTypeFamily } from "./NoticeTypeFamily";

export interface Announcement extends IDocument {
  id: number;
  department: Department;
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
  isAnnouncementAlreadySentToConfrere: boolean;
  announcementStatus: AnnouncementStatus;
  actuLegaleId: number;

  /**
   * WARNING : property not directly fethed from server
   * User ConfrereService.getConfrereForAnnouncement to fetch it !
   */
  confrere: Confrere;
}
