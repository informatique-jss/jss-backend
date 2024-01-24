/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VisitPrepaTiersTurnoverComponent } from './visit-prepa-tiers-turnover.component';

describe('ResponsableMainComponent', () => {
  let component: VisitPrepaTiersTurnoverComponent;
  let fixture: ComponentFixture<VisitPrepaTiersTurnoverComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VisitPrepaTiersTurnoverComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VisitPrepaTiersTurnoverComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
