import { ActiveDirectoryGroup } from "../../miscellaneous/model/ActiveDirectoryGroup";
import { OrderingSearch } from "./OrderingSearch";

export interface OrderingSearchTagged extends OrderingSearch {
  activeDirectoryGroup: ActiveDirectoryGroup;
  isOnlyDisplayUnread: boolean;
}
