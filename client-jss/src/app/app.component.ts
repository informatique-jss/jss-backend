import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SHARED_IMPORTS } from './libs/SharedImports';
import { ConstantService } from './services/constant.service';
import { GtmService } from './services/gtm.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,
    SHARED_IMPORTS,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  standalone: true
})
export class AppComponent {

  constructor(
    private constantService: ConstantService,
    private gtm: GtmService,
  ) { }

  ngOnInit() {
    this.constantService.initConstant();

    //Init Google tag manager if in browser
    this.gtm.init();
  }
}
