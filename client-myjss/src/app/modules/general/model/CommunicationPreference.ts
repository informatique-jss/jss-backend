import { Mail } from "./Mail";

export interface CommunicationPreference {
  id: number;
  mail: Mail;
  isSubscribedToNewspaperNewletter: boolean;
  isSubscribedToCorporateNewsletter: boolean;
}
