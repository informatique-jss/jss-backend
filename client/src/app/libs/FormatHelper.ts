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

export function formatEurosForSortTable(element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string {
  if (element && column && getObjectPropertybyString(element, column.fieldName) && !isNaN(getObjectPropertybyString(element, column.fieldName)))
    return Math.round(getObjectPropertybyString(element, column.fieldName) * 100) / 100 + " €";
  return "";
}

export function formatPercentForSortTable(element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string {
  if (element && column && getObjectPropertybyString(element, column.fieldName) != null)
    return getObjectPropertybyString(element, column.fieldName) + " %";
  return "";
}

export function formatDateForSortTable(element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string {
  if (element && column && column.fieldName && getObjectPropertybyString(element, column.fieldName)) {
    let date = new Date(getObjectPropertybyString(element, column.fieldName));
    return [
      padTo2Digits(date.getDate()),
      padTo2Digits(date.getMonth() + 1),
      date.getFullYear(),
    ].join('/');
  }
  return "";
}

export function formatDateTimeForSortTable(element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string {
  if (element && column && column.fieldName && getObjectPropertybyString(element, column.fieldName)) {
    let date = new Date(getObjectPropertybyString(element, column.fieldName));
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
