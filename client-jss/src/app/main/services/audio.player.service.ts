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


  private audio: HTMLAudioElement = new Audio();
  public isPlaying: boolean = false;
  public currentTime: number = 0;
  public duration: number = 0;
  public volume: number = 0.5;
  public progress: number = 0;

  private isCurrentPodcastDisplayed: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  currentPodcastObservable = this.isCurrentPodcastDisplayed.asObservable();
  currentPodcast: Post | undefined;

  constructor(private postService: PostService,
    private userPreferenceService: UserPreferenceService,
    private platformService: PlatformService
  ) {
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
      const currentPlayingTrack = userPreferenceService.getCurrentPlayingTrack();
      if (currentPlayingTrack)
        this.postService.getPostById(parseInt(currentPlayingTrack)).subscribe(res => {
          this.setTrackProperties(res);
          const currentPlayingTrackTime = userPreferenceService.getCurrentPlayingTrackTime();
          this.audio.currentTime = parseFloat(currentPlayingTrackTime ? currentPlayingTrack : '0');
          this.currentTime = this.audio.currentTime;
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
      this.isCurrentPodcastDisplayed.next(true);
      this.audio.src = res.podcastUrl;
      this.audio.load();
      this.userPreferenceService.setCurrentPlayingTrack(res.id);
    }
  }

  unloadTrackAndClose() {
    if (this.audio) {
      this.audio.pause();
      this.currentPodcast = undefined;
      this.isCurrentPodcastDisplayed.next(false);
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
