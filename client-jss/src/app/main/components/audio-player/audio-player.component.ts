import { Component, OnInit } from '@angular/core';
import { AudioService } from '../../services/audio.service';

@Component({
  selector: 'audio-player',
  templateUrl: './audio-player.component.html',
  styleUrls: ['./audio-player.component.css'],
  standalone: false
})
export class AudioPlayerComponent implements OnInit {

  volumeDropdownOpen = false;
  private volumeHoverTimeout: any = null;

  volumePreviousValue: number = 50;

  constructor(public audioService: AudioService) { }

  ngOnInit(): void {
  }

  togglePlayPause() {
    this.audioService.togglePlayPause();
  }

  forward15Secs() {
    this.audioService.addTime(15);
  }

  backward15Secs() {
    this.audioService.addTime(-15);
  }

  onSeek(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.audioService.seekTo(+value);
  }

  onVolumeChange(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.audioService.setVolume(+value);
    this.volumePreviousValue = this.audioService.volume;
  }

  onVolumeMouseEnter() {
    clearTimeout(this.volumeHoverTimeout);
    this.volumeDropdownOpen = true;
  }

  onVolumeMouseLeave() {
    this.volumeHoverTimeout = setTimeout(() => {
      this.volumeDropdownOpen = false;
    }, 150);
  }

  toggleMute() {
    clearTimeout(this.volumeHoverTimeout);

    const isMuted = this.audioService.volume === 0;
    this.audioService.setVolume(isMuted ? this.volumePreviousValue : 0);
  }
}
