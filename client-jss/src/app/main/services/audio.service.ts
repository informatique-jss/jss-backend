import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AudioService {

  private audio = new Audio();
  public isPlaying = false;
  public currentTime = 0;
  public duration = 0;
  public volume = 0.5;
  public progress = 0;

  public currentTrack: { title: string, subtitle: string, image: string, url: string } | null = null;

  constructor() {
    this.audio.volume = this.volume;

    this.audio.addEventListener('timeupdate', () => {
      this.currentTime = this.audio.currentTime;
      this.duration = this.audio.duration;
      this.progress = (this.audio.currentTime / this.audio.duration) * 100;
    });

    this.audio.addEventListener('ended', () => {
      this.isPlaying = false;
    });
  }

  loadTrack(track: { title: string, subtitle: string, image: string, url: string }) {
    this.currentTrack = track;
    this.audio.src = track.url;
    this.audio.load();
    this.play();
  }

  togglePlayPause() {
    this.isPlaying ? this.pause() : this.play();
  }

  play() {
    this.audio.play();
    this.isPlaying = true;
  }

  pause() {
    this.audio.pause();
    this.isPlaying = false;
  }

  seekTo(value: number) {
    if (this.audio.duration) {
      this.audio.currentTime = (value / 100) * this.audio.duration;
    }
  }

  setVolume(value: number) {
    this.volume = value;
    this.audio.volume = value;
  }

  skipNext() {
    // TODO : Gérer une playlist
    this.pause();
  }

  skipPrevious() {
    this.audio.currentTime = 0;
  }

}
