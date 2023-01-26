import { IProvisionStatusSub } from "./IProvisionStatusSub";

export interface IProvisionStatus {
  id: number | undefined;
  successors: IProvisionStatusSub[];
  predecessors: IProvisionStatusSub[];
  label: string;
  icon: string;
  code: string;
  isOpenState: boolean;
  isCloseState: boolean;
}

