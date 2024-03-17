export interface BatchStatistics {
  idBatchSettings: number;
  new: number;
  success: number;
  waiting: number;
  running: number;
  error: number;
  acknowledge: number;
  standardMeanTime: number;
  currentMeanTime: number;
}
