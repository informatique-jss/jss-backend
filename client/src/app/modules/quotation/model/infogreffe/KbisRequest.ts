import { Attachment } from '../../../miscellaneous/model/Attachment';
export interface KbisRequest {
  id: number;
  siren: string;
  attachment: Attachment;
  dateOrder: Date;
}
