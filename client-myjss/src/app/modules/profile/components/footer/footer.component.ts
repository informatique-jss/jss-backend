import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'main-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {
  logoJss: string = '/assets/images/logo.png';

  constructor() { }

  ngOnInit() {
  }

}
