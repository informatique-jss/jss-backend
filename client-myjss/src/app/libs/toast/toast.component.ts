import { Component, OnInit } from '@angular/core';
import { AppService } from '../../modules/main/services/app.service';
import { SHARED_IMPORTS } from '../SharedImports';
import { Toast } from './Toast';

@Component({
  selector: 'toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class ToastComponent implements OnInit {

  toasts: Toast[] = [];

  constructor(private appService: AppService) {
    this.toasts = appService.toasts;
  }

  ngOnInit() {
  }


}
