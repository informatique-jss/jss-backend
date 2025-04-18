import { Component } from '@angular/core';
import { ConstantService } from './services/constant.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  standalone: false
})

export class AppComponent {
  constructor(private constantService: ConstantService) { }

  ngOnInit() {
    this.constantService.initConstant();
  }
}
