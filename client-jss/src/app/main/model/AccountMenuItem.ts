import { MenuItem } from "./MenuItem";

export const MAIN_ITEM_ACCOUNT: string = "MAIN_ITEM_ACCOUNT";
export const MAIN_ITEM_DASHBOARD: string = "MAIN_ITEM_DASHBOARD";

export interface AccountMenuItem extends MenuItem {
  mainItem: string;
}

