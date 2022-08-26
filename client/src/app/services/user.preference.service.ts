import { Point } from '@angular/cdk/drag-drop';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserPreferenceService {

  constructor(http: HttpClient
  ) {
  }

  // Total draggable table position
  setUserTotalDivPosition(point: Point) {
    localStorage.setItem('total-table-position', JSON.stringify(point));
  }

  getUserTotalDivPosition(): Point {
    if (localStorage.getItem('total-table-position') != null) {
      let a = localStorage.getItem('total-table-position');
      let point = JSON.parse(a!) as Point;
      if (point)
        return point;
    }
    return { x: 0, y: 0 };
  }

  // User note draggable table position
  setUserNoteTablePosition(point: Point) {
    localStorage.setItem('user-note-table-position', JSON.stringify(point));
  }

  getUserNoteTablePosition(): Point {
    if (localStorage.getItem('user-note-table-position') != null) {
      let a = localStorage.getItem('user-note-table-position');
      let point = JSON.parse(a!) as Point;
      if (point)
        return point;
    }
    return { x: 0, y: 0 };
  }
}



