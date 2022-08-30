import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Note } from './model/Note';

@Injectable({
  providedIn: 'root'
})
export class UserNoteService {

  userNotes: Note[] = [] as Array<Note>;
  private userNotesEvent: BehaviorSubject<Note[]> = new BehaviorSubject<Note[]>(this.userNotes);
  userNotesEventObservable = this.userNotesEvent.asObservable();
  maxId: number = 0;

  constructor(http: HttpClient
  ) {
  }

  saveUserNotes() {
    localStorage.setItem('notes', JSON.stringify(this.userNotes));
  }

  restoreUserNotes() {
    if (localStorage.getItem('notes') != null) {
      let a = localStorage.getItem('notes');
      this.userNotes = JSON.parse(a!) as Array<Note>;
      for (let note of this.userNotes)
        if (note.id > this.maxId)
          this.maxId = note.id;
      this.maxId++;
    } else {
      this.userNotes = [] as Array<Note>;
    }
    this.userNotesEvent.next(this.userNotes);
  }

  addToNotes(label: string | undefined, value: any, link: string | undefined, isHeader: boolean) {
    let note = {} as Note;
    note.id = this.maxId++;
    note.label = label;
    note.value = value;
    note.link = link;
    note.isHeader = isHeader;
    this.userNotes.push(note);
    this.saveUserNotes();
    this.restoreUserNotes();
  }

  deleteAllNotes() {
    this.userNotes = [] as Array<Note>;
    this.saveUserNotes();
    this.restoreUserNotes();
  }

  deleteNote(note: Note) {
    if (this.userNotes)
      for (let i = 0; i < this.userNotes.length; i++) {
        const noteFor = this.userNotes[i];
        if (noteFor.id == note.id)
          this.userNotes.splice(i, 1);
      }
    this.saveUserNotes();
    this.restoreUserNotes();
  }

  downNote(note: Note) {
    if (this.userNotes)
      for (let i = 0; i < this.userNotes.length; i++) {
        const noteFor = this.userNotes[i];
        if (noteFor.id == note.id && i < this.userNotes.length - 1) {
          this.userNotes.splice(i, 1);
          this.userNotes.splice(i + 1, 0, noteFor);
          break;
        }
      }
    this.saveUserNotes();
    this.restoreUserNotes();
  }

  upNote(note: Note) {
    if (this.userNotes)
      for (let i = 0; i < this.userNotes.length; i++) {
        const noteFor = this.userNotes[i];
        if (noteFor.id == note.id && i > 0) {
          this.userNotes.splice(i, 1);
          this.userNotes.splice(i - 1, 0, noteFor);
          break;
        }
      }
    this.saveUserNotes();
    this.restoreUserNotes();
  }
}



