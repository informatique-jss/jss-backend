import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { PlatformService } from '../../services/platform.service';
import { Post } from '../model/Post';
import { PostService } from './post.service';
import { UserPreferenceService } from './user.preference.service';

@Injectable({ providedIn: 'root' })
export class AudioPlayerService {

  private audio: HTMLAudioElement | null = null;
  private speechUtterance: SpeechSynthesisUtterance | undefined;

  private ttsWords: string[] = [];
  private ttsText: string = '';
  private speechRate = 1;
  private isPlaying = false;
  private currentTime = 0;
  private duration = 0;
  private volume = 0.5;
  private progress = 0;

  private currentPostId = new BehaviorSubject<number>(0);
  currentPostObservable = this.currentPostId.asObservable();
  currentPost: Post | undefined;

  private progressSubject: BehaviorSubject<number> = new BehaviorSubject<number>(0);
  progressObservable = this.progressSubject.asObservable();

  constructor(
    private postService: PostService,
    private userPref: UserPreferenceService,
    private platform: PlatformService
  ) {
    this.audio = this.platform.getNativeAudio();
    this.initAudio();
  }

  private initAudio(): void {
    if (!this.audio) return;

    const savedVolume = this.userPref.getCurrentPlayingTrackVolume();
    this.volume = savedVolume ? parseFloat(savedVolume) : 0.5;
    this.audio.volume = this.volume;
    this.userPref.setCurrentPlayingTrackVolume(this.volume);

    const lastTrackId = this.userPref.getCurrentPlayingTrack();
    const lastTrackTime = this.userPref.getCurrentPlayingTrackTime();

    if (lastTrackId) {
      this.postService.getPostById(parseInt(lastTrackId)).subscribe(post => {
        this.setTrack(post);
        if (this.isAudioPodcast(post) && lastTrackTime) {
          this.audio!.currentTime = parseFloat(lastTrackTime);
          this.currentTime = this.audio!.currentTime;
        }
      });
    }

    this.audio.addEventListener('timeupdate', () => {
      if (!this.audio) return;
      this.currentTime = this.audio.currentTime;
      this.duration = this.audio.duration;
      this.setProgress((this.currentTime / this.duration) * 100);
      this.userPref.setCurrentPlayingTrackTime(this.currentTime - 5);
    });

    this.audio.addEventListener('ended', () => {
      this.isPlaying = false;
    });
  }

  loadTrack(postId: number): void {
    this.postService.getPostById(postId).subscribe(post => {
      this.setTrack(post);
      this.play();
    });
  }

  private setTrack(post: Post): void {
    if (this.currentPost && this.currentPost.id != post.id) {
      this.unloadTrackAndClose();
    }
    this.currentPost = post;
    this.currentPostId.next(post.id);
    this.userPref.setCurrentPlayingTrack(post.id);

    if (this.isAudioPodcast(post)) {
      this.audio!.src = post.podcastUrl!;
      this.audio!.load();
      this.duration = post.mediaTimeLength;
    } else {
      this.prepareSpeech(post);
    }
  }

  private prepareSpeech(post: Post): void {
    const win = this.platform.getNativeWindow();
    if (!win || !post.contentText) return;

    this.ttsText = this.extractContent(post.contentText);
    this.ttsWords = this.ttsText.split(/\s+/);
    this.speechUtterance = this.createSpeechUtterance(this.ttsText);

    const estimatedDuration = this.ttsWords.length / (this.speechRate * 2);
    this.duration = estimatedDuration;
    this.currentTime = 0;

    // This boundary event estimates spoken progress during TTS playback.
    this.speechUtterance.onboundary = (event: SpeechSynthesisEvent) => {
      const charIndex = event.charIndex;
      const textBefore = this.ttsText.slice(0, charIndex);
      const wordsSpoken = textBefore.trim().split(/\s+/).length;
      this.currentTime = wordsSpoken / (this.speechRate * 2);
      this.setProgress((this.currentTime / this.duration) * 100);
      this.userPref.setCurrentPlayingTrackTime(this.currentTime - 5);
    };

    this.attachUtteranceEvents();
    win.speechSynthesis.speak(this.speechUtterance);
  }

  play(): void {
    if (!this.currentPost) return;

    if (this.isAudioPodcast(this.currentPost)) {
      this.audio?.play();
    } else {
      const win = this.platform.getNativeWindow();
      if (!win || !this.ttsText) return;

      const currentCharIndex = Math.floor((this.progress / 100) * this.ttsText.length);
      const resumeIndex = Math.max(currentCharIndex - 10, 0);
      const textToSpeak = this.ttsText.substring(resumeIndex);

      this.speechUtterance = this.createSpeechUtterance(textToSpeak);

      // This boundary estimates time while resuming TTS at a specific position.
      this.speechUtterance.onboundary = (event: SpeechSynthesisEvent) => {
        if (event.name === 'word') {
          const absoluteIndex = resumeIndex + event.charIndex;
          this.currentTime = absoluteIndex / 10;
          this.setProgress((absoluteIndex / this.ttsText.length) * 100);
          this.userPref.setCurrentPlayingTrackTime(this.currentTime - 5);
        }
      };

      this.attachUtteranceEvents();
      win.speechSynthesis.cancel();
      win.speechSynthesis.speak(this.speechUtterance);
      this.isPlaying = true;
    }

    this.isPlaying = true;
  }

  pause(): void {
    if (!this.currentPost) return;

    if (this.isAudioPodcast(this.currentPost)) {
      this.audio?.pause();
    } else {
      const win = this.platform.getNativeWindow();
      if (win) {
        win.speechSynthesis.pause();
      }
    }

    this.isPlaying = false;
  }

  togglePlayPause(): void {
    this.isPlaying ? this.pause() : this.play();
  }

  unloadTrackAndClose(): void {
    if (this.audio) {
      this.audio.pause();
      this.audio.src = '';
    }

    const win = this.platform.getNativeWindow();
    if (win) {
      win.speechSynthesis.cancel();
    }

    this.currentPost = undefined;
    this.currentPostId.next(0);
    this.isPlaying = false;
    this.currentTime = 0;
    this.progress = 0;
    this.userPref.deleteAudioPreferences();
  }

  seekTo(value: number): void {
    if (!this.currentPost) return;

    if (this.isAudioPodcast(this.currentPost)) {
      if (this.audio && this.audio.duration) {
        this.audio.currentTime = (value / 100) * this.audio.duration;
      }
    } else {
      const win = this.platform.getNativeWindow();
      if (!this.ttsText || !win) return;

      const totalWords = this.ttsWords.length;
      const targetWordIndex = Math.floor((value / 100) * totalWords);
      const textToSpeak = this.ttsWords.slice(targetWordIndex).join(' ');

      win.speechSynthesis.cancel();

      this.speechUtterance = this.createSpeechUtterance(textToSpeak);

      this.duration = totalWords / (this.speechRate * 2);
      this.currentTime = (targetWordIndex / totalWords) * this.duration;
      this.setProgress((this.currentTime / this.duration) * 100);

      this.speechUtterance.onboundary = (event: SpeechSynthesisEvent) => {
        const charIndex = event.charIndex;
        const spokenText = textToSpeak.slice(0, charIndex);
        const spokenWords = spokenText.trim().split(/\s+/).length;
        this.currentTime = ((targetWordIndex + spokenWords) / totalWords) * this.duration;
        this.setProgress((this.currentTime / this.duration) * 100);
        this.userPref.setCurrentPlayingTrackTime(this.currentTime - 5);
      };

      this.attachUtteranceEvents();
      win.speechSynthesis.speak(this.speechUtterance);
    }
  }

  addTime(value: number): void {
    if (this.currentPost && this.isAudioPodcast(this.currentPost) && this.audio) {
      this.audio.currentTime += value;
    }
  }

  changeSpeechRate(): void {
    if (this.speechRate === 2) this.speechRate = 0;
    this.speechRate += 0.25;

    const win = this.platform.getNativeWindow();
    if (!win || !this.ttsText) return;

    const currentCharIndex = Math.floor((this.progress / 100) * this.ttsText.length);
    const resumeIndex = Math.max(currentCharIndex - 10, 0);
    const textToSpeak = this.ttsText.substring(resumeIndex);

    this.speechUtterance = this.createSpeechUtterance(textToSpeak);

    this.speechUtterance.onboundary = (event: SpeechSynthesisEvent) => {
      if (event.name === 'word') {
        const absoluteIndex = resumeIndex + event.charIndex;
        this.currentTime = absoluteIndex / 10;
        this.setProgress((absoluteIndex / this.ttsText.length) * 100);
        this.userPref.setCurrentPlayingTrackTime(this.currentTime - 5);
      }
    };

    this.attachUtteranceEvents();
    win.speechSynthesis.cancel();
    win.speechSynthesis.speak(this.speechUtterance);
    this.isPlaying = true;
  }

  setVolume(value: number): void {
    this.volume = value;
    if (this.audio) this.audio.volume = value;
    this.userPref.setCurrentPlayingTrackVolume(value);
  }

  getCurrentPost(): Post | undefined {
    return this.currentPost;
  }

  isPlayingPost(post: Post) {
    if (this.currentPost && post)
      return this.isPlaying && this.currentPost.id == post.id;
    return false;
  }

  getIsPlaying(): boolean {
    return this.isPlaying;
  }

  getCurrentTime(): number {
    return this.currentTime;
  }

  getProgress(): number {
    return this.progress;
  }

  setProgress(progress: number) {
    this.progress = progress;
    this.progressSubject.next(this.progress);
  }

  getDuration(): number {
    return this.duration;
  }

  getVolume(): number {
    return this.volume;
  }

  getSpeechRate(): number {
    return this.speechRate;
  }

  isAudioPodcast(post: Post): boolean {
    return !!post.podcastUrl;
  }

  private extractContent(html: string): string {
    const temp = document.createElement('div');
    temp.innerHTML = html;
    return temp.textContent || temp.innerText || '';
  }

  private createSpeechUtterance(text: string): SpeechSynthesisUtterance {
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = 'fr-FR';
    utterance.rate = this.speechRate;
    utterance.pitch = 1.4;
    return utterance;
  }

  private attachUtteranceEvents(): void {
    this.speechUtterance!.onstart = () => {
      this.isPlaying = true;
    };
    this.speechUtterance!.onend = () => {
      this.isPlaying = false;
    };
    this.speechUtterance!.onerror = () => {
      this.isPlaying = false;
    };
  }
}
