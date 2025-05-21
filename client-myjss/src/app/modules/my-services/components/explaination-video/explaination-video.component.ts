import { Component, ElementRef, HostListener, Input, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'explaination-video',
  templateUrl: './explaination-video.component.html',
  styleUrls: ['./explaination-video.component.css'],
  standalone: false
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

  constructor() { }
  @ViewChild('videoPlayer') videoPlayer: ElementRef | undefined;

  ngOnInit() {
    this.checkIfMobile();
  }

  @HostListener('window:resize')
  onResize() {
    this.checkIfMobile();
  }

  private checkIfMobile(): void {
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
