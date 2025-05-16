import { Mail } from "../modules/profile/model/Mail";
import { Phone } from "../modules/profile/model/Phone";

export function capitalizeName(name: string): string {
  if (!name) return "";

  const lowerCaseWords = new Set([
    "de", "du", "des", "le", "la", "les", "l'", "d'", "au", "aux", "et"
  ]);

  return name
    .toLocaleLowerCase("fr-FR")
    .split(/(\s+|-)/)
    .map((word, index, arr) => {
      const trimmed = word.trim();
      const isSeparator = /^\s+|-$/u.test(word);
      if (isSeparator || trimmed === "") return word;

      const isFirst = index === 0 || /^\s*[-]/.test(arr[index - 1]);

      if (!isFirst && lowerCaseWords.has(trimmed)) return trimmed;

      return trimmed.charAt(0).toUpperCase() + trimmed.slice(1);
    })
    .join("");
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

export function formatDate(date: Date) {
  date = new Date(date);
  return [
    padTo2Digits(date.getDate()),
    padTo2Digits(date.getMonth() + 1),
    date.getFullYear(),
  ].join('-');
}

export function getTimeReading(html: string): string {
  return Math.ceil(html.replace(/<[^>]+>/g, '').split(' ').length / 220) + " min";
}
