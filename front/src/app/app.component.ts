import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'front';

  constructor(private http: HttpClient) {
    setInterval(() => {
      (this as any).notExistedObj.notExistedProp = 'will throw null reference exception';
    }, 5000);
  }

  generateFrontEndError() {
    throw new Error('Front End Error');
  }

  postHttp() {
    this.http.post('http://localhost:8080/api/docker-kafka-server/topic/test2/data',
    {"clusterId":"docker-kafka-server","topicId":"test2","partition":0,"key":"","timestamp": new Date().toISOString,"value":"toto","keySchema":"","valueSchema":"","headers":{"":""}})
    .subscribe();
  }

  postHttpPayment() {
      this.http.post('http://localhost:8080/api/docker-kafka-server/topic/document/data',
      {"clusterId":"docker-kafka-server","topicId":"document","partition":0,"key":"","timestamp": new Date().toISOString,"value":"{\"clientId\": \"123\", \"documentId\": \"p1\", \"type\":\"payment\"}","keySchema":"","valueSchema":"","headers":{"":""}})
      .subscribe();
    }

    postHttpInvoice() {
        this.http.post('http://localhost:8080/api/docker-kafka-server/topic/document/data',
        {"clusterId":"docker-kafka-server","topicId":"document","partition":0,"key":"","timestamp": new Date().toISOString,"value":"{\"clientId\": \"123\", \"documentId\": \"i1\", \"type\":\"invoice\"}","keySchema":"","valueSchema":"","headers":{"":""}})
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
