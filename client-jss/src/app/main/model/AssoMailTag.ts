import { Mail } from "./Mail";
import { Tag } from "./Tag";

export interface AssoMailTag {
  id: number;
  mail: Mail;
  tag: Tag;
  lastConsultationDate: Date;
}
