
export function getPreviousYear(date: Date, offsetYear: number): Date {
  date = new Date(date);
  const year = date.getFullYear() - offsetYear;
  const month = date.getMonth();
  const day = date.getDate();

  const previousYearDate = new Date(year, month, day);

  if (previousYearDate.getMonth() !== month) {
    return new Date(year, month + 1, 0);
  }

  return previousYearDate;
}

