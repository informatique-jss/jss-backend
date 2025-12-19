import { ActiveDirectoryGroup } from "../../profile/model/ActiveDirectoryGroup";
import { Country } from "../../profile/model/Country";

export interface Constant {
  id: number;
  activeDirectoryGroupSales: ActiveDirectoryGroup;
  activeDirectoryGroupFormalites: ActiveDirectoryGroup;
  countryFrance: Country;
}


export const globalConstantCache: {
  data: Constant | undefined;
} = { data: undefined };
