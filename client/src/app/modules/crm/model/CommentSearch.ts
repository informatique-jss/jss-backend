import { IndexEntity } from "src/app/routing/search/IndexEntity";

export interface CommentSearch {
    post: IndexEntity;
    content: string;
    authorFirstLastName: string;
    creationDate: Date;
    isDeleted: boolean;
    isModerated: boolean;
}
