import { Affaire } from "../../my-account/model/Affaire";
import { ServiceType } from "../../my-account/model/ServiceType";
import { Department } from "../../profile/model/Department";
import { NoticeType } from "./NoticeType";
import { NoticeTypeFamily } from "./NoticeTypeFamily";

export interface ServiceTypeChosen {
  service: ServiceType;
  affaire: Affaire;
  temporaryId: number;
  preTaxPrice: number | undefined;
  discountedAmount: number | undefined;
  announcementPublicationDate: Date;
  announcementRedactedByJss: Boolean;
  announcementProofReading: Boolean;
  announcementNoticeFamily: NoticeTypeFamily;
  announcementNoticeType: NoticeType;
  announcementDepartment: Department;
  announcementNotice: string;
}
