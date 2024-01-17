/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PrepaVisiteTiersTurnoverComponent } from './prepa-visite-tiers-turnover.component';

describe('ResponsableMainComponent', () => {
  let component: PrepaVisiteTiersTurnoverComponent;
  let fixture: ComponentFixture<PrepaVisiteTiersTurnoverComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PrepaVisiteTiersTurnoverComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PrepaVisiteTiersTurnoverComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
