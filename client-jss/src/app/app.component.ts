import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Subscription } from 'rxjs';
import { SHARED_IMPORTS } from './libs/SharedImports';
import { AudioPlayerService } from './main/services/audio.player.service';
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

  isCurrentPodcastDisplayed: boolean = false;
  currentPodcastSubscription: Subscription = new Subscription;

  constructor(
    private constantService: ConstantService,
    private audioService: AudioPlayerService,
    private gtm: GtmService,
  ) { }

  ngOnInit() {
    this.constantService.initConstant();

    //Init Google tag manager if in browser
    this.gtm.init();
  }
}
