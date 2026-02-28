import { Mail } from "../../miscellaneous/model/Mail";

export interface WebinarParticipant {
  id: number;
  firstname: string;
  lastname: string;
  mail: Mail;
  phoneNumber: string;
  comment: string;
}
