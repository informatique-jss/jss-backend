import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { Post } from '../../model/Post';
import { AudioPlayerService } from '../../services/audio.player.service';
import { PostService } from '../../services/post.service';

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

  progress: number = 0;
  progressSubscription: Subscription = new Subscription;


  menuDropdownOpen = false;

  volumePreviousValue: number = 0.5;

  constructor(private audioPlayerService: AudioPlayerService,
    private appService: AppService,
    private postService: PostService,
    private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    this.volumePreviousValue = 0.5;

    this.progressSubscription = this.audioPlayerService.progressObservable.subscribe(item => {
      this.progress = item;
      this.cdr.detectChanges();
    }
    );
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

  changeSpeechRate() {
    this.audioPlayerService.changeSpeechRate();
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
  }

  synchComponentVolumes() {
    this.volumePreviousValue = this.getVolume();
  }

  onOpenMenu() {
    this.menuDropdownOpen === false ? this.menuDropdownOpen = true : this.menuDropdownOpen = false;
  }

  closePlayer() {
    this.audioPlayerService.unloadTrackAndClose();
  }

  openPost(id: number, event: any) {
    this.postService.getPostById(id).subscribe(res => {
      if (res && res.podcastUrl) {
        this.appService.openRoute(event, "podcasts", undefined);
      } else if (res && res.id) {
        this.appService.openRoute(event, "post/" + res.slug, undefined);
      }
    });
  }

  getVolume() {
    return this.audioPlayerService.getVolume();
  }

  getCurrentPost() {
    return this.audioPlayerService.getCurrentPost();
  }

  getIsPlaying() {
    return this.audioPlayerService.getIsPlaying();
  }

  getCurrentTime() {
    return this.audioPlayerService.getCurrentTime();
  }

  getDuration() {
    return this.audioPlayerService.getDuration();
  }

  getSpeechRate(): number {
    return this.audioPlayerService.getSpeechRate();
  }

  getIsPodcast(post: Post | undefined) {
    if (post)
      return this.audioPlayerService.isAudioPodcast(post);

    return null;
  }
}
