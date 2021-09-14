<template>
  <div>
    <b-overlay :show="show" rounded="sm">
      <h2>{{ roomNum }}번 방</h2>
      내용:
      <input v-model="message" type="text" @keyup.enter="sendMessage()" />
      <b-button variant="primary" @click="sendStart()">start</b-button>
      <b-button variant="primary" @click="sendReadProb()">read prob</b-button>
      <b-button variant="primary" @click="sendWaiting()">waiting</b-button>
      <b-button variant="primary" @click="sendOxProb()">ox prob</b-button>
      <b-button variant="primary" @click="sendSbProb()">sb prob</b-button>
      <b-button variant="primary" @click="sendTimer()">timer</b-button>
      <b-button variant="primary" @click="sendTimeCnt()">time cnt</b-button>
      <b-button variant="primary" @click="sendTimeOut()">timeout</b-button>
      <b-button variant="primary" @click="sendCheckAnswer()"
        >check answer</b-button
      >
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
      </div>

      <div class="waiting">
        <label for="">대기방</label>
      </div>
      <div class="oxprob">
        <label for="">OX문제</label>
      </div>
      <div class="subprob">
        <label for="">객관식문제</label>
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
      probList: [
        {
          id: 1,
          questionId: 9,
          questionOrder: 0,
          questionScore: 10,
          questionTime: 5,
          roomId: 25,
          questionText: "계절의 변화와 우리의 건강은 관련이 없다.",
          answer1Text: "",
          answer2Text: "",
          answer3Text: "",
          answer4Text: "",
          questionType: "OX",
          questionAnswer: 2,
        },
        {
          id: 2,
          questionId: 24,
          questionOrder: 0,
          questionScore: 10,
          questionTime: 5,
          roomId: 25,
          questionText:
            "조선시대 궁중 여인이나 양반집 규수가 세정제로 사용한 곡식 가루를 가리킨 ‘비루(飛陋)’에서 유래된 말은?",
          answer1Text: "비듬",
          answer2Text: "비누",
          answer3Text: "비데",
          answer4Text: "비수",
          questionType: "OB",
          questionAnswer: 2,
        },
      ],
    };
  },
  created() {
    window.addEventListener("beforeunload", this.unLoadEvent);

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
    connect() {
      let sock = new SockJS("/stomp");
      this.stompClient = Stomp.over(sock);
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
    recvMessage(resBody) {},
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
    sendOxProb() {
      const type = "OXP";
      let message = "여기 문제 있다.";
      this.send(type, message);
    },
    sendSbProb() {
      const type = "SBP";
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