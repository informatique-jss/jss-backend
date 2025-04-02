import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'duration',
    standalone: false
})
export class TimeFormatPipe implements PipeTransform {

  transform(seconds: number): string {
    if (!seconds || seconds < 0) return '0min';

    const hours = Math.floor(seconds / 3600);
    const minutes = Math.ceil((seconds % 3600) / 60);

    let result = '';
    if (hours > 0) {
      result += `${hours} h `;
    }
    if (minutes > 0) {
      result += `${minutes} min`;
    }

    return result.trim();
  }
}
