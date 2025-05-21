import { Component, Input, OnInit } from '@angular/core';
import { AudioService } from '../../services/audio.service';

@Component({
  selector: 'audio-player',
  templateUrl: './audio-player.component.html',
  styleUrls: ['./audio-player.component.css'],
  standalone: false
})
export class AudioPlayerComponent implements OnInit {

  volumeDropdownOpen = false;

  @Input() isPlayToggled: boolean = false;


  constructor(public audioService: AudioService) { }

  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }

  togglePlayPause() {
    this.audioService.togglePlayPause();
  }

  forward15Secs() {
    this.audioService.skipNext();
  }

  backward15Secs() {
    this.audioService.skipPrevious();
  }

  onSeek(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.audioService.seekTo(+value);
  }

  onVolumeChange(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.audioService.setVolume(+value);
  }

  toggleVolumeDropdown(event: Event) {
    event.preventDefault();
    this.volumeDropdownOpen = !this.volumeDropdownOpen;
  }
}
