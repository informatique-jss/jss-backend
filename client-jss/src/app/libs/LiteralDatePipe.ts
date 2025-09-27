import { Pipe, PipeTransform } from "@angular/core";

@Pipe({ name: 'literalDatePipe', standalone: true },)
export class LiteralDatePipe implements PipeTransform {
  transform(value: string | Date): string {
    const d = new Date(value);
    const now = new Date();

    const options: Intl.DateTimeFormatOptions =
      d.getFullYear() === now.getFullYear()
        ? { weekday: 'long', day: 'numeric', month: 'long' }
        : { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' };

    return new Intl.DateTimeFormat('fr-FR', options).format(d);
  }
}
