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

export function convertToRtf(plainText: string) {
  plainText = plainText.replace(/\n/g, "\\par\n");
  return "{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang2057{\\fonttbl{\\f0\\fnil\\fcharset0 Microsoft Sans Serif;}}\n\\viewkind4\\uc1\\pard\\f0\\fs17 " + plainText + "\\par\n}";
}
