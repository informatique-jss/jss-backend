import { Post } from "./Post";
import { ReadingFolder } from "./ReadingFolder";

export interface AssoMailPost {
  id: number;
  post: Post;
  readingFolder: ReadingFolder;
}
