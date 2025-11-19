import { ActiveDirectoryGroup } from "../../profile/model/ActiveDirectoryGroup";

export interface Constant {
  id: number;
  activeDirectoryGroupSales: ActiveDirectoryGroup;
}


export const globalConstantCache: {
  data: Constant | undefined;
} = { data: undefined };
