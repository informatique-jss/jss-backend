import { SortTableColumn } from "../modules/miscellaneous/model/SortTableColumn";

export function padTo2Digits(num: number) {
  return num.toString().padStart(2, '0');
}

export function formatDate(date: Date) {
  date = new Date(date);
  return [
    padTo2Digits(date.getDate()),
    padTo2Digits(date.getMonth() + 1),
    date.getFullYear(),
  ].join('-');
}

export function toIsoString(date: Date) {
  const pad = function (num: number) {
    return (num < 10 ? '0' : '') + num;
  };

  return date.getFullYear() +
    '-' + pad(date.getMonth() + 1) +
    '-' + pad(date.getDate()) +
    'T' + pad(date.getHours()) +
    ':' + pad(date.getMinutes()) +
    ':' + pad(date.getSeconds()) +
    '.000Z';
}

export function formatEurosForSortTable<T>(element: T, column: SortTableColumn<T>): string {
  if (element && column && (getObjectPropertybyString(element, column.fieldName) || getObjectPropertybyString(element, column.fieldName) == 0) && !isNaN(getObjectPropertybyString(element, column.fieldName)))
    return (((getObjectPropertybyString(element, column.fieldName)).toFixed(2)) + "").replace(".", ",") + " €";
  return "";
}

export function formatPercentForSortTable<T>(element: T, column: SortTableColumn<T>): string {
  if (element && column && getObjectPropertybyString(element, column.fieldName) != null)
    return getObjectPropertybyString(element, column.fieldName) + " %";
  return "";
}

export function formatDateForSortTable<T>(element: T, column: SortTableColumn<T>): string {
  if (element && column && column.fieldName && getObjectPropertybyString(element, column.fieldName)) {
    let date = new Date(getObjectPropertybyString(element, column.fieldName));
    return formatDateFrance(date);
  }
  return "";
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

export function formatDateFrance(date: Date) {
  if (!(date instanceof Date))
    date = new Date(date);
  return [
    padTo2Digits(date.getDate()),
    padTo2Digits(date.getMonth() + 1),
    date.getFullYear(),
  ].join('/');
}

export function formatDateTimeFrance(date: Date) {
  if (!(date instanceof Date))
    date = new Date(date);
  return formatDateFrance(date) + " " + [
    padTo2Digits(date.getHours()),
    padTo2Digits(date.getMinutes()),
    padTo2Digits(date.getSeconds()),
  ].join(':');
}

export function formatDateTimeForSortTable<T>(element: T, column: SortTableColumn<T>): string {
  if (element && column && column.fieldName && getObjectPropertybyString(element, column.fieldName)) {
    let date = new Date(getObjectPropertybyString(element, column.fieldName));
    return formatDateTimeFrance(date);
  }

  return "";
}

export function getObjectPropertybyString(element: any, propertyPath: string) {
  propertyPath = propertyPath.replace(/\[(\w+)\]/g, '.$1'); // convert indexes to properties
  propertyPath = propertyPath.replace(/^\./, '');           // strip a leading dot
  var a = propertyPath.split('.');
  for (var i = 0, n = a.length; i < n; ++i) {
    var k = a[i];
    if (typeof element === "object" && k in element) {
      element = element[k];
    } else {
      return;
    }
  }
  return element;
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
