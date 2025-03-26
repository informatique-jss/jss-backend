import { Mail } from "../../profile/model/Mail";
import { Post } from "./Post";

export interface Comment {
    id: number;
    mailAdress: string;
    mail: Mail;
    parentComment: Comment;
    childrenComments: Comment[];
    post: Post;
    content: string;
    authorFirstName: string;
    authorLastName: string;
    creationDate: Date;
    isDeleted: boolean;
}
