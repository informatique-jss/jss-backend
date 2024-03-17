import { OsirisLog } from "../../administration/model/OsirisLog";
import { BatchSettings } from "./BatchSettings";
import { BatchStatus } from "./BatchStatus";

export interface Batch {
  id: number;
  batchSettings: BatchSettings;
  createdDate: Date;
  startDate: Date;
  endDate: Date;
  entityId: number;
  batchStatus: BatchStatus;
  node: Node;
  osirisLog: OsirisLog;
}
