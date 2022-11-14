export interface IWorkflowElement {
  id: number | undefined;
  successors: IWorkflowElement[];
  predecessors: IWorkflowElement[];
  label: string;
  icon: string;
  code: string;
  isOpenState: boolean;
  isCloseState: boolean;
}

