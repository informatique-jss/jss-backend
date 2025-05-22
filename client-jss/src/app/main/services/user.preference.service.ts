import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserPreferenceService {

  constructor() { }

  // --------- Audio information --------

  setCurrentPlayingTrack(idPodcast: number) {
    localStorage.setItem('current-playing-track-id', JSON.stringify(idPodcast));
  }

  getCurrentPlayingTrack() {
    return localStorage.getItem('current-playing-track-id');
  }

  setCurrentPlayingTrackTime(currentPlyingTime: number) {
    localStorage.setItem('current-playing-time', JSON.stringify(currentPlyingTime));
  }

  getCurrentPlayingTrackTime() {
    return localStorage.getItem('current-playing-time');
  }

  setCurrentPlayingTrackVolume(currentPlyingVolume: number) {
    localStorage.setItem('current-playing-volume', JSON.stringify(currentPlyingVolume));
  }

  getCurrentPlayingTrackVolume() {
    return localStorage.getItem('current-playing-volume');
  }

  clearUserPrefLocalStorage() {
    return localStorage.clear();
  }
}



