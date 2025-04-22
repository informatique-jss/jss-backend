import { Department } from "../../profile/model/Department";

export interface Announcement {
  id: number;
  department: Department;
  publicationDate: Date;
  notice: string;
}
