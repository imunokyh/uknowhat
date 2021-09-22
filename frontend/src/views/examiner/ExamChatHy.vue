<template>
  <div>
    <b-overlay :show="show" rounded="sm">
      <h2>{{ roomNum }}번 방</h2>
      내용:
      <input v-model="message" type="text" @keyup.enter="sendMessage()" />
      <b-button variant="primary" @click="sendStart()">start</b-button>
      <b-button variant="primary" @click="readProb()">read prob</b-button>
      <b-button variant="primary" @click="sendWaiting()">waiting</b-button>
      <b-button variant="primary" @click="readNext()">next</b-button>
      <b-button variant="primary" @click="sendOxProb()">ox prob</b-button>
      <b-button variant="primary" @click="sendObProb()">ob prob</b-button>
      <b-button variant="primary" @click="sendTimer()">timer</b-button>
      <b-button variant="primary" @click="sendTimeCnt()">time cnt</b-button>
      <b-button variant="primary" @click="sendTimeOut()">timeout</b-button>
      <b-button variant="primary" @click="writeAnswer()">write answer</b-button>
      <b-button variant="primary" @click="checkAnswer()">check answer</b-button>
      <b-button variant="primary" @click="checkWinner()">check winner</b-button>
      <b-button variant="primary" @click="sendAnsChart()">report</b-button>

      <!-- 기존채팅-->
      <div>
        <ul class="list-group" :key="idx" v-for="(item, idx) in recvList">
          <li class="list-group-item">
            [{{ item.participantName }}]: {{ item.content }}
          </li>
        </ul>
      </div>
      <div class="start">
        <label for="">시작</label>
      </div>

      <div class="readprob">
        <label for="">문제읽기</label>
        <b-container fluid="sm">
          <b-row>
            <b-col sm="3" v-for="(qes, index) in probList" :key="index">
              {{ qes }}
            </b-col>
          </b-row>
        </b-container>
      </div>

      <div class="next">
        <label for=""
          >현재문제번호: {{ currentProbNum + 1 }}/{{ probList.length }}
        </label>
        <label v-if="probList.length > 0">
          ({{ probList[currentProbNum].questionType }})
        </label>
      </div>

      <div class="curprob">
        <label for="">현재문제</label>
        <div v-if="probList.length > 0">
          <div class="col-sm-12">
            <h2>{{ probList[currentProbNum].questionText }}</h2>
          </div>
          <div v-if="probList[currentProbNum].questionType === 'OX'">
            <div class="row h-100">
              <b-button variant="primary" class="col-sm-6">O</b-button>
              <b-button variant="danger" class="col-sm-6">X</b-button>
            </div>
          </div>
          <div v-else-if="probList[currentProbNum].questionType === 'OB'">
            <div class="row h-50">
              <b-button variant="primary" class="col-sm-6">{{
                probList[currentProbNum].answer1Text
              }}</b-button>
              <b-button variant="danger" class="col-sm-6">{{
                probList[currentProbNum].answer2Text
              }}</b-button>
            </div>
            <div class="row h-50">
              <b-button variant="success" class="col-sm-6">{{
                probList[currentProbNum].answer3Text
              }}</b-button>
              <b-button variant="warning" class="col-sm-6">{{
                probList[currentProbNum].answer4Text
              }}</b-button>
            </div>
          </div>
        </div>
      </div>

      <div class="waiting">
        <label for="">대기방</label>
      </div>
      <div class="checkanswer">
        <label for="">답체크</label>
      </div>
      <div class="report">
        <label for="">레포트</label>
      </div>
    </b-overlay>
  </div>
</template>

<script>
import Stomp from "webstomp-client";
import SockJS from "sockjs-client";

export default {
  name: "Chat",
  props: {
    number: {
      type: String,
      default: "",
    },
    nickname: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      roomNum: "",
      userName: "",
      message: "",
      examinerId: "hygjob",
      recvList: [],
      show: false,
      participantStore: {
        userName: "",
        score: 0,
      },
      probList: [],
      curList: [],
      currentProbNum: 0,
    };
  },
  created() {
    window.addEventListener("beforeunload", this.unLoadEvent);
    this.roomId = 28;
    this.roomNum = this.number;
    this.userName = this.nickname;
    console.log(this.number);
    console.log(this.nickname);
    this.connect();
  },
  destroyed() {
    if (this.stompClient !== null) {
      const msg = {
        roomNumber: this.roomNum,
        participantName: this.userName,
        content: "",
        type: "UNJOIN",
      };
      this.stompClient.send("/publish/play/join", JSON.stringify(msg));

      this.stompClient.unsubscribe("/subscribe/play/room/" + this.roomNum, {});
      this.stompClient.disconnect();
      this.stompClient = null;
    }
  },
  mounted() {
    this.examinerId = this.nickname;
  },
  methods: {
    readProb() {
      const config = {
        //headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
        params: {
          roomId: this.roomId,
        },
      };
      //console.log(sessionStorage.getItem("token"))
      let promise = this.$http.get("/api/v1/play/readprob", config);
      return promise.then((res) => {
        this.probList = res.data.result;
      });
    },
    readNext() {
      const config = {
        //headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
        params: {
          roomId: this.roomId,
        },
      };
      //console.log(sessionStorage.getItem("token"))
      let promise = this.$http.get("/api/v1/play/readnext", config);
      return promise.then((res) => {
        this.curList = res.data.result;
      });
    },
    writeAnswer() {
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
        // params: {
        //   roomNumber: this.roomId,
        // },
      };
      const data = {
        roomNumber: '1234',
        participantName: 'yuno4',
        questionNumber : 1,
        answerText : 4
      }
      //console.log(sessionStorage.getItem("token"))
      let promise = this.$http.post("/api/v1/play/writeAnswer", 
        data,
        {headers: config.headers});
      return promise.then((res) => {
        //this.curList = res.data.result;
      });
    },
    checkAnswer() {
      const config = {
        //headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
        params: {
          roomId: this.roomId,
        },
      };
      //console.log(sessionStorage.getItem("token"))
      let promise = this.$http.get("/api/v1/play/checkans", config);
      return promise.then((res) => {
        this.curList = res.data.result;
      });
    },
    checkWinner() {
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
        // params: {
        //   roomNumber: this.roomId,
        // },
      };
      const data = {
        roomNumber: '1234',
      }
      //console.log(sessionStorage.getItem("token"))
      let promise = this.$http.post("/api/v1/play/checkwinner", 
        data,
        {headers: config.headers});
      return promise.then((res) => {
        //this.curList = res.data.result;
      });
    },
    connect() {
      let sock = new SockJS("/stomp");
      this.stompClient = Stomp.over(sock, { debug: false });
      console.log("소켓 연결을 시도합니다.");
      this.stompClient.connect(
        {},
        (frame) => {
          // 소켓 연결 성공
          this.connected = true;
          console.log("소켓 연결 성공");
          this.stompClient.subscribe(
            "/subscribe/play/room/" + this.roomNum,
            (res) => {
              //this.recvList.push(JSON.parse(res.body));
              //this.show = false;
              this.recvMessage(res.body);
            }
          );

          const msg = {
            roomNumber: this.roomNum,
            participantName: this.userName,
            content: "",
            type: "JOIN",
          };

          this.stompClient.send("/publish/play/join", JSON.stringify(msg));
        },
        (error) => {
          // 소켓 연결 실패
          console.log("소켓 연결 실패", error);
          this.connected = false;
        }
      );
    },
    send() {
      if (this.stompClient && this.stompClient.connected) {
        const msg = {
          roomNumber: this.roomNum,
          participantName: this.userName,
          content: this.message,
          type: "CHAT",
        };
        this.stompClient.send("/publish/play/message", JSON.stringify(msg));
      }
    },
    send(type, message) {
      if (this.stompClient && this.stompClient.connected) {
        const msg = {
          roomNumber: this.roomNum,
          examinerId: this.examinerId,
          content: message,
          type: type,
        };
        this.stompClient.send("/publish/play/message", JSON.stringify(msg));
      }
    },
    send(type, message, answer1Text, answer2Text, answer3Text, answer4Text) {
      if (this.stompClient && this.stompClient.connected) {
        const msg = {
          roomNumber: this.roomNum,
          examinerId: this.examinerId,
          content: message,
          answer1Text: answer1Text,
          answer2Text: answer2Text,
          answer3Text: answer3Text,
          answer4Text: answer4Text,
          type: type,
        };
        this.stompClient.send("/publish/play/message", JSON.stringify(msg));
      }
    },
    unLoadEvent(event) {
      if (this.stompClient !== null) {
        const msg = {
          roomNumber: this.roomNum,
          participantName: this.userName,
          content: "",
          type: "UNJOIN",
        };
        this.stompClient.send("/publish/play/join", JSON.stringify(msg));

        this.stompClient.unsubscribe(
          "/subscribe/chat/room/" + this.roomNum,
          {}
        );
        this.stompClient.disconnect();
        this.stompClient = null;
      }
    },
    getRandomInt() {
      let min = 0; // 하한
      let max = 10; // 상한
      min = Math.ceil(min);
      max = Math.ceil(max);
      return Math.floor(Math.random() * (max - min)) + min; // 상한은 포함X
    },
    recvMessage(resBody) {
      let res = JSON.parse(resBody);
      if (res.type === "READPROB") {
        this.probList = res.result;
      }
    },
    sendMessage() {
      if (this.userName !== "" && this.message !== "") {
        this.send();
        this.message = "";
      }
    },
    sendStart() {
      const type = "START";
      let message = "start";
      this.send(type, message);
    },
    sendReadProb() {
      const type = "READPROB";
      let message = "read problem";
      this.send(type, message);
    },
    sendWaiting() {
      const type = "WAITING";
      let message = "waiting room";
      this.send(type, message);
    },
    sendNext() {
      this.currentProbNum++;
      if (this.currentProbNum >= this.probList.length) {
        alert("문제 수를 넘어가서 처음으로 초기화 합니다.");
        this.currentProbNum = 0;
      }

      if (this.probList[this.currentProbNum].questionType === "OX")
        this.sendOxProb();
      else if (this.probList[this.currentProbNum].questionType === "OB")
        this.sendObProb();
    },
    sendOxProb() {
      const type = "OXP";
      let message = "여기 문제 있다.";
      this.send(type, message);
    },
    sendObProb() {
      const type = "OBP";
      let message = "광운센터위치는?";
      let answer1Text = "1층";
      let answer2Text = "2층";
      let answer3Text = "3층";
      let answer4Text = "4층";
      this.send(
        type,
        message,
        answer1Text,
        answer2Text,
        answer3Text,
        answer4Text
      );
    },
    sendTimer() {
      const type = "TIMER";
      let message = "10"; // count of timer

      this.send(type, message);
    },
    sendTimeCnt() {
      const type = "TIMECNT";
      let message = this.getRandomInt(); // 임의의 수를 일단 보낸다.

      this.send(type, message);
    },
    sendTimeOut() {
      const type = "TIMEOUT";
      let message = "time out";
      this.send(type, message);
    },
    sendCheckAnswer() {
      const type = "ANSCHK";
      let message = "subject problem"; // TODO 상세 메시지
      this.send(type, message);
    },
    sendAnsChart() {
      const type = "ANSCHART";
      let message = "report result";
      this.send(type, message);
    },
  },
};
</script>