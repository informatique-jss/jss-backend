import { ActiveDirectoryGroup } from "../../miscellaneous/model/ActiveDirectoryGroup";
import { Employee } from "../../profile/model/Employee";
import { ITiers } from '../../tiers/model/ITiers';
import { Affaire } from "./Affaire";
import { CustomerOrderStatus } from './CustomerOrderStatus';
import { OrderingSearch } from "./OrderingSearch";

export interface OrderingSearchTagged extends OrderingSearch {
  activeDirectoryGroup: ActiveDirectoryGroup;
  isOnlyDisplayUnread: boolean;
}
