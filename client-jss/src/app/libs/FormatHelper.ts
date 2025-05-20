export function getTimeReading(html: string): string {
  return Math.ceil(html.replace(/<[^>]+>/g, '').split(' ').length / 220) + " min";
}


export function capitalize(word: string) {
  if (!word) return word;
  return word[0].toUpperCase() + word.substr(1).toLowerCase();
}

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
