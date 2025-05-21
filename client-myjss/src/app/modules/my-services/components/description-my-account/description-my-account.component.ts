import { Component, HostListener, OnInit } from '@angular/core';

@Component({
  selector: 'description-my-account',
  templateUrl: './description-my-account.component.html',
  styleUrls: ['./description-my-account.component.css'],
  standalone: false
})
export class DescriptionMyAccountComponent implements OnInit {

  isMobile: boolean = false;
  constructor() { }

  ngOnInit() {
    this.checkIfMobile();
  }

  @HostListener('window:resize')
  onResize() {
    this.checkIfMobile();
  }

  private checkIfMobile(): void {
    this.isMobile = window.innerWidth <= 768;
  }
}
