import { BatchSettings } from "./BatchSettings";
import { BatchStatus } from "./BatchStatus";
import { Node } from "./Node";

export interface BatchSearch {
  startDate: Date;
  endDate: Date;
  batchSettings: BatchSettings[];
  batchStatus: BatchStatus[];
  nodes: Node[];
  entityId: number;
}
