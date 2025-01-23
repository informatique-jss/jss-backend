import { Component, OnInit } from '@angular/core';
import { AppService } from '../app.service';
import { Toast } from './Toast';

@Component({
  selector: 'toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css']
})
export class ToastComponent implements OnInit {

  toasts: Toast[] = [];

  constructor(private appService: AppService) {
    this.toasts = appService.toasts;
  }

  ngOnInit() {
  }


}
