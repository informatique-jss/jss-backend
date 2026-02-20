import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
  name: 'literalDateTimePipe',
  standalone: true
})
export class LiteralDateTimePipe implements PipeTransform {
  transform(value: string | Date | null | undefined): string {
    if (!value) return '';

    const d = new Date(value);
    const now = new Date();

    const dateOptions: Intl.DateTimeFormatOptions =
      d.getFullYear() === now.getFullYear()
        ? { weekday: 'long', day: 'numeric', month: 'long' }
        : { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' };

    const datePart = new Intl.DateTimeFormat('fr-FR', dateOptions).format(d);

    const timePart = new Intl.DateTimeFormat('fr-FR', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    }).format(d);

    // Assemblage final
    return `${datePart} à ${timePart}`;
  }
}
