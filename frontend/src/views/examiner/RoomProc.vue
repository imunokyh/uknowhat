<template>
  <b-overlay :show="show" rounded="lg" class="h-100">
    <!-- 대기실 페이지 -->
    <div v-if="pageType===0" class="h-100">
      <div style="height:10%;">
        <b-card bg-variant="dark" text-variant="white" class="text-center">
          <b-card-text class="twb-35"> {{ roomNum }}번 방 </b-card-text>
        </b-card>
      </div>
      <div style="height:5%;">
        <b-button variant="primary" class="col-sm-1 mr-5 float-right" @click="sendStart($event)">Start</b-button>
        <b-button variant="danger" class="col-sm-1 mr-3 float-right" @click="sendExit($event)">Exit</b-button>
      </div>
      <div style="height:85%;"> 
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
    </div>
    <!-- 게임 진행 페이지 -->
    <div v-else-if="pageType===1" class="h-100">
      <div class="h-75">
        <div style="height:10%;">
          <b-button variant="dark" class="col-sm-1 mt-1 ml-5 float-left">{{ roomNum }}번 방</b-button>
          <b-button variant="white" class="col-sm-1 mt-1 float-center">{{currentProbNum + 1}}/{{probList.length}}</b-button>
          <b-button v-if="currentProbNum < probList.length - 1" :disabled="nextDisable" variant="primary" class="col-sm-1 mt-1 mr-5 float-right" @click="sendNext($event)">Next</b-button>
          <b-button v-else-if="currentProbNum === probList.length - 1" :disabled="resultDisable" variant="success" class="col-sm-1 mt-1 mr-5 float-right" @click="sendResult($event)">Result</b-button>
        </div>
        <div style="height:90%;">
          <div v-if="showProb" class="h-100">
            <div class="row h-100">
              <div class="col-sm-1 h-100 d-flex justify-content-center align-items-center">
                <b-avatar variant="primary" size="5em" :text="timerCnt"></b-avatar>
              </div>
              <div class="col-sm-10 h-100 d-flex justify-content-center align-items-center">
                <h2 class="tbb-70">{{probList[currentProbNum].questionText}}</h2>
              </div>
              <div class="col-sm-1 h-100 d-flex justify-content-center align-items-center">
                <p class="tbb-25">
                    {{submitNum}}<br>
                    Answers
                </p>
              </div>
            </div>
          </div>
          <div v-else class="h-100 d-flex justify-content-center align-items-center">
            <bar-comp :chart-data="barData" :options="options"></bar-comp>
          </div>
        </div>
      </div>
      <div class="h-25">
        <div v-if="probList[currentProbNum].questionType==='OX'" class="h-100">
          <div class="h-100">
            <div class="left h-100">
              <b-button :disabled="bdisable['true']" id="true" variant="primary" class="col-sm-12 h-100">O</b-button>
            </div>
            <div class="right h-100">
              <b-button :disabled="bdisable['false']" id="false" variant="danger" class="col-sm-12 h-100">X</b-button>
            </div>
          </div>
        </div>
        <div v-else-if="probList[currentProbNum].questionType==='OB'" class="h-100">
          <div class="h-50">
            <div class="left h-100">
              <b-button :disabled="bdisable['one']" id="one" variant="primary" class="col-sm-12 h-100">{{probList[currentProbNum].answer1Text}}</b-button>
            </div>
            <div class="right h-100">
              <b-button :disabled="bdisable['two']" id="two" variant="danger" class="col-sm-12 h-100">{{probList[currentProbNum].answer2Text}}</b-button>
            </div>
          </div>
          <div class="h-50">
            <div class="left h-100">
              <b-button :disabled="bdisable['three']" id="three" variant="success" class="col-sm-12 h-100">{{probList[currentProbNum].answer3Text}}</b-button>
            </div>
            <div class="right h-100">
              <b-button :disabled="bdisable['four']" id="four" variant="warning" class="col-sm-12 h-100">{{probList[currentProbNum].answer4Text}}</b-button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 랭킹 페이지 -->
    <div v-else-if="pageType===2" class="h-100">
      <b-button variant="dark" class="col-sm-1 mt-1 ml-5 mb-5 float-left">{{ roomNum }}번 방</b-button>
      <b-button v-if="currentProbNum < probList.length" :disabled="nextDisable" variant="primary" class="col-sm-1 mt-1 mr-5 mb-5 float-right" @click="sendNext($event)">Next</b-button>
      <b-button v-else variant="danger" class="col-sm-1 mt-1 mr-5 float-right" @click="sendExit($event)">Exit</b-button>
      <b-table small :fields="fields" :items="rankList" responsive="sm">
        <template #cell(rank)="data">
          {{data.index + 1}}
        </template>
        <template #cell(nickname)="data">
          {{data.item.value}}
        </template>
        <template #cell(score)="data">
          {{data.item.score}}
        </template>
      </b-table>
    </div>
    <!-- 문제 페이지 -->
    <div v-else-if="pageType===3" class="h-100" style="background-color: #81c147;">
      <div style="height:10%;">
        <p class="twb-50 float-left" style="background-color: green; padding: 0 20px 0">{{currentProbNum + 1}} of {{probList.length}}</p>
        <b-avatar class="float-right mt-2 mr-3" variant="light" size="5em" :text="showProbTimerCnt"></b-avatar>
      </div>
      <div style="height:80%;">
        <div class="h-25">
          <h2 v-if="probList[currentProbNum].questionType==='OX'" class="twb-50">OX 퀴즈</h2>
          <h2 v-else-if="probList[currentProbNum].questionType==='OB'" class="twb-50">4지선다형 퀴즈</h2>
        </div>
        <div class="h-75 d-flex justify-content-center align-items-center" style="background-color: white;">
          <h2 class="tbb-70 ml-5 mr-5">{{probList[currentProbNum].questionText}}</h2>
        </div>
      </div>
      <div style="height:10%;">
      </div>
    </div>
  </b-overlay>
</template>

<script>
import Stomp from "webstomp-client";
import SockJS from "sockjs-client";
import BarComp from '@/components/BarComp'

export default {
  name: "RoomProc",
  components: {
    BarComp,
  },
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
      showProb: true,
      nextDisable: false,
      resultDisable: true,
      currentUserNum: 0,
      userList: [],
      probList: [],
      rankList: [],
      fields: [
        'rank',
        'nickname',
        'score'
      ],
      currentProbNum: 0,
      submitNum: 0,
      timerCnt: 0,
      showProbTimerCnt: 5,
      chartData: null,
      barData: null,
      options: {
        // title
        title: {
          display: false,
          text: "결과",
          fontSize : 10,
          fontStyle: "bold"
        },
        scales: {
          yAxes: [
            {
              ticks: {
                display: false,
                beginAtZero: true,
              },
              gridLines: {
                display: false,
              },
            },
          ],
          xAxes: [
            {
              ticks: {
                beginAtZero: true,
                fontSize: 20,
                fontStyle: "bold"
              },
              gridLines: {
                display: false,
              },
            },
          ],
        },
        legend: {
          display: false,
        },
        responsive: true,
        maintainAspectRatio: false,
      },
      bdisable: {
        "true": false,
        "false": false,
        "one": false,
        "two": false,
        "three": false,
        "four": false,
      }
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
      } else if (msg.type === "ANSWER") {
        this.submitNum++;
        if (this.submitNum === this.currentUserNum)
          this.sendMessage("TIMEROFF", "Timer Off");
      } else if (msg.type === "ANSCHART") {
        this.showProb = false;
        this.chartData = msg.result;
        if (this.probList[this.currentProbNum].questionType === "OX") {
          let answer1 = 0, answer2 = 0;

          for (var key in this.chartData) {
            if (key === '1') {
              answer1 = this.chartData[key];
            } else if (key === '2') {
              answer2 = this.chartData[key];
            }
          }

          if (this.probList[this.currentProbNum].questionAnswer === "1") {
            this.bdisable['true'] = false;
            this.bdisable['false'] = true;
          } else if (this.probList[this.currentProbNum].questionAnswer === "2") {
            this.bdisable['true'] = true;
            this.bdisable['false'] = false;
          }

          this.fillOXData(answer1, answer2);
        } else if (this.probList[this.currentProbNum].questionType === "OB") {
          let answer1 = 0, answer2 = 0, answer3 = 0, answer4 = 0;

          for (var key in this.chartData) {
            if (key === '1') {
              answer1 = this.chartData[key];
            } else if (key === '2') {
              answer2 = this.chartData[key];
            } else if (key === '3') {
              answer3 = this.chartData[key];
            } else if (key === '4') {
              answer4 = this.chartData[key];
            }
          }

          if (this.probList[this.currentProbNum].questionAnswer === "1") {
            this.bdisable['one'] = false;
            this.bdisable['two'] = true;
            this.bdisable['three'] = true;
            this.bdisable['four'] = true;
          } else if (this.probList[this.currentProbNum].questionAnswer === "2") {
            this.bdisable['one'] = true;
            this.bdisable['two'] = false;
            this.bdisable['three'] = true;
            this.bdisable['four'] = true;
          } else if (this.probList[this.currentProbNum].questionAnswer === "3") {
            this.bdisable['one'] = true;
            this.bdisable['two'] = true;
            this.bdisable['three'] = false;
            this.bdisable['four'] = true;
          } else if (this.probList[this.currentProbNum].questionAnswer === "4") {
            this.bdisable['one'] = true;
            this.bdisable['two'] = true;
            this.bdisable['three'] = true;
            this.bdisable['four'] = false;
          }

          this.fillOBData(this.probList[this.currentProbNum].answer1Text, answer1, 
                          this.probList[this.currentProbNum].answer2Text, answer2, 
                          this.probList[this.currentProbNum].answer3Text, answer3, 
                          this.probList[this.currentProbNum].answer4Text, answer4);
        }
      } else if (msg.type === "RANK") {
        this.rankList = msg.result;
      } else if (msg.type === "TIMECNT") {
        this.timerCnt = parseInt(msg.content);
      } else if (msg.type === "TIMEOUT") {
        this.sendMessage("ANSCHK", "Answer Check");
        this.sendMessage("ANSCHART", "Answer Chart");
        this.nextDisable = false;

        if (this.currentProbNum === this.probList.length - 1)
          this.resultDisable = false;

      } else if (msg.type === "OXP" || msg.type === "OBP") {
        this.pageType = 1;
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
    sendResult(event) {
      if (this.pageType !== 2) {
        if (this.showProb === false) {
          this.pageType = 2;
          this.sendMessage("RANK", "Rank Check");
          this.sendMessage("FINALRANK", "Send Final Rank");
        }
      }

      this.currentProbNum++;
    },
    sendNext(event) {
      if (this.pageType !== 2) {
        if (this.showProb === false) {
          this.pageType = 2;
          this.sendMessage("RANK", "Rank Check");
          return;
        }
      } else {
        this.pageType = 1;
      }

      if (event !== undefined)
        this.currentProbNum++;

      if (this.currentProbNum < this.probList.length) {
        this.showProbTimerCnt = 5;
        this.pageType = 3;

        this.sendMessage("LOADING", "Please Loading");
        setTimeout(this.timerFunc, 1000);
      } else {
        this.sendResult();
      }

      for (var key in this.bdisable) {
        this.bdisable[key] = false;
      }

      this.nextDisable = true;
      this.submitNum = 0;
      this.showProb = true;
    },
    fillOXData(answer1Count, answer2Count) {
      (this.barData = {
        labels: ["O", "X"],
        datasets: [
          {
            label: "선택 수",
            backgroundColor: ["#0d6efd", "#dc3545"],
            data: [answer1Count, answer2Count],
          },
        ],
      });
    },
    fillOBData(answer1Text, answer1Count, answer2Text, answer2Count,
            answer3Text, answer3Count, answer4Text, answer4Count) {
      (this.barData = {
        labels: [answer1Text, answer2Text, answer3Text, answer4Text],
        datasets: [
          {
            label: "선택 수",
            backgroundColor: ["#0d6efd", "#dc3545", "#198754", "#ffc107"],
            data: [answer1Count, answer2Count, answer3Count, answer4Count],
          },
        ],
      });
    },
    timerFunc() {
      this.showProbTimerCnt--;

      if (this.showProbTimerCnt === 0) {
        if (this.probList[this.currentProbNum].questionType === "OX")
          this.sendMessage("OXP", this.probList[this.currentProbNum].id.toString());
        else if (this.probList[this.currentProbNum].questionType === "OB")
          this.sendMessage("OBP", this.probList[this.currentProbNum].id.toString()); 

        this.sendMessage("TIMER", this.probList[this.currentProbNum].questionTime);
        this.timerCnt = this.probList[this.currentProbNum].questionTime;
        this.currentUserNum = this.userList.length;
      } else {
        setTimeout(this.timerFunc, 1000); 
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

.twb-35 {
  color: white;
  font-size: 35px;
  font-weight: bold;
  text-align: center;
}

.tbb-70 {
  color: black;
  font-size: 70px;
  font-weight: bold;
  text-align: center;
}

.tbb-25 {
  color: black;
  font-size: 25px;
  font-weight: bold;
  text-align: center;
}

div.left {
  width: 50%;
  float: left;
}

div.right {
  width: 50%;
  float: right;
}
</style>