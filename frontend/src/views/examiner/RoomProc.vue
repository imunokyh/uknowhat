<template>
  <b-overlay :show="show" rounded="sm" class="h-100">
    <!-- 대기실 페이지 -->
    <div v-if="pageType===0" class="h-100">
      <h2>{{ roomNum }}번 방</h2>
      <hr class="my-4" />
      <h1>You're in!</h1>
      <h2>See your nickname on screen?</h2>
      <hr class="my-4" />
      <b-button variant="primary" class="col-sm-1 mr-5 float-right" @click="sendStart($event)">Start</b-button>
      <b-button variant="danger" class="col-sm-1 mr-3 float-right" @click="sendExit($event)">Exit</b-button>
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
    <div v-else-if="pageType===1" class="h-100">
      <div class="h-50">
        <div class="h-25">
          <div>
            <h2>{{ roomNum }}번 방</h2>
          </div>
          <b-button variant="primary" class="col-sm-1 mt-4 mr-5 float-right" @click="sendNext($event)">Next</b-button>
        </div>
        <div class="h-75">
          <h2 class="tbb-70 mt-5 ml-5 mr-5">{{probList[currentProbNum].questionText}}</h2>
        </div>
      </div>
      <div class="h-50">
        <div v-if="probList[currentProbNum].questionType==='OX'" class="h-100">
          <div class="h-100">
            <b-button id="true" variant="primary" class="col-sm-6 h-100">O</b-button>
            <b-button id="false" variant="danger" class="col-sm-6 h-100">X</b-button>
          </div>
        </div>
        <div v-else-if="probList[currentProbNum].questionType==='OB'" class="h-100">
          <div class="h-50">
            <b-button id="one" variant="primary" class="col-sm-6 h-100">{{probList[currentProbNum].answer1Text}}</b-button>
            <b-button id="two" variant="danger" class="col-sm-6 h-100">{{probList[currentProbNum].answer2Text}}</b-button>
          </div>
          <div class="h-50">
            <b-button id="three" variant="success" class="col-sm-6 h-100">{{probList[currentProbNum].answer3Text}}</b-button>
            <b-button id="four" variant="warning" class="col-sm-6 h-100">{{probList[currentProbNum].answer4Text}}</b-button>
          </div>
        </div>
      </div>
    </div>
  </b-overlay>
</template>

<script>
import Stomp from "webstomp-client";
import SockJS from "sockjs-client";

export default {
  name: "RoomProc",
  props: {
    identification: {
      type: Number,
      default: "",
    },
    number: {
      type: String,
      default: "",
    },
    examiner: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      roomId: -1,
      roomNum: "",
      examinerId: "",
      message: "",
      show: true,
      pageType: 0,
      userList: [],
      probList: [],
      currentProbNum: 0
    };
  },
  created() {
    window.addEventListener("beforeunload", this.unLoadEvent);

    this.roomId = this.identification;
    this.roomNum = this.number;
    this.examinerId = this.examiner;

    this.$http
      .get("/api/v1/room/check/ptlist?number=" + this.roomNum)
      .then((res) => {
        for (const user of res.data.result) {
          this.userList.push(user);
        }
      })
      .catch((error) => {console.log(error);});

    this.connect();
  },
  destroyed() {
    if (this.stompClient !== null) {
      this.sendMessage("EXIT", "Room Exit");
      this.stompClient.unsubscribe("/subscribe/play/room/" + this.roomNum, {});
      this.stompClient.disconnect();
      this.stompClient = null;
    }
  },
  methods: {
    connect() {
      let sock = new SockJS("/stomp");
      this.stompClient = Stomp.over(sock, {debug: false});
      console.log("소켓 연결을 시도합니다.");
      this.stompClient.connect(
        {},
        (frame) => {
          // 소켓 연결 성공
          this.connected = true;
          this.show = false;

          this.stompClient.subscribe("/subscribe/play/room/" + this.roomNum, res => {
              this.recvMessage(res.body);
          });
        },
        error => {
          // 소켓 연결 실패
          console.log("소켓 연결 실패", error);
          this.connected = false;
        }
      );
    },
    unLoadEvent(event) {
      if (this.stompClient !== null) {
        this.stompClient.unsubscribe("/subscribe/chat/room/" + this.roomNum, {});
        this.stompClient.disconnect();
        this.stompClient = null;
      }
    },
    sendMessage(type, message) {
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
    sendMessage(type, message, answer1Text, answer2Text, answer3Text, answer4Text) {
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
    recvMessage(resBody) {
      let msg = JSON.parse(resBody);

      if (msg.type === "JOIN") {
        let found = this.userList.find((user) => user === msg.participantName);
        if (found === undefined)
          this.userList.push(msg.participantName);
      } else if (msg.type === "UNJOIN") {
        this.userList = this.userList.filter((user) => user !== msg.participantName);
      } else if (msg.type === "READPROB") {
        this.probList = msg.result;
        this.show = false;
        this.pageType = 1;
        this.sendNext();
      }
    },
    sendStart(event) {
      if (this.userList.length > 0) {
        this.sendMessage("START", "Room Start");
        this.sendMessage("READPROB", "Read Problem");
        this.show = true;
      } else {
        alert("아직 참가자가 없습니다.");
      }
    },
    sendExit(event) {
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
      };

      this.$http
        .put("/api/v1/room/state", {
          "id": this.roomId,
          "state": "END"
        }, config)
        .then((res) => {
          if (res.data.code !== 0) {
            alert(res.data.message);
          } else {
            alert(this.roomNum + "방이 종료되었습니다.");
          }
          this.$router.go(-1);
        })
        .catch((error) => { console.log(error); });
    },
    sendNext(event) {
      if (event !== undefined)
        this.currentProbNum++;

      if (this.currentProbNum < this.probList.length) {
        if (this.probList[this.currentProbNum].questionType === "OX")
          this.sendMessage("OXP", this.probList[this.currentProbNum].questionText);
        else if (this.probList[this.currentProbNum].questionType === "OB")
          this.sendMessage("OBP", this.probList[this.currentProbNum].questionText,
            this.probList[this.currentProbNum].answer1Text, this.probList[this.currentProbNum].answer2Text,
            this.probList[this.currentProbNum].answer3Text, this.probList[this.currentProbNum].answer4Text);  
      } else {
        this.currentProbNum = 0;
        this.pageType = 0;
        this.sendMessage("WAITING", "Go to Waiting");
      }
    },
  },
};
</script>

<style>
#true, #false {
  font-size: 100px;
  font-weight: bold;
}

#one, #two, #three, #four {
  font-size: 60px;
  font-weight: bold;
}

.twb-50 {
  color: white;
  font-size: 50px;
  font-weight: bold;
  text-align: center;
}

.tbb-70 {
  color: black;
  font-size: 70px;
  font-weight: bold;
  text-align: center;
}
</style>