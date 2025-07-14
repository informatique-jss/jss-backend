import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserPreferenceService {

  constructor() { }

  // User search
  setUserSearchBookmark(search: any, searchName: string) {
    if (search && searchName)
      localStorage.setItem('search-' + searchName, JSON.stringify(search));
  }

  getUserSearchBookmark(searchName: string): any {
    if (searchName) {
      let value = localStorage.getItem('search-' + searchName);
      if (value) {
        let list = JSON.parse(value!);
        if (list)
          return list;
      }
    }
    return undefined;
  }

  // Dark mode
  setDarkMode(darkMode: boolean) {
    if (darkMode != undefined && darkMode != null)
      localStorage.setItem("darkMode", JSON.stringify(darkMode));
  }

  getDarkMode(): boolean {
    let value = localStorage.getItem('darkMode');
    if (value) {
      let darkMode = JSON.parse(value!);
      if (darkMode)
        return darkMode;
    }
    return false;
  }
}



