import { ErrorHandler, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http'
import { ApmErrorHandler, ApmModule, ApmService } from '@elastic/apm-rum-angular';

import { AppComponent } from './app.component';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot([]),
    HttpClientModule,
    ApmModule
  ],
  providers: [{
    provide: ErrorHandler,
    useClass: ApmErrorHandler
  }],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor(apmService: ApmService) {
    const apm = apmService.init({
      serviceName: 'angular-app',
      serverUrl: 'http://localhost:8200',
      environment: 'production',
      propagateTracestate: true,
      distributedTracingOrigins: ['http://localhost:8080']
    });
  }
}

