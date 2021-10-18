<template>
  <b-overlay :show="show" rounded="lg" class="h-100">
    <!-- 대기실 페이지 -->
    <div v-if="pageType===0" class="h-100">
      <h1>You're in!</h1>
      <h2>See your nickname on screen?</h2>
      <hr class="my-4" />
      <b-container fluid="sm">
        <b-row>
          <b-col sm="2" v-for="(user, index) in userList" :key="index">
            <b-card bg-variant="dark" text-variant="white" class="text-center m-1">
              <b-card-text> {{user}} </b-card-text>
            </b-card>
          </b-col>
        </b-row>
      </b-container>
    </div>
    <!-- OX 문제 정답 선택 페이지 -->
    <div v-else-if="pageType===1" class="h-100">
      <div class="row h-100">
        <b-button id="true" variant="primary" class="col-sm-6" @click="submit($event)">O</b-button>
        <b-button id="false" variant="danger" class="col-sm-6" @click="submit($event)">X</b-button>
      </div>
    </div>
    <!-- OB(객관식) 문제 정답 선택 페이지 -->
    <div v-else-if="pageType===2" class="h-100">
      <div class="row h-50">
        <b-button id="one" variant="primary" class="col-sm-6" @click="submit($event)">{{answer1Text}}</b-button>
        <b-button id="two" variant="danger" class="col-sm-6" @click="submit($event)">{{answer2Text}}</b-button>
      </div>
      <div class="row h-50">
        <b-button id="three" variant="success" class="col-sm-6" @click="submit($event)">{{answer3Text}}</b-button>
        <b-button id="four" variant="warning" class="col-sm-6" @click="submit($event)">{{answer4Text}}</b-button>
      </div>
    </div>
    <!-- 정답 결과 페이지 -->
    <div v-else-if="pageType===3" class="h-100">
      <div v-if="resType===0">
        <h1>Correct</h1>
        <b-card bg-variant="dark" text-variant="white" class="text-center m-1">
          <b-card-text> + {{score}} </b-card-text>
        </b-card>
      </div>
      <div v-else-if="resType===1">
        <h1>Incorrect</h1>
        <b-card bg-variant="dark" text-variant="white" class="text-center m-1">
          <b-card-text> We believe in you! </b-card-text>
        </b-card>
      </div>
      <div v-else>
        <h1>Time's up</h1>
        <b-card bg-variant="dark" text-variant="white" class="text-center m-1">
          <b-card-text> Dust yourself off. Greatness awaits! </b-card-text>
        </b-card>
      </div>
    </div>
    <!-- 최종 순위 페이지 -->
    <div v-else-if="pageType===4">
      <h1>{{grade}}등</h1>
      <h2 v-if="grade<=3">축하합니다!</h2>
      <h2 v-else-if="grade<=10 && grade>=3">조금 아쉽지만, 그래도 잘하셨습니다!</h2>
      <h2 v-else>다음엔 더 나은 성적을 얻기를 응원합니다!</h2>
    </div>
  </b-overlay>
</template>

<script>
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'

export default {
  name: 'PlayMain',
  props: {
    number: {
        type: String,
        default: ""
    },
    nickname: {
        type: String,
        default: ""
    }
  },
  data() {
    return {
      roomNum: "",
      userName: "",
      show: true,
      pageType: 0,
      userList: [],
      resType: 0,
      score: 0,
      grade: 0,
      answer1Text: "",
      answer2Text: "",
      answer3Text: "",
      answer3Text: "",
    }
  },
  created() {
    window.addEventListener('beforeunload', this.unLoadEvent);

    this.roomNum = this.number;
    this.userName = this.nickname;

    this.connect()
  },
  destroyed() {
    if (this.stompClient !== null) {
        this.sendJoinMessage("UNJOIN");
        this.stompClient.unsubscribe("/subscribe/play/room/" + this.roomNum, {});
        this.stompClient.unsubscribe("/subscribe/play/room/" + this.roomNum + "/" + this.userName, {});
        this.stompClient.disconnect();
        this.stompClient = null;
    }
  },
  methods: {
    connect() {
        let sock = new SockJS("/stomp");
        this.stompClient = Stomp.over(sock, {debug: false});
        this.stompClient.connect(
            {},
            frame => {
              // 소켓 연결 성공
              this.connected = true;
              this.show = false;
              // 서버의 메시지 전송 endpoint를 구독합니다.
              // 이런형태를 pub sub 구조라고 합니다.                    
              this.stompClient.subscribe("/subscribe/play/room/" + this.roomNum, res => {
                // 받은 데이터를 json으로 파싱하고 리스트에 넣어줍니다.
                this.receiveMessage(res.body);
              });

              this.stompClient.subscribe("/subscribe/play/room/" + this.roomNum + "/" + this.userName, res => {
                // 받은 데이터를 json으로 파싱하고 리스트에 넣어줍니다.
                this.receiveMessage(res.body);
              });

              this.$http
                .get("/api/v1/room/check/ptlist?number=" + this.roomNum)
                .then((res) => {
                  for (const user of res.data.result) {
                    let found = this.userList.find((iter) => iter === user);
                    if (found === undefined)
                      this.userList.push(user);
                  }
                })
                .catch((error) => {console.log(error);});

              this.sendJoinMessage("JOIN");
            },
            error => {
                // 소켓 연결 실패
                console.log('소켓 연결 실패', error);
                this.connected = false;
            }
        );
    },
    unLoadEvent(event) {
      if (this.stompClient !== null) {
        this.sendJoinMessage("UNJOIN");
        this.stompClient.unsubscribe("/subscribe/play/room/" + this.roomNum, {});
        this.stompClient.unsubscribe("/subscribe/play/room/" + this.roomNum + "/" + this.userName, {});
        this.stompClient.disconnect();
        this.stompClient = null;
      }
    },
    submit(event) {    
      let ans;

      this.show = true;

      if (event.target.id === "true" || event.target.id === "one")
        ans = "1";
      else if (event.target.id === "false" || event.target.id === "two")
        ans = "2";
      else if (event.target.id === "three")
        ans = "3";
      else if (event.target.id === "four")
        ans = "4";

      this.sendMessage("ANSWER", ans);
    },
    receiveMessage(resBody) {
      let msg = JSON.parse(resBody);

      if (msg.type === "JOIN") {
        let found = this.userList.find((user) => user === msg.participantName);
        if (found === undefined)
          this.userList.push(msg.participantName);
      } else if (msg.type === "UNJOIN") {
        this.userList = this.userList.filter((user) => user !== msg.participantName);
      } else if (msg.type === "START") {
        this.show = true;
      } else if (msg.type === "WAITING") {
        this.pageType = 0;
        this.show = false;
      } else if (msg.type === "LOADING") {
        this.show = true;
      }else if (msg.type === "OXP") {
        this.show = false;
        this.pageType = 1;
      } else if (msg.type === "OBP") {
        this.show = false;
        this.pageType = 2;
        this.answer1Text = msg.answer1Text;
        this.answer2Text = msg.answer2Text;
        this.answer3Text = msg.answer3Text;
        this.answer4Text = msg.answer4Text;
      } else if (msg.type === "TIMEOUT") {
        this.pageType = 3;
        this.resType = 2;
      } else if (msg.type === "CORRECT") {
        this.show = false;
        this.pageType = 3;
        this.resType = 0;
        this.score = msg.score;
      } else if (msg.type === "INCORRECT") {
        this.show = false;
        this.pageType = 3;
        this.resType = 1;
      } else if (msg.type === "CHAT") {

      } else if (msg.type === "FINALRANK") {
        this.grade = msg.content;
        this.pageType = 4;
      } else if (msg.type === "EXIT") {
        this.$router.go(-1);
      }
    },
    sendJoinMessage(type) {
      if (this.stompClient && this.stompClient.connected) {
        const msg = {
          roomNumber: this.roomNum,
          participantName: this.userName,
          type: type,
        };

        this.stompClient.send("/publish/play/join", JSON.stringify(msg));
      }
    },
    sendMessage(type, message) {
      if (this.stompClient && this.stompClient.connected) {
        const msg = {
          roomNumber: this.roomNum,
          participantName: this.userName,
          content: message,
          type: type,
        };

        this.stompClient.send("/publish/play/message", JSON.stringify(msg));
      }
    },
  },
}
</script>

<style>
#true, #false {
  font-size: 100px;
  font-weight: bold;
}

#one, #two, #three, #four {
  color: white;
  font-size: 100px;
  font-weight: bold;
}

.h-center {
  float: none; 
  margin: 0 auto
}
</style>