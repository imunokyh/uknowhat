<template>
  <b-overlay :show="show" rounded="lg" class="h-100">
    <!-- 대기실 페이지 -->
    <div v-if="pageType===0" class="h-100">
      <div style="height:10%;">
        <b-card bg-variant="dark" text-variant="white" class="text-center">
          <b-button pill variant="info" @click="open = true" class="ml-3 float-left">채팅</b-button>
          <b-button pill variant="primary" class="col-sm-1 mr-4 float-right" @click="sendStart($event)">Start</b-button>
          <b-button pill variant="danger" class="col-sm-1 mr-2 float-right" @click="sendExit($event)">Exit</b-button>
          <b-card-text class="twb-35 float-center"> {{ roomNum }}번 방 </b-card-text>
        </b-card>
      </div>
      <div style="height:5%;">
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
          <b-button pill variant="info" @click="open = true" class="ml-3 mt-1 float-left">채팅</b-button>
          <b-button variant="dark" class="col-sm-1 mt-1 ml-3 float-left">{{ roomNum }}번 방</b-button>
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
            <b-table v-if="answerUserShow" sticky-header small style="width: 20.8%;" :fields="answerUserFields" :items="answerUserList" responsive="sm">
              <template #cell(order)="data">
                {{data.index + 1}}
              </template>
              <template #cell(nickname)="data">
                {{data.item.value}}
              </template>
              <template #cell(time)="data">
                {{data.item.time}}
              </template>
            </b-table>
            <bar-comp v-else :chartData="barData" :chartOptions="options"></bar-comp>
            <b-button pill class="mb-auto p-2" variant="warning" @click="changeChart($event)">★</b-button>
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
      <b-button pill variant="info" @click="open = true" class="ml-3 mt-1 float-left">채팅</b-button>
      <b-button variant="dark" class="col-sm-1 mt-1 ml-3 float-left">{{ roomNum }}번 방</b-button>
      <b-button v-if="currentProbNum < probList.length" :disabled="nextDisable" variant="primary" class="col-sm-1 mt-1 mr-5 mb-5 float-right" @click="sendNext($event)">Next</b-button>
      <b-button v-else variant="danger" class="col-sm-1 mt-1 mr-5 float-right" @click="sendExit($event)">Exit</b-button>
      <b-table small :fields="fields" :items="rankList" responsive="sm">
        <template #cell(rank)="data">
          <!--{{data.index + 1}}-->
          {{data.item.rank}}
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
          <h3 class="twb-25"> 점수: {{probList[currentProbNum].questionScore}}점 </h3>
          <h3 class="twb-25"> 제한시간: {{probList[currentProbNum].questionTime}}초 </h3>
        </div>
        <div class="h-75 d-flex justify-content-center align-items-center" style="background-color: white;">
          <h2 class="tbb-70 ml-5 mr-5">{{probList[currentProbNum].questionText}}</h2>
        </div>
      </div>
      <div style="height:10%;">
      </div>
    </div>
    <window-popup v-model="open">
      <beautiful-chat
        :participants="participants"
        :onMessageWasSent="onMessageWasSent"
        :messageList="messageList"
        :isOpen="true"
        :showEmoji="true"
        :showFile="false"
        :showEdition="false"
        :showDeletion="false"
        :showLauncher="false"
        :showCloseButton="false"
        :colors="colors"
        :alwaysScrollToBottom="false"
        :disableUserListToggle="true"
        :messageStyling="true" />
    </window-popup>
  </b-overlay>
</template>

<script>
import Stomp from "webstomp-client";
import SockJS from "sockjs-client";
import BarComp from '@/components/BarComp'
import WindowPopup from "@/components/WindowPopup.vue";

export default {
  name: "RoomProc",
  components: {
    BarComp,
    WindowPopup,
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
      open: false,
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
      answerUserList: [],
      answerUserFields: [
        'order',
        'nickname',
        'time'
      ],
      answerUserShow: false,
      currentProbNum: 0,
      submitNum: 0,
      timerCnt: 0,
      showProbTimerCnt: 5,
      chartData: null,
      barData: null,
      options: {
        plugins: {
          datalabels: {
            color: 'black',
            font: {
              size: 20,
              weight: 'bold'
            },
            anchor: 'end',
            align: 'top',
            offset: 10,
          }
        },
        layout: {
          padding: {
            top: 50,
            right: 0,
            left: 0,
            bottom: 0
          }
        },
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
                stepSize : 5,
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
      },
      participants: [],
      messageList: [],
      colors: {
          header: {
              bg: '#4e8cff',
              text: '#ffffff'
          },
          launcher: {
              bg: '#4e8cff'
          },
          messageList: {
              bg: '#ffffff'
          },
          sentMessage: {
              bg: '#4e8cff',
              text: '#ffffff'
          },
          receivedMessage: {
              bg: '#eaeaea',
              text: '#222222'
          },
          userInput: {
              bg: '#f4f7f9',
              text: '#565867'
          }
      },
    };
  },
  created() {
    window.addEventListener("beforeunload", this.unLoadEvent);

    history.pushState(null, document.title, location.href);
    window.addEventListener('popstate', function(event) {
      history.pushState(null, document.title, location.href);
    });

    document.oncontextmenu = function(e) {
      if (e) {
        e.preventDefault();
      } else {
        event.keyCode = 0;
        event.returnValue = false;
      }
    }

    document.onkeydown = function(e) {
      let key = (e) ? e.keyCode : event.keyCode;
      let ctrl = (e) ? e.ctrlKey  : event.ctrlKey;
      
      if ((ctrl == true && (key == 78 || key == 82)) || key==116) {
        if (e) {
          e.preventDefault();
        } else {
          event.keyCode = 0;
          event.returnValue = false;
        }
      }
    }

    this.roomId = this.identification;
    this.roomNum = this.number;
    this.examinerId = this.examiner;

    this.$http
      .get("/api/v1/room/check/ptlist?number=" + this.roomNum)
      .then((res) => {
        for (const user of res.data.result) {
          this.userList.push(user);

          if (user !== this.userName)
            this.participants = [ ...this.participants, { id: user,  name: user } ]
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
    sendChatMessage(chatType, message) {
      if (this.stompClient && this.stompClient.connected) {
        const msg = {
          roomNumber: this.roomNum,
          participantName: '★방 장★',
          content: message,
          chatType: chatType,
          type: 'CHAT',
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

          if (msg.participantName !== this.userName)
            this.participants = [ ...this.participants, { id: msg.participantName,  name: msg.participantName } ]
      } else if (msg.type === "UNJOIN") {
        this.userList = this.userList.filter((user) => user !== msg.participantName);

        this.participants = this.participants.filter((part) => part.id !== msg.participantName)
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
      } else if (msg.type === "ANSUSERLIST") {
        this.answerUserList = msg.result;
      } else if (msg.type === "RANK") {
        this.rankList = msg.result;
        this.answerUserShow = false;
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
      } else if (msg.type === "CHAT") {
        this.recvChatMessage(msg.participantName, msg.chatType, msg.content);
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
          //this.$router.go(-1);
          this.$router.replace({ name: "RoomMng" });
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
        } else if (event !== undefined && this.pageType === 1) {
          this.sendMessage("TIMEROFF", "Timer Off");
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
    changeChart(event) {
      this.answerUserShow = !this.answerUserShow;
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

        if (this.probList[this.currentProbNum].questionTime === 0)
          this.nextDisable = false;

        this.sendMessage("TIMER", this.probList[this.currentProbNum].questionTime);
        this.timerCnt = this.probList[this.currentProbNum].questionTime;
        this.currentUserNum = this.userList.length;
      } else {
        setTimeout(this.timerFunc, 1000); 
      }
    },
    recvChatMessage(nick, msgt, cont) {
      if (cont.length > 0) {
        if (nick !== this.userName) {
          this.newMessagesCount = this.isChatOpen ? this.newMessagesCount : this.newMessagesCount + 1

          let convMsg = "【" + nick + "】 : " + cont;
          if (msgt === 'text')
            this.messageList = [ ...this.messageList, { author: nick, type: msgt, data: { text: convMsg } } ]
          else if (msgt === 'emoji')
            this.messageList = [ ...this.messageList, { author: nick, type: msgt, data: { emoji: convMsg } } ]
        }
      }
    },
    onMessageWasSent(message) {
      if (message.type === 'text')
        this.sendChatMessage(message.type, message.data.text)
      else if (message.type === 'emoji')
        this.sendChatMessage(message.type, message.data.emoji)
      
      this.messageList = [ ...this.messageList, message ]
    },
  },
};
</script>

<style scoped>
#true, #false {
  font-size: 100px;
  font-weight: bold;
}

#one, #two, #three, #four {
  color: white;
  font-size: 60px;
  font-weight: bold;
}
</style>

<style>
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

.twb-25 {
  color: white;
  font-size: 25px;
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