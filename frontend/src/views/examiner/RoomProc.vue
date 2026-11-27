<template>
  <div class="uk-game proc">
    <div v-if="reconnecting" class="uk-banner">
      <span class="spin"></span>서버와 연결이 끊어졌어요. 다시 연결하는 중... (게임은 서버에서 계속 진행됩니다)
    </div>
    <div v-if="show" class="proc-loading"><div class="uk-spinner"></div></div>

    <!-- 상단 바 -->
    <header class="proc-bar">
      <div class="left">
        <span class="pin-sm">{{ roomNum }}</span>
        <span v-if="pageType !== 0 && probList.length" class="progress-text">
          {{ Math.min(currentProbNum + 1, probList.length) }} / {{ probList.length }}
        </span>
      </div>
      <div class="right">
        <button class="uk-btn glass sm" @click="open = true">💬 채팅</button>
        <template v-if="pageType===0">
          <button class="uk-btn glass sm" @click="sendExit($event)">방 종료</button>
        </template>
        <template v-else-if="pageType===1">
          <button v-if="currentProbNum < probList.length - 1" class="uk-btn accent sm" :disabled="nextDisable" @click="sendNext($event)">
            {{ showProb ? "문제 마감" : "순위 보기" }} →
          </button>
          <button v-else-if="currentProbNum === probList.length - 1" class="uk-btn accent sm" :disabled="resultDisable" @click="sendResult($event)">
            {{ showProb ? "문제 마감" : "최종 결과" }} 🏁
          </button>
        </template>
        <template v-else-if="pageType===2">
          <button v-if="currentProbNum < probList.length" class="uk-btn accent sm" :disabled="nextDisable" @click="sendNext($event)">다음 문제 →</button>
          <button v-else class="uk-btn danger sm" @click="sendExit($event)">방 종료</button>
        </template>
      </div>
    </header>

    <!-- 대기실 -->
    <section v-if="pageType===0" class="lobby">
      <div class="join-box">
        <p class="join-label">참여 방법: 이 사이트에 접속 후 방 번호 입력</p>
        <div class="uk-pin pin-big">{{ roomNum }}</div>
      </div>

      <div class="lobby-head">
        <span class="count">👥 {{ userList.length }}명 참가</span>
        <button class="uk-btn accent lg" :disabled="userList.length === 0" @click="sendStart($event)">게임 시작 ▶</button>
      </div>

      <div v-if="userList.length === 0" class="waiting-empty">
        <div class="uk-spinner"></div>
        <p>참가자를 기다리는 중...</p>
      </div>
      <div v-else class="players">
        <span v-for="user in userList" :key="user" class="uk-chip" :class="{ offline: isOffline(user) }">
          <span class="dot"></span>{{ user }}
        </span>
      </div>
    </section>

    <!-- 문제 공개 전 카운트다운 -->
    <section v-else-if="pageType===3" class="intro">
      <div class="intro-meta">
        <span class="uk-badge" :class="currentProb.questionType === 'OX' ? 'ox' : 'ob'">
          {{ currentProb.questionType === 'OX' ? 'OX 퀴즈' : '4지선다' }}
        </span>
        <span class="meta-item">⭐ {{ currentProb.questionScore }}점</span>
        <span class="meta-item">⏱ {{ currentProb.questionTime > 0 ? currentProb.questionTime + '초' : '제한 없음' }}</span>
      </div>
      <h1 class="intro-question">{{ currentProb.questionText }}</h1>
      <div :key="showProbTimerCnt" class="uk-countdown small">{{ showProbTimerCnt }}</div>
    </section>

    <!-- 문제 진행 / 결과 -->
    <section v-else-if="pageType===1" class="question">
      <div class="q-main">
        <div v-if="showProb" class="q-live">
          <div class="uk-timer" :class="{ urgent: timerCnt > 0 && timerCnt <= 5 }">
            {{ currentProb.questionTime > 0 ? timerCnt : '∞' }}<small>남은 시간</small>
          </div>
          <div class="q-card">
            <h2>{{ currentProb.questionText }}</h2>
          </div>
          <div class="submit-count">
            <strong>{{ submitNum }}</strong>
            <span>/ {{ userList.length }} 제출</span>
          </div>
        </div>

        <div v-else class="q-result">
          <div class="result-card">
            <div class="result-head">
              <h3>{{ answerUserShow ? '정답자 (빠른 순)' : '선택 분포' }}</h3>
              <button class="uk-btn ghost sm" @click="changeChart($event)">
                {{ answerUserShow ? '📊 분포 보기' : '🏃 정답자 보기' }}
              </button>
            </div>
            <div v-if="answerUserShow" class="answer-users">
              <p v-if="answerUserList.length === 0" class="uk-empty">정답자가 없어요 😢</p>
              <ol v-else class="uk-rank-list">
                <li v-for="(u, idx) in answerUserList" :key="u.value" class="uk-rank-item" :style="{ animationDelay: idx * 60 + 'ms' }">
                  <span class="rank">{{ idx + 1 }}</span>
                  <span class="name">{{ u.value }}</span>
                  <span class="score">{{ u.time }}</span>
                </li>
              </ol>
            </div>
            <div v-else class="chart-box">
              <bar-comp :chartData="barData" :chartOptions="options"></bar-comp>
            </div>
          </div>
        </div>
      </div>

      <div class="q-answers">
        <div v-if="currentProb.questionType==='OX'" class="uk-answers">
          <button class="uk-answer a2 ox" disabled :class="tileClass('true')">O</button>
          <button class="uk-answer a1 ox" disabled :class="tileClass('false')">X</button>
        </div>
        <div v-else-if="currentProb.questionType==='OB'" class="uk-answers">
          <button class="uk-answer a1" disabled :class="tileClass('one')"><span class="num">1</span>{{currentProb.answer1Text}}</button>
          <button class="uk-answer a2" disabled :class="tileClass('two')"><span class="num">2</span>{{currentProb.answer2Text}}</button>
          <button class="uk-answer a3" disabled :class="tileClass('three')"><span class="num">3</span>{{currentProb.answer3Text}}</button>
          <button class="uk-answer a4" disabled :class="tileClass('four')"><span class="num">4</span>{{currentProb.answer4Text}}</button>
        </div>
      </div>
    </section>

    <!-- 순위 -->
    <section v-else-if="pageType===2" class="ranking">
      <div class="rank-head">
        <div class="stage-icon">{{ currentProbNum >= probList.length ? '🏆' : '📈' }}</div>
        <h1>{{ currentProbNum >= probList.length ? '최종 순위' : '현재 순위' }}</h1>
      </div>
      <ol class="uk-rank-list rank-board">
        <li v-for="(item, idx) in rankList" :key="item.value" class="uk-rank-item"
            :class="'r' + item.rank" :style="{ animationDelay: idx * 70 + 'ms' }">
          <span class="rank">{{ item.rank }}</span>
          <span class="name">{{ item.value }}</span>
          <span class="score">{{ formatScore(item.score) }}점</span>
        </li>
      </ol>
    </section>

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
  </div>
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
            color: '#1d1638',
            font: {
              size: 22,
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
                fontSize: 18,
                fontStyle: "bold",
                fontColor: "#1d1638"
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
    // 결과 화면에서 정답 보기는 강조, 나머지는 흐리게
    tileClass(key) {
      if (this.showProb) return {};
      return { dim: this.bdisable[key], correct: !this.bdisable[key] };
    },
    formatScore(score) {
      const n = parseFloat(score);
      return isNaN(n) ? score : Math.round(n).toLocaleString();
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
.proc {
  display: flex;
  flex-direction: column;
}
.proc-loading {
  position: fixed; inset: 0; z-index: 1900;
  display: flex; align-items: center; justify-content: center;
  background: rgba(30, 10, 80, 0.45);
}
.proc-bar {
  position: relative; z-index: 2;
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 22px;
  background: rgba(0, 0, 0, 0.18);
}
.proc-bar .left, .proc-bar .right { display: flex; align-items: center; gap: 10px; }
.pin-sm {
  background: #fff; color: var(--uk-text);
  font-weight: 900; letter-spacing: 0.1em;
  padding: 4px 14px; border-radius: 10px; font-size: 18px;
}
.progress-text { font-weight: 800; font-size: 17px; color: rgba(255, 255, 255, 0.9); }

/* lobby */
.lobby { position: relative; z-index: 1; flex: 1; padding: 28px 24px 40px; display: flex; flex-direction: column; align-items: center; }
.join-box {
  background: rgba(255, 255, 255, 0.12); border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: 24px; padding: 22px 40px; text-align: center;
}
.join-label { margin: 0 0 12px; color: rgba(255, 255, 255, 0.85); font-weight: 700; }
.pin-big { font-size: clamp(56px, 10vw, 96px); padding: 8px 36px; border-radius: 20px; }
.lobby-head {
  width: 100%; max-width: 1000px;
  display: flex; align-items: center; justify-content: space-between;
  margin: 32px 0 20px; gap: 16px; flex-wrap: wrap;
}
.lobby-head .count { font-size: 22px; font-weight: 900; }
.players { display: flex; flex-wrap: wrap; justify-content: center; gap: 12px; max-width: 1000px; }
.waiting-empty { margin-top: 40px; text-align: center; color: rgba(255, 255, 255, 0.85); font-weight: 700; }
.waiting-empty p { margin-top: 16px; }

/* intro */
.intro { position: relative; z-index: 1; flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 24px; text-align: center; }
.intro-meta { display: flex; gap: 12px; align-items: center; margin-bottom: 20px; }
.meta-item { font-weight: 800; color: rgba(255, 255, 255, 0.9); }
.intro-question {
  background: #fff; color: var(--uk-text);
  border-radius: 24px; padding: 32px 44px; max-width: 1100px;
  font-size: clamp(28px, 4.5vw, 52px); font-weight: 900; line-height: 1.3;
  box-shadow: var(--uk-shadow-lg); white-space: pre-wrap; word-break: keep-all;
}
.uk-countdown.small { font-size: clamp(72px, 12vw, 120px); margin-top: 28px; }

/* question */
.question { position: relative; z-index: 1; flex: 1; display: flex; flex-direction: column; padding: 20px 22px 22px; gap: 20px; min-height: 0; }
.q-main { flex: 1.4; display: flex; min-height: 0; }
.q-live { flex: 1; display: grid; grid-template-columns: auto 1fr auto; align-items: center; gap: 24px; }
.q-card {
  background: #fff; color: var(--uk-text);
  border-radius: 24px; padding: 32px; height: 100%;
  display: flex; align-items: center; justify-content: center;
  box-shadow: var(--uk-shadow-lg);
}
.q-card h2 { font-size: clamp(26px, 4vw, 52px); font-weight: 900; margin: 0; line-height: 1.3; white-space: pre-wrap; word-break: keep-all; }
.submit-count {
  width: 110px; text-align: center;
  background: rgba(255, 255, 255, 0.14); border-radius: 18px; padding: 16px 8px;
}
.submit-count strong { display: block; font-size: 42px; font-weight: 900; line-height: 1; }
.submit-count span { font-size: 13px; font-weight: 700; color: rgba(255, 255, 255, 0.8); }
.q-result { flex: 1; display: flex; justify-content: center; }
.result-card {
  background: #fff; color: var(--uk-text);
  border-radius: 24px; padding: 20px 24px; width: 100%; max-width: 900px;
  display: flex; flex-direction: column; box-shadow: var(--uk-shadow-lg);
}
.result-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
.result-head h3 { margin: 0; font-size: 20px; font-weight: 900; }
.chart-box { flex: 1; position: relative; min-height: 240px; }
.chart-box > div { height: 100%; }
.answer-users { flex: 1; overflow-y: auto; max-height: 360px; }
.answer-users .uk-rank-item { box-shadow: none; background: #f7f5fe; font-size: 17px; padding: 10px 16px; }
.q-answers { flex: 1; min-height: 170px; }

/* ranking */
.ranking { position: relative; z-index: 1; flex: 1; padding: 24px; display: flex; flex-direction: column; align-items: center; }
.rank-head { text-align: center; margin-bottom: 20px; }
.rank-head h1 { font-size: 40px; font-weight: 900; margin: 0; }
.stage-icon { font-size: 56px; }
.rank-board { width: 100%; max-width: 720px; }

@media (max-width: 768px) {
  .q-live { grid-template-columns: 1fr; }
  .submit-count { width: auto; }
}
</style>
