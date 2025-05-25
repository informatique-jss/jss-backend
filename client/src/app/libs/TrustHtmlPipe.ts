import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

@Pipe({
  name: 'trustHtml',
  pure: true,
  standalone: true
})
export class TrustHtmlPipe implements PipeTransform {

  constructor(private sanitizer: DomSanitizer) {
  }

  transform(pUnsafe: string) {
    return this.sanitizer.bypassSecurityTrustHtml(pUnsafe);
  }
}
