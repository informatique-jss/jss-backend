import { Component, OnInit } from '@angular/core';
import { NgbToastModule } from '@ng-bootstrap/ng-bootstrap';
import { AppService } from '../../services/app.service';
import { SHARED_IMPORTS } from '../SharedImports';
import { Toast } from './Toast';

@Component({
  selector: 'toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, NgbToastModule]
})
export class ToastComponent implements OnInit {

  toasts: Toast[] = [];

  constructor(private appService: AppService) {
    this.toasts = appService.toasts;
  }

  ngOnInit() {
  }

  remove(toastToRemove: Toast): void {
    this.toasts = this.toasts.filter(t => t !== toastToRemove);
  }
}
