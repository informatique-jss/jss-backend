import { SortTableColumn } from "../modules/miscellaneous/model/SortTableColumn";

export function padTo2Digits(num: number) {
  return num.toString().padStart(2, '0');
}

export function formatDate(date: Date) {
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

export function formatEurosForSortTable(element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string {
  if (element && column && element[column.fieldName] != null)
    return element[column.fieldName] + " €";
  return "";
}

export function formatDateForSortTable(element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string {
  if (element && column && column.fieldName && element[column.fieldName]) {
    let date = new Date(element[column.fieldName]);
    return [
      padTo2Digits(date.getDate()),
      padTo2Digits(date.getMonth() + 1),
      date.getFullYear(),
    ].join('/');
  }
  return "";
}

export function formatDateTimeForSortTable(element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string {
  if (element && column && column.fieldName && element[column.fieldName]) {
    let date = new Date(element[column.fieldName]);
    return [
      padTo2Digits(date.getDate()),
      padTo2Digits(date.getMonth() + 1),
      date.getFullYear(),
    ].join('/') + " " + [
      padTo2Digits(date.getHours()),
      padTo2Digits(date.getMinutes()),
      padTo2Digits(date.getSeconds()),
    ].join(':');
  }

  return "";
}
