import { Team } from "./Team";

export interface Employee {
  id: number;
  firstname: string;
  lastname: string;
  team: Team;
}
