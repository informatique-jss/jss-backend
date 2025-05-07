import { Mail } from "../../profile/model/Mail";

export interface WebinarParticipant {
  id: number;
  firstname: string;
  lastname: string;
  mail: Mail;
  phoneNumber: string;
}
