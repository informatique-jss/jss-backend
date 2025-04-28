export interface IWorkflowElement<T> {
  id: number | undefined;
  successors: IWorkflowElement<T>[];
  predecessors: IWorkflowElement<T>[];
  label: string;
  icon: string;
  code: string;
  isOpenState: boolean;
  isCloseState: boolean;
  aggregateStatus: string;

  // for front kanban purpose
  entities: T[];
}

