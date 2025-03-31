import { Mail } from "../../profile/model/Mail";

export interface Comment {
    id: number;
    mail: Mail;
    childrenComments: Comment[];
    content: string;
    authorFirstName: string;
    authorLastName: string;
    authorLastNameInitials: string;
    creationDate: Date;
}
