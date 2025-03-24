import { Post } from '../../../../../../client-jss/src/app/main/model/Post';
import { Mail } from "../../miscellaneous/model/Mail";

export interface Comment {
  id: number;
  mail: Mail;
  parentComment: Comment;
  post: Post;
  content: string;
  authorFirstName: string;
  authorLastName: string;
  creationDate: Date;
  isDeleted: boolean;
}
