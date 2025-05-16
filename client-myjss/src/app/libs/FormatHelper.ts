import { Mail } from "../modules/profile/model/Mail";
import { Phone } from "../modules/profile/model/Phone";

export function capitalizeName(name: string): string {
  if (name)
    return name.toLowerCase().replace(/\b(\w)/g, s => s.toUpperCase());
  return "";
}

export function getListMails(mails: Mail[]) {
  if (mails)
    return mails.map(mail => mail.mail).join(", ");
  return "";
}

export function getListPhones(phones: Phone[]) {
  if (phones)
    return phones.map(phones => phones.phoneNumber).join(", ");
  return "";
}

export function formatBytes(bytes: number, decimals: number) {
  if (bytes === 0) {
    return '0 Bytes';
  }
  const k = 1024;
  const dm = decimals <= 0 ? 0 : decimals || 2;
  const sizes = ['Octets', 'Ko', 'Mo', 'Go', 'To', 'Po', 'Eo', 'Zo', 'Yo'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
}

export function padTo2Digits(num: number) {
  return num.toString().padStart(2, '0');
}

export function formatDateFrance(date: Date) {
  if (!(date instanceof Date))
    date = new Date(date);
  return [
    padTo2Digits(date.getDate()),
    padTo2Digits(date.getMonth() + 1),
    date.getFullYear(),
  ].join('/');
}

export function formatDateHourFrance(dateInput: Date) {
  const date = new Date(dateInput);

  const day = padTo2Digits(date.getDate());
  const month = padTo2Digits(date.getMonth() + 1);
  const year = date.getFullYear();
  const hours = padTo2Digits(date.getHours());
  const minutes = padTo2Digits(date.getMinutes());

  return `${day}/${month}/${year} ${hours}:${minutes}`;
}

export function formatDate(date: Date) {
  date = new Date(date);
  return [
    padTo2Digits(date.getDate()),
    padTo2Digits(date.getMonth() + 1),
    date.getFullYear(),
  ].join('-');
}
