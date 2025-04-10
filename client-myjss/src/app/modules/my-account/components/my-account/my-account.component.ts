import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ThemeService } from '../../../../libs/theme.service';

@Component({
  selector: '.my-account',
  templateUrl: './my-account.component.html',
  styleUrls: ['./my-account.component.css'],
  standalone: false
})
export class MyAccountComponent implements OnInit {

  constructor(
    private router: Router,
    private themeService: ThemeService
  ) { }

  ngOnInit() {
  }
}
