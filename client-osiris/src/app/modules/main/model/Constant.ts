
export interface Constant {
  id: number;
}


export const globalConstantCache: {
  data: Constant | undefined;
} = { data: undefined };
