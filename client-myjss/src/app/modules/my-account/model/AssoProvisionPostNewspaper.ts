import { Newspaper } from '../../../../../../client-jss/src/app/main/model/Newspaper';
import { Post } from "../../tools/model/Post";
import { Provision } from "./Provision";

export interface AssoProvisionPostNewspaper {
  id: number;
  provision: Provision;
  post: Post;
  newspaper: Newspaper;
}
