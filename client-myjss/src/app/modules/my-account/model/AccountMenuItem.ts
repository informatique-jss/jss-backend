export const MAIN_ITEM_ACCOUNT: string = "MAIN_ITEM_ACCOUNT";
export const MAIN_ITEM_DASHBOARD: string = "MAIN_ITEM_DASHBOARD";

export interface AccountMenuItem {
  mainItem: string;
  label: string;
  iconClass: string;
  route: string;
}

