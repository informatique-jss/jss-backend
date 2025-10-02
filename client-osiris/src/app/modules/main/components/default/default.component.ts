import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { VerticalLayoutComponent } from '../vertical-layout/vertical-layout.component';

@Component({
  selector: 'default',
  templateUrl: './default.component.html',
  styleUrls: ['./default.component.scss'],
  imports: [SHARED_IMPORTS, VerticalLayoutComponent],
  standalone: true
})
export class DefaultComponent implements OnInit {

  constructor(
    private router: Router,
  ) { }

  ngOnInit() {
  }

  ngOnDestroy() {
  }
}
