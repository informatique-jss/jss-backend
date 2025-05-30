import { Author } from "./Author";
import { Mail } from "./Mail";

export interface AssoMailAuthor {
  id: number;
  mail: Mail;
  author: Author;
  lastConsultationDate: Date;
}
