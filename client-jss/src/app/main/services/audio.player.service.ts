import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { PlatformService } from '../../services/platform.service';
import { Post } from '../model/Post';
import { PostService } from './post.service';
import { UserPreferenceService } from './user.preference.service';

@Injectable({
  providedIn: 'root'
})
export class AudioPlayerService {

  private audio: HTMLAudioElement | null = null;
  public isPlaying: boolean = false;
  public currentTime: number = 0;
  public duration: number = 0;
  public volume: number = 0.5;
  public progress: number = 0;

  private currentPodcastId: BehaviorSubject<number> = new BehaviorSubject<number>(0);
  currentPodcastObservable = this.currentPodcastId.asObservable();
  currentPodcast: Post | undefined;

  constructor(private postService: PostService,
    private userPreferenceService: UserPreferenceService,
    private platformService: PlatformService
  ) {
    this.audio = platformService.getNativeAudio();
    if (this.audio) {
      const currentPlayingTrackVolume = this.userPreferenceService.getCurrentPlayingTrackVolume();
      if (currentPlayingTrackVolume) {
        this.audio.volume = parseFloat(currentPlayingTrackVolume);
        this.volume = parseFloat(currentPlayingTrackVolume);
      } else {
        this.audio.volume = this.volume;
        userPreferenceService.setCurrentPlayingTrackVolume(this.volume);
      }

      // If already connected, we look in local storage to make it play where the user left it
      if (userPreferenceService.getCurrentPlayingTrack() && userPreferenceService.getCurrentPlayingTrackTime()) {
        const currentPlayingTrackId = userPreferenceService.getCurrentPlayingTrack();
        if (currentPlayingTrackId)
          this.postService.getPostById(parseInt(currentPlayingTrackId)).subscribe(res => {
            this.setTrackProperties(res);
            this.duration = res.mediaTimeLength;
            const currentPlayingTrackTime = userPreferenceService.getCurrentPlayingTrackTime();
            this.audio!.currentTime = parseFloat(currentPlayingTrackTime ? currentPlayingTrackTime : '0');
            this.currentTime = this.audio!.currentTime;
          });
      }

      this.audio.addEventListener('timeupdate', () => {
        if (this.audio) {
          this.currentTime = this.audio.currentTime;
          userPreferenceService.setCurrentPlayingTrackTime(this.currentTime - 5);
          this.duration = this.audio.duration;
          this.progress = (this.audio.currentTime / this.audio.duration) * 100;
        }
      });

      this.audio.addEventListener('ended', () => {
        this.isPlaying = false;
      });
    }
  }

  // To know if the specific podcast is been played
  isPlayingPodcast(post: Post) {
    if (this.currentPodcast && post)
      return this.isPlaying && this.currentPodcast.id == post.id;

    return false
  }

  loadTrack(podcastId: number) {
    this.postService.getPostById(podcastId).subscribe(res => {
      this.setTrackProperties(res);
      this.play();
    });
  }

  private setTrackProperties(res: Post) {
    if (this.audio) {
      this.currentPodcast = res;
      this.currentPodcastId.next(res.id);
      this.audio.src = res.podcastUrl;
      this.audio.load();
      this.duration = res.mediaTimeLength;
      this.userPreferenceService.setCurrentPlayingTrack(res.id);
    }
  }

  unloadTrackAndClose() {
    if (this.audio) {
      this.audio.pause();
      this.currentPodcast = undefined;
      this.currentPodcastId.next(0);
      this.audio.src = "";
      this.userPreferenceService.deleteAudioPreferences();
    }
  }

  togglePlayPause() {
    this.isPlaying ? this.pause() : this.play();
  }

  play() {
    if (this.audio)
      this.audio.play();
    this.isPlaying = true;
  }

  pause() {
    if (this.audio)
      this.audio.pause();
    this.isPlaying = false;
  }

  // When using the progress bar
  seekTo(value: number) {
    if (this.audio)
      if (this.audio.duration) {
        this.audio.currentTime = (value / 100) * this.audio.duration;
      }
  }

  // If needs to go at a specific moment of the audio
  addTime(value: number) {
    if (this.audio)
      if (value) {
        this.audio.currentTime = this.currentTime + value;
      }
  }

  getVolume() {
    return this.volume;
  }

  setVolume(value: number) {
    this.volume = value;
    if (this.audio)
      this.audio.volume = value;
    this.userPreferenceService.setCurrentPlayingTrackVolume(value);
  }

  getCurrentPodcast() {
    return this.currentPodcast
  }

  getIsPlaying() {
    return this.isPlaying;
  }

  getCurrentTime() {
    return this.currentTime;
  }

  getProgress() {
    return this.progress;
  }

  getDuration() {
    return this.duration;
  }
}
