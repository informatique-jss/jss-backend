import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-author-list',
  templateUrl: './author-list.component.html',
  styleUrls: ['./author-list.component.css']
})
export class AuthorListComponent implements OnInit {

  page: number = 0;
  constructor() { }

  ngOnInit() {
  }

  getNextPosts() {
    this.page++;
    this.fetchNextPosts();
  }

  fetchNextPosts() {

  }

}
