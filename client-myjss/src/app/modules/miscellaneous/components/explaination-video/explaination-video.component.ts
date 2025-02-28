import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';

@Component({
  selector: 'explaination-video',
  templateUrl: './explaination-video.component.html',
  styleUrls: ['./explaination-video.component.css']
})
export class ExplainationVideoComponent implements OnInit {

  @ViewChild('videoPlayer') videoElementRef!: ElementRef;
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
      video.classList.remove('d-none'); // Afficher la vidéo
      video.play(); // Lancer la lecture de la vidéo
      this.videoPlaying = true;
    }
  }

  jumpTo(seconds: number) {
    if (!this.isPlaying) {
      this.playVideo();
    }
    if (seconds)
      this.videoElementRef.nativeElement.currentTime = seconds;
  }
}
