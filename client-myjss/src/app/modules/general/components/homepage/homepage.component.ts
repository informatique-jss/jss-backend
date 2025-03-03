import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {

  logoJss: string = '/assets/images/white-logo.svg';
  videoParis: string = 'assets/videos/paris-home-video.webm'

  constructor() { }

  ngOnInit() {
  }

}
