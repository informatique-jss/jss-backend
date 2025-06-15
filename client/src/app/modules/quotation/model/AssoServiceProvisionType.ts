import { AnnouncementNoticeTemplate } from "./AnnouncementNoticeTemplate";
import { NoticeType } from "./NoticeType";
import { NoticeTypeFamily } from "./NoticeTypeFamily";
import { ProvisionType } from "./ProvisionType";
import { FormeJuridique } from "./guichet-unique/referentials/FormeJuridique";

export interface AssoServiceProvisionType {
  id: number;
  provisionType: ProvisionType;
  apeCodes: string;
  formeJuridiques: FormeJuridique[];
  minEmployee: number;
  maxEmployee: number;
  customerMessageWhenAdded: string;
  noticeType: NoticeType;
  noticeTypeFamily: NoticeTypeFamily;
  noticeTemplate: AnnouncementNoticeTemplate;
  complexity: number;
}
