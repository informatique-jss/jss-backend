import { Mail } from "../../miscellaneous/model/Mail";
import { Post } from "./Post";

export interface Comment {
  id: number;
  mail: Mail;
  post: Post;
  content: string;
  authorFirstName: string;
  authorLastName: string;
  creationDate: Date;
  isDeleted: boolean;
  isModerated: boolean;
}
