import { AfterViewInit, Component, Input, OnInit } from '@angular/core';
import { debounceTime, fromEvent, Subject, takeUntil } from 'rxjs';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { PlatformService } from '../../../main/services/platform.service';

@Component({
  selector: 'description-my-account',
  templateUrl: './description-my-account.component.html',
  styleUrls: ['./description-my-account.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class DescriptionMyAccountComponent implements OnInit, AfterViewInit {

  isMobile: boolean = false;

  @Input() imageSrc: string = "";

  private eventResize = new Subject<void>();

  constructor(private platformService: PlatformService) { }

  ngOnInit() {
    this.checkIfMobile();
  }

  ngOnDestroy(): void {
    this.eventResize.next();
    this.eventResize.complete();
  }

  ngAfterViewInit() {
    if (this.platformService.isServer()) return;

    fromEvent(window, 'resize')
      .pipe(debounceTime(200), takeUntil(this.eventResize))
      .subscribe(() => {
        this.checkIfMobile();
      });
  }

  private checkIfMobile(): void {
    if (this.platformService.isServer()) return;
    this.isMobile = window.innerWidth <= 768;
  }
}
