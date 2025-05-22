import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Post } from '../model/Post';
import { PostService } from './post.service';
import { UserPreferenceService } from './user.preference.service';

@Injectable({
  providedIn: 'root'
})
export class AudioService {

  private audio = new Audio();
  public isPlaying: boolean = false;
  public currentTime: number = 0;
  public duration: number = 0;
  public volume: number = 0.5;
  public progress: number = 0;

  private isCurrentPodcastDisplayed: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  currentPodcastObservable = this.isCurrentPodcastDisplayed.asObservable();
  currentPodcast: Post | undefined;

  constructor(private postService: PostService,
    private userPreferenceService: UserPreferenceService
  ) {
    if (this.userPreferenceService.getCurrentPlayingTrackVolume()) {
      this.audio.volume = parseInt(this.userPreferenceService.getCurrentPlayingTrackVolume()!);
      this.volume = parseInt(this.userPreferenceService.getCurrentPlayingTrackVolume()!);
    } else {
      this.audio.volume = this.volume;
      userPreferenceService.setCurrentPlayingTrackVolume(this.volume);
    }

    // If already connected, we look in local storage to make it play where the user left it
    if (userPreferenceService.getCurrentPlayingTrack() && userPreferenceService.getCurrentPlayingTrackTime()) {
      this.postService.getPostById(parseInt(userPreferenceService.getCurrentPlayingTrack()!)).subscribe(res => {
        this.setTrackProperties(res);
        this.audio.currentTime = parseInt(userPreferenceService.getCurrentPlayingTrackTime()!);
        this.currentTime = this.audio.currentTime;
      });
    }

    this.audio.addEventListener('timeupdate', () => {
      this.currentTime = this.audio.currentTime;
      userPreferenceService.setCurrentPlayingTrackTime(this.currentTime - 5);
      this.duration = this.audio.duration;
      this.progress = (this.audio.currentTime / this.audio.duration) * 100;
    });

    this.audio.addEventListener('ended', () => {
      this.isPlaying = false;
    });
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
    this.currentPodcast = res;
    this.isCurrentPodcastDisplayed.next(true);
    this.audio.src = res.podcastUrl;
    this.audio.load();
    this.userPreferenceService.setCurrentPlayingTrack(res.id);
  }

  unloadTrackAndClose() {
    this.audio.pause();
    this.currentPodcast = undefined;
    this.isCurrentPodcastDisplayed.next(false);
    this.audio.src = "";
    this.userPreferenceService.deleteAudioPreferences();
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

  // When using the progress bar
  seekTo(value: number) {
    if (this.audio.duration) {
      this.audio.currentTime = (value / 100) * this.audio.duration;
    }
  }

  // If needs to go at a specific moment of the audio
  addTime(value: number) {
    if (value) {
      this.audio.currentTime = this.currentTime + value;
    }
  }

  setVolume(value: number) {
    this.volume = value;
    this.audio.volume = value;
    this.userPreferenceService.setCurrentPlayingTrackVolume(value);
  }
}
