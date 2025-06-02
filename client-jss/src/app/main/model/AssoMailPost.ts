import { Mail } from "./Mail";
import { Post } from "./Post";

export interface AssoMailPost {
  id: number;
  mail: Mail;
  post: Post;
}
