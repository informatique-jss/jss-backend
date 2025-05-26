import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { Post } from '../../model/Post';
import { AudioPlayerService } from '../../services/audio.player.service';

@Component({
  selector: 'audio-player',
  templateUrl: './audio-player.component.html',
  styleUrls: ['./audio-player.component.css'],
  imports: [SHARED_IMPORTS],
  standalone: true
})
export class AudioPlayerComponent implements OnInit, AfterViewInit {

  @ViewChild('scrollTextZone') scrollTextRef!: ElementRef;
  containerSize: number = 50;

  volumeDropdownOpen = false;
  private volumeHoverTimeout: any = null;

  menuDropdownOpen = false;

  volumeCurrentValue: number = 0.5;
  volumePreviousValue: number = 0.5;
  currentPodcast: Post | undefined;
  isPlaying: boolean = false;
  currentTime: number = 0;
  progress: number = 0;
  duration: number = 0;

  constructor(private audioPlayerService: AudioPlayerService,
    private appService: AppService,
    private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.volumeCurrentValue = this.getVolume();
    this.volumePreviousValue = 0.5;
    this.currentPodcast = this.getCurrentPodcast();
    this.isPlaying = this.getIsPlaying();
    this.currentTime = this.getCurrentTime();
    this.progress = this.getProgress();
    this.duration = this.getDuration();
  }

  ngAfterViewInit(): void {
    this.setScrollSize();
  }

  setScrollSize(): void {
    const el = this.scrollTextRef.nativeElement as HTMLElement;
    this.containerSize = el.clientWidth;
    this.cdr.detectChanges();
  }

  togglePlayPause() {
    this.audioPlayerService.togglePlayPause();
  }

  forwardFifteenSecs() {
    this.audioPlayerService.addTime(15);
  }

  backwardFifteenSecs() {
    this.audioPlayerService.addTime(-15);
  }

  onSeek(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.audioPlayerService.seekTo(+value);
  }

  onVolumeChange(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.audioPlayerService.setVolume(+value);
    this.synchComponentVolumes();
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
    if (this.volumePreviousValue === 0) {
      this.volumePreviousValue = 0.2;
    }

    this.audioPlayerService.setVolume(this.getVolume() === 0 ? this.volumePreviousValue : 0);
    this.volumeCurrentValue = this.getVolume();
  }

  synchComponentVolumes() {
    this.volumeCurrentValue = this.getVolume();
    this.volumePreviousValue = this.volumeCurrentValue;
  }

  onOpenMenu() {
    this.menuDropdownOpen === false ? this.menuDropdownOpen = true : this.menuDropdownOpen = false;
  }

  closePlayer() {
    this.audioPlayerService.unloadTrackAndClose();
  }

  openPodcasts(event: any) {
    this.appService.openRoute(event, "podcasts", undefined);
  }

  getVolume() {
    return this.audioPlayerService.getVolume();
  }

  getCurrentPodcast() {
    return this.audioPlayerService.getCurrentPodcast();
  }

  getIsPlaying() {
    return this.audioPlayerService.getIsPlaying();
  }

  getCurrentTime() {
    return this.audioPlayerService.getCurrentTime();
  }

  getProgress() {
    return this.audioPlayerService.getProgress();
  }

  getDuration() {
    return this.audioPlayerService.getDuration();
  }
}
