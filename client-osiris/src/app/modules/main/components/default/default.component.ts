import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { ToastComponent } from '../../../../libs/toast/toast.component';
import { LoadingComponent } from '../loading/loading.component';
import { VerticalLayoutComponent } from '../vertical-layout/vertical-layout.component';

@Component({
  selector: 'default',
  templateUrl: './default.component.html',
  styleUrls: ['./default.component.scss'],
  imports: [SHARED_IMPORTS, VerticalLayoutComponent, LoadingComponent, ToastComponent],
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
