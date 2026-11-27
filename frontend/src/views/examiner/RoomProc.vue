<template>
  <b-overlay :show="show" rounded="lg" class="h-100">
    <b-alert :show="reconnecting" variant="warning" class="m-0 text-center" style="position: fixed; top: 0; left: 0; right: 0; z-index: 2000;">
      서버와 연결이 끊어졌습니다. 재연결 중입니다... (게임은 서버에서 계속 진행됩니다)
    </b-alert>
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
              <b-card :bg-variant="isOffline(user) ? 'secondary' : 'dark'" text-variant="white" class="text-center m-1">
                <b-card-text> {{user}} <small v-if="isOffline(user)">(연결 끊김)</small> </b-card-text>
              </b-card>
            </b-col>
          </b-row>
        </b-container>
      </div>
    </div>
    <!-- 게임 진행 페이지 -->
    <div v-else-if="pageType===1" class="h-100">
      <div style="height:70%;">
        <div style="height:10%;">
          <b-button pill variant="info" @click="open = true" class="ml-3 mt-1 float-left">채팅</b-button>
          <b-button variant="dark" class="col-sm-1 mt-1 ml-3 float-left">{{ roomNum }}번 방</b-button>
          <b-button variant="white" class="col-sm-1 mt-1 float-center">{{currentProbNum + 1}}/{{probList.length}}</b-button>
          <b-button v-if="currentProbNum < probList.length - 1" :disabled="nextDisable" variant="primary" class="col-sm-1 mt-1 mr-5 float-right" @click="sendNext($event)">Next</b-button>
          <b-button v-else-if="currentProbNum === probList.length - 1" :disabled="resultDisable" variant="success" class="col-sm-1 mt-1 mr-5 float-right" @click="sendResult($event)">Result</b-button>
        </div>
        <div style="height:90%; white-space:pre-wrap;">
          <div v-if="showProb" class="h-100">
            <div class="row h-100">
              <div class="col-sm-1 h-100 d-flex justify-content-center align-items-center">
                <b-avatar variant="primary" size="5em" :text="timerCnt"></b-avatar>
              </div>
              <div class="col-sm-10 h-100 d-flex justify-content-center align-items-center">
                <h2 class="tbb-70">{{currentProb.questionText}}</h2>
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
      <div style="height:30%;">
        <div v-if="currentProb.questionType==='OX'" class="h-100">
          <div class="h-100">
            <div class="left h-100">
              <b-button :disabled="bdisable['true']" id="true" variant="primary" class="col-sm-12 h-100">O</b-button>
            </div>
            <div class="right h-100">
              <b-button :disabled="bdisable['false']" id="false" variant="danger" class="col-sm-12 h-100">X</b-button>
            </div>
          </div>
        </div>
        <div v-else-if="currentProb.questionType==='OB'" class="h-100">
          <div class="h-50">
            <div class="left h-100">
              <b-button :disabled="bdisable['one']" id="one" variant="primary" class="col-sm-12 h-100">{{currentProb.answer1Text}}</b-button>
            </div>
            <div class="right h-100">
              <b-button :disabled="bdisable['two']" id="two" variant="danger" class="col-sm-12 h-100">{{currentProb.answer2Text}}</b-button>
            </div>
          </div>
          <div class="h-50">
            <div class="left h-100">
              <b-button :disabled="bdisable['three']" id="three" variant="success" class="col-sm-12 h-100">{{currentProb.answer3Text}}</b-button>
            </div>
            <div class="right h-100">
              <b-button :disabled="bdisable['four']" id="four" variant="warning" class="col-sm-12 h-100">{{currentProb.answer4Text}}</b-button>
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
      <div style="height:80%; white-space:pre-wrap;">
        <div class="h-25">
          <h2 v-if="currentProb.questionType==='OX'" class="twb-50">OX 퀴즈</h2>
          <h2 v-else-if="currentProb.questionType==='OB'" class="twb-50">4지선다형 퀴즈</h2>
          <h3 class="twb-25"> 점수: {{currentProb.questionScore}}점 </h3>
          <h3 class="twb-25"> 제한시간: {{currentProb.questionTime}}초 </h3>
        </div>
        <div class="h-75 d-flex justify-content-center align-items-center" style="background-color: white;">
          <h2 class="tbb-70 ml-5 mr-5">{{currentProb.questionText}}</h2>
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
import GameSocket from "@/utils/gameSocket";
import { saveExaminer, loadExaminer, clearExaminer, errorMessage } from "@/utils/gameSession";
import BarComp from '@/components/BarComp'
import WindowPopup from "@/components/WindowPopup.vue";

const EXAMINER_NAME = "★방 장★";

export default {
  name: "RoomProc",
  components: {
    BarComp,
    WindowPopup,
  },
  props: {
    identification: {
      type: Number,
      default: -1,
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
      socket: null,
      connected: false,
      reconnecting: false,
      leaving: false,
      phase: "WAITING",
      message: "",
      show: true,
      pageType: 0,
      showProb: true,
      nextDisable: false,
      resultDisable: true,
      userList: [],
      offlineUsers: [],
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
  computed: {
    currentProb() {
      return this.probList[this.currentProbNum] || {};
    },
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

    // 새로고침 시 라우터 params 가 사라지므로 저장된 정보로 복원
    if (this.number) {
      this.roomId = this.identification;
      this.roomNum = this.number;
      this.examinerId = this.examiner;
    } else {
      const saved = loadExaminer();
      if (!saved) {
        this.$router.replace({ name: "RoomMng" });
        return;
      }
      this.roomId = saved.roomId;
      this.roomNum = saved.roomNum;
      this.examinerId = saved.examinerId;
    }
    saveExaminer({ roomId: this.roomId, roomNum: this.roomNum, examinerId: this.examinerId });

    this.connect();
  },
  destroyed() {
    // 화면을 벗어나도 게임은 서버에서 계속 진행된다. (방 종료는 Exit 버튼으로만)
    window.removeEventListener("beforeunload", this.unLoadEvent);
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
  },
  methods: {
    authHeader() {
      return { Authorization: `Bearer ${sessionStorage.getItem("token")}` };
    },
    connect() {
      this.socket = new GameSocket({
        headers: () => this.authHeader(),
        subscribe: (client) => {
          client.subscribe("/subscribe/play/room/" + this.roomNum, res => this.recvMessage(res.body));
          client.subscribe("/user/queue/game", res => this.recvMessage(res.body));
          client.subscribe("/user/queue/errors", res => this.recvError(res.body));
        },
        onConnect: () => {
          this.connected = true;
          this.reconnecting = false;
          this.socket.send("/publish/play/examiner", { roomNumber: this.roomNum, type: "JOIN" });
          this.syncState();
        },
        onDisconnect: () => {
          this.connected = false;
          if (!this.leaving) this.reconnecting = true;
        },
      });
      this.socket.connect();
    },
    unLoadEvent(event) {
      if (this.socket) {
        this.socket.close();
        this.socket = null;
      }
    },
    // 서버의 현재 진행 상태로 화면 복원 (최초 접속 / 재접속)
    syncState() {
      this.$http
        .get("/api/v1/play/state", { params: { number: this.roomNum }, headers: this.authHeader() })
        .then((res) => {
          this.applyState(res.data.result);
          this.show = false;
        })
        .catch((error) => {
          this.show = false;
          const status = error.response ? error.response.status : 0;
          if (status === 401) {
            this.leaving = true;
            alert("로그인이 만료되었습니다. 다시 로그인 후 방 관리에서 '이어하기'를 눌러주세요.");
            this.$router.replace({ name: "Login" });
          } else if (status >= 400 && status < 500) {
            this.leaving = true;
            clearExaminer();
            alert(errorMessage(error, "진행 중인 방이 아닙니다."));
            this.$router.replace({ name: "RoomMng" });
          } else {
            console.log(error);
          }
        });
    },
    applyState(state) {
      this.phase = state.phase;
      this.probList = state.questions || [];
      this.currentProbNum = state.questionIndex || 0;

      this.userList = (state.participants || []).map((p) => p.name);
      this.offlineUsers = (state.participants || []).filter((p) => !p.online).map((p) => p.name);
      this.participants = this.userList.map((u) => ({ id: u, name: u }));

      this.resetButtons();
      this.submitNum = state.answerCount || 0;
      this.answerUserShow = false;

      switch (state.phase) {
        case "WAITING":
          this.pageType = 0;
          break;
        case "COUNTDOWN":
          this.pageType = 3;
          this.showProbTimerCnt = state.remainSec;
          this.nextDisable = true;
          this.showProb = true;
          break;
        case "QUESTION":
          this.pageType = 1;
          this.showProb = true;
          this.timerCnt = state.remainSec;
          // 제한시간 없는 문제는 출제자가 Next(마지막 문제는 Result) 로 종료
          this.nextDisable = this.currentProb.questionTime > 0;
          this.resultDisable = this.currentProb.questionTime > 0;
          break;
        case "RESULT":
          this.pageType = 1;
          this.answerUserList = state.answerUserList || [];
          this.showResultChart(state.answerChart || {});
          break;
        case "RANK":
          this.pageType = 2;
          this.rankList = state.rankList || [];
          this.nextDisable = false;
          break;
        case "FINISHED":
          this.pageType = 2;
          this.rankList = state.rankList || [];
          this.currentProbNum = this.probList.length;
          break;
      }
    },
    sendMessage(type, message) {
      const sent = this.socket ? this.socket.send("/publish/play/message", {
        roomNumber: this.roomNum,
        examinerId: this.examinerId,
        content: message,
        type: type,
      }) : false;
      if (!sent)
        alert("서버와 연결이 끊어져 있습니다. 재연결 후 다시 시도해 주세요.\n(진행 중인 문제는 서버에서 계속 진행됩니다)");
      return sent;
    },
    sendChatMessage(chatType, message) {
      return this.socket ? this.socket.send("/publish/play/message", {
        roomNumber: this.roomNum,
        participantName: EXAMINER_NAME,
        content: message,
        chatType: chatType,
        type: 'CHAT',
      }) : false;
    },
    recvError(resBody) {
      let msg;
      try {
        msg = JSON.parse(resBody);
      } catch (e) {
        return;
      }
      this.show = false;
      alert(msg.content);
      // 화면과 서버 상태가 어긋났을 수 있으므로 다시 동기화
      this.syncState();
    },
    recvMessage(resBody) {
      let msg;
      try {
        msg = JSON.parse(resBody);
      } catch (e) {
        console.log("잘못된 메시지", resBody);
        return;
      }

      if (msg.type === "JOIN") {
        let found = this.userList.find((user) => user === msg.participantName);
        if (found === undefined) {
          this.userList.push(msg.participantName);
          this.participants = [ ...this.participants, { id: msg.participantName,  name: msg.participantName } ]
        }
        this.offlineUsers = this.offlineUsers.filter((user) => user !== msg.participantName);
      } else if (msg.type === "UNJOIN") {
        this.userList = this.userList.filter((user) => user !== msg.participantName);
        this.offlineUsers = this.offlineUsers.filter((user) => user !== msg.participantName);
        this.participants = this.participants.filter((part) => part.id !== msg.participantName)
      } else if (msg.type === "OFFLINE") {
        if (!this.offlineUsers.includes(msg.participantName))
          this.offlineUsers.push(msg.participantName);
      } else if (msg.type === "READPROB") {
        this.probList = msg.result;
        this.show = false;
      } else if (msg.type === "LOADING" || msg.type === "COUNTDOWN") {
        if (this.probList.length === 0) {
          // 문제 목록을 못 받은 경우(재접속 직후 등) 서버 상태로 복원
          this.syncState();
          return;
        }
        this.show = false;
        this.phase = "COUNTDOWN";
        this.pageType = 3;
        this.currentProbNum = msg.questionIndex;
        this.showProbTimerCnt = parseInt(msg.content);
        this.resetButtons();
        this.nextDisable = true;
        this.resultDisable = true;
        this.submitNum = 0;
        this.showProb = true;
      } else if (msg.type === "OXP" || msg.type === "OBP") {
        this.phase = "QUESTION";
        this.pageType = 1;
        this.currentProbNum = msg.questionIndex;
        this.showProb = true;
        this.submitNum = 0;
        this.timerCnt = this.currentProb.questionTime;
        this.nextDisable = this.currentProb.questionTime > 0;
        this.resultDisable = this.currentProb.questionTime > 0;
      } else if (msg.type === "ANSWER") {
        this.submitNum = parseInt(msg.content) || this.submitNum + 1;
      } else if (msg.type === "TIMECNT") {
        this.timerCnt = parseInt(msg.content);
      } else if (msg.type === "TIMEOUT") {
        this.phase = "RESULT";
        this.timerCnt = 0;
      } else if (msg.type === "ANSUSERLIST") {
        this.answerUserList = msg.result;
      } else if (msg.type === "ANSCHART") {
        this.phase = "RESULT";
        this.pageType = 1;
        this.showResultChart(msg.result || {});
      } else if (msg.type === "RANK") {
        this.phase = "RANK";
        this.pageType = 2;
        this.rankList = msg.result;
        this.answerUserShow = false;
        this.nextDisable = false;
      } else if (msg.type === "FINISH") {
        this.phase = "FINISHED";
        this.pageType = 2;
        this.currentProbNum = this.probList.length;
      } else if (msg.type === "CHAT") {
        this.recvChatMessage(msg.participantName, msg.chatType, msg.content);
      }
    },
    resetButtons() {
      for (var key in this.bdisable) {
        this.bdisable[key] = false;
      }
    },
    // 채점 결과(정답 분포) 표시
    showResultChart(chartData) {
      const prob = this.currentProb;
      this.showProb = false;
      this.chartData = chartData;
      this.nextDisable = false;
      this.resultDisable = this.currentProbNum !== this.probList.length - 1;

      const count = (key) => chartData[key] || 0;

      if (prob.questionType === "OX") {
        this.bdisable['true'] = prob.questionAnswer !== "1";
        this.bdisable['false'] = prob.questionAnswer !== "2";
        this.fillOXData(count('1'), count('2'));
      } else if (prob.questionType === "OB") {
        this.bdisable['one'] = prob.questionAnswer !== "1";
        this.bdisable['two'] = prob.questionAnswer !== "2";
        this.bdisable['three'] = prob.questionAnswer !== "3";
        this.bdisable['four'] = prob.questionAnswer !== "4";
        this.fillOBData(prob.answer1Text, count('1'), prob.answer2Text, count('2'),
                        prob.answer3Text, count('3'), prob.answer4Text, count('4'));
      }
    },
    sendStart(event) {
      if (this.userList.length > 0) {
        if (this.sendMessage("START", "Room Start"))
          this.show = true;
      } else {
        alert("아직 참가자가 없습니다.");
      }
    },
    sendExit(event) {
      if (this.phase !== "FINISHED" && this.phase !== "WAITING"
          && !confirm("게임이 진행 중입니다. 방을 종료하시겠습니까?"))
        return;

      this.$http
        .put("/api/v1/room/state", {
          "id": this.roomId,
          "state": "END"
        }, { headers: this.authHeader() })
        .then((res) => {
          if (res.data.code !== 0) {
            alert(res.data.message);
            return;
          }
          alert(this.roomNum + "방이 종료되었습니다.");
          this.leaving = true;
          clearExaminer();
          this.$router.replace({ name: "RoomMng" });
        })
        .catch((error) => {
          console.log(error);
          alert(errorMessage(error, "방 종료 중 오류가 발생했습니다. 다시 시도해 주세요."));
        });
    },
    // 마지막 문제 결과 → 최종 순위
    sendResult(event) {
      this.sendNext(event);
    },
    // 다음 단계 진행은 서버가 판단한다. (현재 단계를 함께 보내 중복 클릭 방지)
    sendNext(event) {
      if (this.sendMessage("NEXT", this.phase)) {
        this.nextDisable = true;
        this.resultDisable = true;
      }
    },
    changeChart(event) {
      this.answerUserShow = !this.answerUserShow;
    },
    isOffline(user) {
      return this.offlineUsers.includes(user);
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
    recvChatMessage(nick, msgt, cont) {
      if (cont && cont.length > 0) {
        if (nick !== EXAMINER_NAME) {
          let convMsg = "【" + nick + "】 : " + cont;
          if (msgt === 'text')
            this.messageList = [ ...this.messageList, { author: nick, type: msgt, data: { text: convMsg } } ]
          else if (msgt === 'emoji')
            this.messageList = [ ...this.messageList, { author: nick, type: msgt, data: { emoji: convMsg } } ]
        }
      }
    },
    onMessageWasSent(message) {
      let sent = false;
      if (message.type === 'text')
        sent = this.sendChatMessage(message.type, message.data.text)
      else if (message.type === 'emoji')
        sent = this.sendChatMessage(message.type, message.data.emoji)

      if (!sent) {
        alert("연결이 끊어져 메시지를 보내지 못했습니다.");
        return;
      }
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
  font-size: 40px;
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