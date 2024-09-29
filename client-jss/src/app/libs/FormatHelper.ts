export function getTimeReading(html: string): string {
  return Math.ceil(html.replace(/<[^>]+>/g, '').split(' ').length / 220) + " min";
}
