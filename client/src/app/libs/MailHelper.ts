import { Employee } from '../modules/profile/model/Employee';
import { validateEmail, validateFrenchPhone, validateInternationalPhone } from "./CustomFormsValidatorsHelper";

export function prepareMail(mailAdress: string, subject: string | null, body: string | null) {
  if (mailAdress == null || !validateEmail(mailAdress))
    return;

  let mail = "mailto:" + mailAdress;

  if (subject != null)
    mail += "+?subject=" + subject;

  if (body != null) {
    if (subject == null)
      mail += "+?";
    mail += "&body=" + body;
  }
  window.location.href = mail;
}

export function callNumber(phoneNumber: string) {
  if (phoneNumber == null || (!validateFrenchPhone(phoneNumber) && !validateInternationalPhone(phoneNumber)))
    return;

  window.location.href = "tel:" + phoneNumber;
}

export function displayInTeams(employee: Employee) {
  window.location.href = "im:" + employee.mail;
}
