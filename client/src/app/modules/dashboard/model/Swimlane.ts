export interface Swimlane<T> {
  rawLabel: string;
  label: string;
  status: T[];
  aggregatedStatus: T[];
  totalItems: number;
  isCollapsed: boolean;
}
