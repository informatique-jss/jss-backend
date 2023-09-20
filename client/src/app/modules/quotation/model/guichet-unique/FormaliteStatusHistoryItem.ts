import { Status } from "./referentials/Status";

export interface FormaliteStatusHistoryItem {
  id: number;
  sourceStatus: Status;
  status: Status;
}
