import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { AppService } from '../../../services/app.service';
import { AudioService } from '../../services/audio.service';

@Component({
  selector: 'audio-player',
  templateUrl: './audio-player.component.html',
  styleUrls: ['./audio-player.component.css'],
  standalone: false
})
export class AudioPlayerComponent implements OnInit, AfterViewInit {

  @ViewChild('scrollText') scrollTextRef!: ElementRef;
  containerSize: number = 50;


  volumeDropdownOpen = false;
  private volumeHoverTimeout: any = null;

  menuDropdownOpen = false;

  volumePreviousValue: number = 50;

  constructor(public audioService: AudioService,
    private appService: AppService,
    private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.checkScroll();
  }

  checkScroll(): void {
    const el = this.scrollTextRef.nativeElement as HTMLElement;
    this.containerSize = el.clientWidth;
    this.cdr.detectChanges();
  }

  togglePlayPause() {
    this.audioService.togglePlayPause();
  }

  forwardFifteenSecs() {
    this.audioService.addTime(15);
  }

  backwardFifteenSecs() {
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
    this.menuDropdownOpen = false;
    this.volumeDropdownOpen = true;
  }

  onVolumeMouseLeave() {
    this.volumeHoverTimeout = setTimeout(() => {
      this.volumeDropdownOpen = false;
    }, 150);
  }

  toggleMute() {
    clearTimeout(this.volumeHoverTimeout);

    this.audioService.setVolume(this.audioService.volume === 0 ? this.volumePreviousValue : 0);
  }

  onOpenMenu() {
    this.menuDropdownOpen === false ? this.menuDropdownOpen = true : this.menuDropdownOpen = false;
  }

  closePlayer() {
    this.audioService.unloadTrackAndClose();
  }

  openPodcasts(event: any) {
    this.appService.openRoute(event, "podcasts", undefined);
  }
}
