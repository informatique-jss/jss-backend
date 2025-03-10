import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'explaination-video',
  templateUrl: './explaination-video.component.html',
  styleUrls: ['./explaination-video.component.css']
})
export class ExplainationVideoComponent implements OnInit {
  @Input() title: string = '';
  @Input() firstStepTitle: string = '';
  @Input() secondStepTitle: string = '';
  @Input() thirdStepTitle: string = '';
  @Input() firstStepDescription: string = '';
  @Input() secondStepDescription: string = '';
  @Input() thirdStepDescription: string = '';

  isPlaying: boolean = false;
  currentTime: number = 0;
  videoPlaying = false;

  constructor() { }
  @ViewChild('videoPlayer') videoPlayer: ElementRef | undefined;

  ngOnInit() {
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
    if (!this.isPlaying) {
      this.playVideo();
    }
    if (seconds && this.videoPlayer)
      this.videoPlayer.nativeElement.currentTime = seconds;
  }
}
