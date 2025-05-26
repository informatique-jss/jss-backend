import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { PlatformService } from '../services/platform.service';

@Pipe({
  name: 'trustHtml',
  pure: true,
  standalone: true
})
export class TrustHtmlPipe implements PipeTransform {

  constructor(private sanitizer: DomSanitizer, private platformService: PlatformService) {
  }

  transform(pUnsafe: string) {
    return this.sanitizer.bypassSecurityTrustHtml(pUnsafe);
  }
}
