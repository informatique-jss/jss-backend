export function capitalizeName(name: string): string {
  if (name)
    return name.toLowerCase().replace(/\b(\w)/g, s => s.toUpperCase());
  return "";
}
