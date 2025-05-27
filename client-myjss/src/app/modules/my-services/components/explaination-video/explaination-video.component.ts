import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { Subject, debounceTime, fromEvent, takeUntil } from 'rxjs';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { PlatformService } from '../../../main/services/platform.service';

@Component({
  selector: 'explaination-video',
  templateUrl: './explaination-video.component.html',
  styleUrls: ['./explaination-video.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class ExplainationVideoComponent implements OnInit {
  @Input() title: string = '';
  @Input() firstStepTitle: string = '';
  @Input() secondStepTitle: string = '';
  @Input() thirdStepTitle: string = '';
  @Input() firstStepDescription: string = '';
  @Input() secondStepDescription: string = '';
  @Input() thirdStepDescription: string = '';
  @Input() posterPath: string = '';
  @Input() videoPath: string = '';
  @Input() firstJumpTo: number = 0;
  @Input() secondJumpTo: number = 0;
  @Input() thirdJumpTo: number = 0;


  isPlaying: boolean = false;
  currentTime: number = 0;
  videoPlaying = false;
  isMobile: boolean = false;

  private eventResize = new Subject<void>();

  constructor(private platformService: PlatformService) { }
  @ViewChild('videoPlayer') videoPlayer: ElementRef | undefined;

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

  playVideo(): void {
    if (this.videoPlayer) {
      const video = this.videoPlayer.nativeElement;
      video.classList.remove('d-none');
      video.play();
      this.videoPlaying = true;
    }
  }

  jumpTo(seconds: number) {
    if (!this.isPlaying && seconds) {
      this.playVideo();
    }
    if (seconds && this.videoPlayer)
      this.videoPlayer.nativeElement.currentTime = seconds;
  }
}
