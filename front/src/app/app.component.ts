import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'front';

  constructor(private http: HttpClient) {}

  generateFrontEndError() {
    throw new Error('Front End Error');
  }

  postHttp() {
    this.http.post('http://localhost:8080/api/docker-kafka-server/topic/test2/data',
    {"clusterId":"docker-kafka-server","topicId":"test2","partition":0,"key":"","timestamp": new Date().toISOString,"value":"toto","keySchema":"","valueSchema":"","headers":{"":""}})
    .subscribe();
  }

  postHttpError() {
    this.http.post('http://localhost:8080/api/docker-kafka-server/topic/test2/data',
    {"clusterId":"docker-kafka-server","topicId":"test2","partition":0,"key":"","timestamp": new Date().toISOString,"value":"error","keySchema":"","valueSchema":"","headers":{"":""}})
    .subscribe();
  }

  getHttp404() {
    this.http.get('http://localhost:8080/notfound').subscribe();
  }
}
