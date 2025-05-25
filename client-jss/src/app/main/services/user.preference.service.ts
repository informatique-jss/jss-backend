import { Injectable } from '@angular/core';
import { PlatformService } from '../../services/platform.service';

@Injectable({
  providedIn: 'root'
})
export class UserPreferenceService {

  constructor(private platformService: PlatformService) { }

  // --------- Audio information --------

  setCurrentPlayingTrack(idPodcast: number) {
    if (this.platformService.isBrowser())
      this.platformService.getNativeLocalStorage()!.setItem('current-playing-track-id', JSON.stringify(idPodcast));
  }

  getCurrentPlayingTrack() {
    if (this.platformService.isBrowser())
      return this.platformService.getNativeLocalStorage()!.getItem('current-playing-track-id');
    return "0";
  }

  setCurrentPlayingTrackTime(currentPlyingTime: number) {
    if (this.platformService.isBrowser())
      this.platformService.getNativeLocalStorage()!.setItem('current-playing-time', JSON.stringify(currentPlyingTime));
  }

  getCurrentPlayingTrackTime() {
    if (this.platformService.isBrowser())
      return this.platformService.getNativeLocalStorage()!.getItem('current-playing-time');
    return "0";
  }

  setCurrentPlayingTrackVolume(currentPlyingVolume: number) {
    if (this.platformService.isBrowser())
      this.platformService.getNativeLocalStorage()!.setItem('current-playing-volume', JSON.stringify(currentPlyingVolume));
  }

  getCurrentPlayingTrackVolume() {
    if (this.platformService.isBrowser())
      return this.platformService.getNativeLocalStorage()!.getItem('current-playing-volume');
    return "0";
  }

  deleteAudioPreferences() {
    if (this.platformService.isBrowser()) {
      let allItems = this.platformService.getNativeLocalStorage()! as any;
      if (allItems)
        for (let key in allItems)
          if (key && ["current-playing-track-id", "current-playing-time", "current-playing-volume"].indexOf(key) >= 0)
            this.platformService.getNativeLocalStorage()!.removeItem(key);
    }
  }
}



