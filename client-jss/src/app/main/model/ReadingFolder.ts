import { Post } from "./Post";

export interface ReadingFolder {
  id: number;
  label: string;
  posts: Post[];
}
