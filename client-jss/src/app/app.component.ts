import { Component } from '@angular/core';
import { Subscription } from 'rxjs';
import { AudioService } from './main/services/audio.service';
import { ConstantService } from './services/constant.service';

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
    private audioService: AudioService
  ) { }

  ngOnInit() {
    this.constantService.initConstant();
    this.currentPodcastSubscription = this.audioService.currentPodcastObservable.subscribe(item => this.isCurrentPodcastDisplayed = item);
  }

  ngOnDestroy() {
    this.currentPodcastSubscription.unsubscribe();
  }
}
