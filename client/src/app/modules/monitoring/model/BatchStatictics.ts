export interface BatchStatistics {
  idBatchSettings: number;
  success: number;
  waiting: number;
  running: number;
  error: number;
  acknowledge: number;
  standardMeanTime: number;
  currentMeanTime: number;
}
