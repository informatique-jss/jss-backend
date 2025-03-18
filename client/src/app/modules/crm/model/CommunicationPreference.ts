import { Mail } from "../../miscellaneous/model/Mail";


export interface CommunicationPreference {
    id: number;
    mail: Mail;
    isSubscribedToNewspaperNewletter: boolean;
    isSubscribedToCorporateNewsletter: boolean;
}
