export function capitalizeName(name: string): string {
  return name.toLowerCase().replace(/\b(\w)/g, s => s.toUpperCase());
}
