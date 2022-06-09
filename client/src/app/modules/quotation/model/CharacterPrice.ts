import { Department } from "../../miscellaneous/model/Department";

export interface CharacterPrice {
  id: number;
  price: number;
  departments: Department[];
  startDate: Date;
}

