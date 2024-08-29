import { ActiveDirectoryGroup } from "../../miscellaneous/model/ActiveDirectoryGroup";
import { OrderingSearchResult } from "./OrderingSearchResult";

export interface OrderingSearchTaggedResult extends OrderingSearchResult {
  activeDirectoryGroupLabel: string;
}
