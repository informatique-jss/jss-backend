import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { PlatformService } from '../../../main/services/platform.service';

@Component({
  selector: 'app-beta',
  templateUrl: './beta.component.html',
  styleUrls: ['./beta.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class BetaComponent implements OnInit {

  constructor(private plateformService: PlatformService, private appService: AppService) { }

  ngOnInit() {
    if (this.plateformService.isBrowser()) {
      this.plateformService.getNativeDocument()!.cookie = "beta_access=true; path=/; SameSite=Lax";
      this.appService.openRoute(null, "/", undefined);
    }
  }

}
