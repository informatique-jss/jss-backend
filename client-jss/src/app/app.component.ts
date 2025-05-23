import { Component } from '@angular/core';
import { Subscription } from 'rxjs';
import { AudioService } from './main/services/audio.service';
import { ConstantService } from './services/constant.service';
import { GtmService } from './services/gtm.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  standalone: false
})

export class AppComponent {

  isCurrentPodcastDisplayed: boolean = false;
  currentPodcastSubscription: Subscription = new Subscription;


  constructor(private constantService: ConstantService,
    private audioService: AudioService,
    private gtm: GtmService
  ) { }

  ngOnInit() {
    this.constantService.initConstant();
    this.currentPodcastSubscription = this.audioService.currentPodcastObservable.subscribe(item => this.isCurrentPodcastDisplayed = item);
    //Init Google tag manager if in browser
    this.gtm.init();
  }

  ngOnDestroy() {
    this.currentPodcastSubscription.unsubscribe();
  }
}
