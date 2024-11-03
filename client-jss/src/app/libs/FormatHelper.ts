export function getTimeReading(html: string): string {
  return Math.ceil(html.replace(/<[^>]+>/g, '').split(' ').length / 220) + " min";
}


export function capitalize(word: string) {
  if (!word) return word;
  return word[0].toUpperCase() + word.substr(1).toLowerCase();
}
