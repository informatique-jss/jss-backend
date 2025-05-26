import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { ToastComponent } from '../../../libs/toast/toast.component';
import { PlatformService } from '../../../services/platform.service';
import { AudioPlayerService } from '../../services/audio.player.service';
import { AudioPlayerComponent } from '../audio-player/audio-player.component';
import { FooterComponent } from '../footer/footer.component';
import { HeaderComponent } from '../header/header.component';

@Component({
  selector: 'default',
  templateUrl: './default.component.html',
  styleUrls: ['./default.component.css'],
  imports: [SHARED_IMPORTS,
    HeaderComponent,
    AudioPlayerComponent,
    FooterComponent,
    ToastComponent,
  ],
  standalone: true
})
export class DefaultComponent implements OnInit {
  currentPodcastId: number = 0;
  currentPodcastSubscription: Subscription = new Subscription;

  constructor(
    private audioService: AudioPlayerService,
    private platformService: PlatformService
  ) { }

  ngOnInit() {
    if (this.platformService.isBrowser())
      this.currentPodcastSubscription = this.audioService.currentPodcastObservable.subscribe(item => this.currentPodcastId = item);
  }

  ngOnDestroy() {
    this.currentPodcastSubscription.unsubscribe();
  }
}