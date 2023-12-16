export interface BatchSettings {
  id: number;
  code: string;
  label: string;
  queueSize: number;
  fixedRate: number;
  maxAddedNumberPerIteration: number;
  isActive: boolean;
}
