import { Post } from '../../../../../../client-myjss/src/app/modules/tools/model/Post';
import { Mail } from "../../miscellaneous/model/Mail";

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
