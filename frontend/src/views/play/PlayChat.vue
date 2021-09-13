<template>
  <div>
    <b-overlay :show="show" rounded="sm">
      <h2>{{ roomNum }}번 방</h2>
      내용:
      <input v-model="message" type="text" @keyup.enter="sendMessage()" />

      <b-button variant="primary" @click="sendJoin()">Join</b-button>
      <b-button variant="primary" @click="sendAnswer()">sendanswwer</b-button>

      <!-- 기존채팅-->
      <div>
        <ul class="list-group" :key="idx" v-for="(item, idx) in recvList">
          <li class="list-group-item">
            [{{ item.participantName }}]: {{ item.content }}
          </li>
        </ul>
      </div>
      <div class="join">
        <label for="">참여</label>
      </div>
      <div class="pwaiting">
        <label for="">출제자문제보기중</label>
      </div>
      <div class="oxp">
        <label for="">OX답선택</label>
        남은시간: {{timeCnt}}
        {{oxQuestion}}
      </div>
      <div class="sbp">
        <label for="">객관식답선택</label>
        남은시간: {{timeCnt}}<br>
        {{sbQuestion}}<br>
        {{answer1Text}}<br>
        {{answer2Text}}<br>
        {{answer3Text}}<br>
        {{answer4Text}}
      </div>
      <div class="anschk">
        <label for="">정답여부</label>
        {{correctYn}}
      </div>
      <div class="anschart">
        <label for="">정답분포도</label>
        {{correctYn}}
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
      recvList: [],
      show: false,
      oxQuestion: "",
      sbQuestion: "",
      answer1Text: "",
      answer2Text: "",
      answer3Text: "",
      answer4Text: "",
      timeCnt: "",
      correntYn: "",
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
        type: "UJOI",
      };
      this.stompClient.send("/publish/chat/join", JSON.stringify(msg));

      this.stompClient.unsubscribe("/subscribe/chat/room/" + this.roomNum, {});
      this.stompClient.disconnect();
      this.stompClient = null;
    }
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
          // 서버의 메시지 전송 endpoint를 구독합니다.
          // 이런형태를 pub sub 구조라고 합니다.
          this.stompClient.subscribe(
            "/subscribe/chat/room/" + this.roomNum,
            (res) => {
              this.recvMessage(res.body);
            }
          );

          const msg = {
            roomNumber: this.roomNum,
            participantName: this.userName,
            content: "",
            type: "JOIN",
          };

          this.stompClient.send("/publish/chat/join", JSON.stringify(msg));
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
        this.stompClient.send("/publish/chat/message", JSON.stringify(msg));
      }
    },
    send(type, message) {
      if (this.stompClient && this.stompClient.connected) {
        const msg = {
          roomNumber: this.roomNum,
          participantName: this.userName,
          content: message,
          type: type,
        };
        this.stompClient.send("/publish/chat/message", JSON.stringify(msg));
      }
    },
    unLoadEvent(event) {
      if (this.stompClient !== null) {
        const msg = {
          roomNumber: this.roomNum,
          participantName: this.userName,
          content: "",
          type: "UJOI",
        };
        this.stompClient.send("/publish/chat/join", JSON.stringify(msg));

        this.stompClient.unsubscribe(
          "/subscribe/chat/room/" + this.roomNum,
          {}
        );
        this.stompClient.disconnect();
        this.stompClient = null;
      }
    },
    recvMessage(aResBody) {
      
      let resBody = JSON.parse(aResBody);
      let type = resBody.type;
      console.log("aaa");
      console.log(resBody);
      console.log(resBody.type)
      if (type == "OXP") {
        this.oxQuestion = resBody.content;
      } else if (type == "SBP") {
        this.sbQuestion = resBody.content;
        this.answer1Text = resBody.answer1Text;
        this.answer2Text = resBody.answer2Text;
        this.answer3Text = resBody.answer3Text;
        this.answer4Text = resBody.answer4Text;
      } else if (type == "TIMECNT") {
        this.timeCnt = resBody.content;
      } else if (type == "TIMEOUT") {
      } else if (type == "ANSCHK") {
        this.correctYn = resBody.content; // Y or N
      } else if (type == "ANSCHART") {
      } else if (type == "PWAITING") {
      }
    },
    sendMessage() {
      if (this.userName !== "" && this.message !== "") {
        this.send();
        this.message = "";
      }
    },
    sendAnswer() {
      const type = "SENDANS";
      let message = "1"; // 선택한 답
      this.send(type, message);
    },
  },
};
</script>