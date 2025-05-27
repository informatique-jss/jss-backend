import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { ToastComponent } from '../../../../libs/toast/toast.component';
import { FooterComponent } from '../../../profile/components/footer/footer.component';
import { TopBarComponent } from '../../../profile/components/top-bar/top-bar.component';

@Component({
  selector: 'default',
  templateUrl: './default.component.html',
  styleUrls: ['./default.component.css'],
  imports: [SHARED_IMPORTS, TopBarComponent, FooterComponent, ToastComponent],
  standalone: true
})
export class DefaultComponent implements OnInit {

  constructor(
    private router: Router,
  ) { }

  ngOnInit() {
  }

  ngOnDestroy() {
  }

  isDisplayLightHeader() {
    let url: String = this.router.url;
    if (url) {
      if (url.indexOf("account") >= 0 && url.indexOf("signin") <= 0)
        return true;
      if (url.indexOf("quotation") >= 0)
        return true;
    }
    return false;
  }

  isDisplayFooter() {
    let url: String = this.router.url;
    if (url) {
      if (url.indexOf("signin") >= 0)
        return false;
      if (url.indexOf("quotation") >= 0)
        return false;
    }
    return true;
  }

}
