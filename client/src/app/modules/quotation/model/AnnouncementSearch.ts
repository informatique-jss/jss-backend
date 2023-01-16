import { AnnouncementStatus } from './AnnouncementStatus';
import { Confrere } from './Confrere';

export interface AnnouncementSearch {
  confrere: Confrere;
  announcementStatus: AnnouncementStatus[];
  startDate: Date;
  endDate: Date;
}
