
export interface Node {
  id: number;
  hostname: string;
  batchNodePriority: number;
  lastAliveDatetime: Date;
  freeSpace: number;
  totalSpace: number;
  freeMemory: number;
  totalMemory: number;
  cpuLoad: number;
}
