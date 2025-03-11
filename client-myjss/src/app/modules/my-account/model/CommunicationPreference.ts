import { Mail } from "../../profile/model/Mail";


export interface CommunicationPreference {
    id: number;
    mail: Mail;
    isSubscribedToNewspaperNewletter: boolean;
    isSubscribedToCorporateNewsletter: boolean;
}
