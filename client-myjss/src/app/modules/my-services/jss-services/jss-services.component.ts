import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'jss-services',
  templateUrl: './jss-services.component.html',
  styleUrls: ['./jss-services.component.css']
})
export class JssServicesComponent implements OnInit {
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
}
