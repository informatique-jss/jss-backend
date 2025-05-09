export interface Swimlane<T> {
  id: number;
  rawLabel: string;
  label: string;
  status: T[];
  aggregatedStatus: T[];
  totalItems: number;
  isCollapsed: boolean;
}
