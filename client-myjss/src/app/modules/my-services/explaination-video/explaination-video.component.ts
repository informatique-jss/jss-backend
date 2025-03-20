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
  @Input() posterPath: string = '';
  @Input() videoPath: string = '';
  @Input() firstJumpTo: string = '';
  @Input() secondJumpTo: string = '';
  @Input() thirdJumpTo: string = '';


  isPlaying: boolean = false;
  currentTime: number = 0;
  videoPlaying = false;

  constructor() { }
  @ViewChild('videoPlayer') videoPlayer: ElementRef | undefined;

  ngOnInit() {
  }

  convertStringToInt(stringTime: string) {
    var Num = parseInt(stringTime);
    return Num;
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
