<template>
  <div class="uk-game play">
    <div v-if="reconnecting" class="uk-banner">
      <span class="spin"></span>연결이 끊어졌어요. 다시 연결하는 중... (게임은 계속 진행 중이에요)
    </div>

    <!-- 상단 바 -->
    <header class="play-bar">
      <span class="uk-badge">{{ roomNum }}번 방</span>
      <span class="me">{{ userName }}</span>
      <span class="total">{{ scoreTotal }}<small>점</small></span>
    </header>

    <main class="play-body">
      <!-- 대기실 -->
      <section v-if="pageType===0" class="stage">
        <div class="stage-icon">🎉</div>
        <h1 class="stage-title">입장 완료!</h1>
        <p class="stage-sub">화면에서 내 닉네임을 찾아보세요. 출제자가 곧 게임을 시작해요.</p>
        <div class="players">
          <span v-for="user in userList" :key="user" class="uk-chip" :class="{ me: user === userName }">{{ user }}</span>
        </div>
        <button class="uk-btn glass sm mt-4" @click="exit($event)">나가기</button>
      </section>

      <!-- OX 문제 -->
      <section v-else-if="pageType===1" class="answer-stage">
        <p class="answer-hint">정답을 선택하세요</p>
        <div class="uk-answers">
          <button id="true" class="uk-answer a2 ox" @click="submit($event)">O</button>
          <button id="false" class="uk-answer a1 ox" @click="submit($event)">X</button>
        </div>
      </section>

      <!-- 객관식 문제 -->
      <section v-else-if="pageType===2" class="answer-stage">
        <p class="answer-hint">정답을 선택하세요</p>
        <div class="uk-answers">
          <button id="one" class="uk-answer a1" @click="submit($event)"><span class="num">1</span>{{answer1Text}}</button>
          <button id="two" class="uk-answer a2" @click="submit($event)"><span class="num">2</span>{{answer2Text}}</button>
          <button id="three" class="uk-answer a3" @click="submit($event)"><span class="num">3</span>{{answer3Text}}</button>
          <button id="four" class="uk-answer a4" @click="submit($event)"><span class="num">4</span>{{answer4Text}}</button>
        </div>
      </section>

      <!-- 채점 결과 -->
      <section v-else-if="pageType===3" class="stage">
        <template v-if="resType===0">
          <div class="uk-result-icon correct">✓</div>
          <h1 class="uk-result-title">정답이에요!</h1>
          <span class="uk-score-pill">+ {{ score }}점</span>
        </template>
        <template v-else-if="resType===1">
          <div class="uk-result-icon incorrect">✕</div>
          <h1 class="uk-result-title">아쉬워요</h1>
          <p class="stage-sub">다음 문제는 맞힐 수 있어요. 힘내세요!</p>
        </template>
        <template v-else>
          <div class="uk-result-icon timeout">⏱</div>
          <h1 class="uk-result-title">시간 초과</h1>
          <p class="stage-sub">다음엔 조금 더 빠르게! 기회는 또 있어요.</p>
        </template>
        <p class="stage-foot">현재 총점 <strong>{{ scoreTotal }}점</strong></p>
      </section>

      <!-- 최종 순위 -->
      <section v-else-if="pageType===4" class="stage">
        <div class="stage-icon big">{{ grade == 1 ? "🏆" : grade <= 3 ? "🥳" : "👏" }}</div>
        <p class="stage-sub mb-1">최종 순위</p>
        <h1 class="final-rank">{{grade}}<small>등</small></h1>
        <p v-if="grade<=3" class="stage-sub">축하합니다! 정말 대단해요!</p>
        <p v-else-if="grade<=10" class="stage-sub">조금 아쉽지만, 그래도 잘하셨습니다!</p>
        <p v-else class="stage-sub">다음엔 더 나은 성적을 얻기를 응원합니다!</p>
        <span class="uk-score-pill mt-2">총 {{ scoreTotal }}점</span>
        <button class="uk-btn light mt-5" @click="exit($event)">처음으로</button>
      </section>

      <!-- 대기 -->
      <section v-else-if="pageType===5" class="stage">
        <div v-if="countdown > 0" :key="countdown" class="uk-countdown">{{ countdown }}</div>
        <div v-else class="uk-spinner"></div>
        <h2 class="stage-title mt-4">{{ waitMessage }}</h2>
      </section>
    </main>

    <beautiful-chat
      :participants="participants"
      :onMessageWasSent="onMessageWasSent"
      :messageList="messageList"
      :newMessagesCount="newMessagesCount"
      :isOpen="isChatOpen"
      :close="closeChat"
      :open="openChat"
      :showEmoji="true"
      :showFile="false"
      :showEdition="false"
      :showDeletion="false"
      :showTypingIndicator="showTypingIndicator"
      :showLauncher="showLch"
      :showCloseButton="true"
      :colors="colors"
      :alwaysScrollToBottom="alwaysScrollToBottom"
      :disableUserListToggle="true"
      :messageStyling="messageStyling"
      @onType="handleOnType"
      @edit="editMessage" />
  </div>
</template>

<script>
import GameSocket from "@/utils/gameSocket";
import {
  createToken,
  saveParticipant,
  loadParticipant,
  clearParticipant,
  errorMessage,
} from "@/utils/gameSession";

const EXAMINER_NAME = "★방 장★";

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
      token: "",
      socket: null,
      connected: false,
      reconnecting: false,
      leaving: false,
      awaitingJoin: false,
      joinFallbackTimer: null,
      show: true,
      pageType: 5,
      waitMessage: "연결 중...",
      countdown: 0,
      questionIndex: -1,
      userList: [],
      resType: 0,
      score: 0,
      scoreTotal: 0,
      grade: 0,
      answer1Text: "",
      answer2Text: "",
      answer3Text: "",
      answer4Text: "",
      participants: [
        {
          "id": EXAMINER_NAME,
          "name": EXAMINER_NAME
        }
      ],
      messageList: [],
      newMessagesCount: 0,
      isChatOpen: false,
      showTypingIndicator: '',
      showLch: true,
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
      alwaysScrollToBottom: false,
      messageStyling: true
    }
  },
  created() {
    window.addEventListener('beforeunload', this.unLoadEvent);

    history.pushState(null, document.title, location.href);
    window.addEventListener('popstate', function(e) {
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

    // 새로고침/재접속 시 라우터 params 가 사라지므로 저장된 참여 정보로 복원
    const saved = loadParticipant();
    if (this.number && this.nickname) {
      this.roomNum = this.number;
      this.userName = this.nickname;
      this.token = (saved && saved.roomNum === this.number && saved.nickname === this.nickname && saved.token)
        ? saved.token : createToken();
    } else if (saved) {
      this.roomNum = saved.roomNum;
      this.userName = saved.nickname;
      this.token = saved.token;
    } else {
      this.$router.replace({ name: "PtEnter" });
      return;
    }
    saveParticipant({ roomNum: this.roomNum, nickname: this.userName, token: this.token });

    this.connect();
  },
  destroyed() {
    clearTimeout(this.joinFallbackTimer);
    window.removeEventListener('beforeunload', this.unLoadEvent);
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
  },
  methods: {
    connect() {
      this.socket = new GameSocket({
        subscribe: (client) => {
          client.subscribe("/subscribe/play/room/" + this.roomNum, res => this.receiveMessage(res.body));
          client.subscribe("/subscribe/play/room/" + this.roomNum + "/" + this.userName, res => this.receiveMessage(res.body));
          client.subscribe("/user/queue/errors", res => this.receiveError(res.body));
        },
        onConnect: () => {
          this.connected = true;
          this.reconnecting = false;
          // JOIN 처리가 끝난 뒤(내 JOIN 브로드캐스트 수신)에 상태를 조회해야 한다.
          // 동시에 조회하면 아직 참여자로 등록되기 전이라 "참여 정보가 없습니다" 오류가 난다.
          this.awaitingJoin = true;
          this.sendJoinMessage("JOIN");
          clearTimeout(this.joinFallbackTimer);
          this.joinFallbackTimer = setTimeout(() => {
            // JOIN 브로드캐스트를 놓친 경우 대비
            if (this.awaitingJoin) {
              this.awaitingJoin = false;
              this.syncState();
            }
          }, 2000);
        },
        onDisconnect: () => {
          this.connected = false;
          if (!this.leaving) this.reconnecting = true;
        },
      });
      this.socket.connect();
    },
    unLoadEvent(event) {
      // 새로고침/창 닫기는 퇴장이 아님 → UNJOIN 을 보내지 않아야 재접속 시 이어서 진행 가능
      if (this.socket) {
        this.socket.close();
        this.socket = null;
      }
    },
    // 서버의 현재 진행 상태로 화면 복원
    syncState(retries = 1) {
      this.$http
        .get("/api/v1/room/check/state", {
          params: { number: this.roomNum, nickname: this.userName, token: this.token },
        })
        .then((res) => {
          if (res.data.code !== 0) {
            this.leaveWithMessage(res.data.message);
            return;
          }
          this.applyState(res.data.result);
        })
        .catch((error) => {
          if (error.response && error.response.status >= 400 && error.response.status < 500) {
            if (retries > 0) {
              // 입장 처리 직후의 일시적인 불일치일 수 있으므로 한 번 더 확인
              setTimeout(() => this.syncState(retries - 1), 1000);
              return;
            }
            // 방이 종료됐거나 참여 정보가 유효하지 않음
            this.leaveWithMessage(errorMessage(error, "게임에 다시 참여할 수 없습니다."));
          } else {
            console.log(error);
          }
        });
    },
    applyState(state) {
      this.userList = state.participants || [];
      this.participants = [
        { id: EXAMINER_NAME, name: EXAMINER_NAME },
        ...this.userList.filter((u) => u !== this.userName).map((u) => ({ id: u, name: u })),
      ];
      this.scoreTotal = state.totalScore || 0;
      this.questionIndex = state.questionIndex;

      switch (state.phase) {
        case "WAITING":
          this.pageType = 0;
          this.showLch = true;
          break;
        case "COUNTDOWN":
          this.setWaiting("다음 문제를 준비하고 있어요", state.remainSec);
          break;
        case "QUESTION":
          if (state.answered) {
            this.setWaiting("답안 제출 완료! 결과를 기다리는 중...");
          } else {
            this.answer1Text = state.answer1Text;
            this.answer2Text = state.answer2Text;
            this.answer3Text = state.answer3Text;
            this.answer4Text = state.answer4Text;
            this.pageType = state.questionType === "OX" ? 1 : 2;
            this.showLch = false;
            this.closeChat();
          }
          break;
        case "RESULT":
        case "RANK":
          this.pageType = 3;
          this.showLch = true;
          if (state.result === "CORRECT") {
            this.resType = 0;
            this.score = state.score;
          } else if (state.result === "INCORRECT") {
            this.resType = 1;
          } else {
            this.resType = 2;
          }
          break;
        case "FINISHED":
          this.pageType = 4;
          this.showLch = true;
          if (state.finalRank) this.grade = state.finalRank;
          break;
      }
    },
    submit(event) {
      let ans;

      if (event.target.id === "true" || event.target.id === "one")
        ans = "1";
      else if (event.target.id === "false" || event.target.id === "two")
        ans = "2";
      else if (event.target.id === "three")
        ans = "3";
      else if (event.target.id === "four")
        ans = "4";

      if (!this.sendMessage("ANSWER", ans, { questionIndex: this.questionIndex })) {
        // 연결이 끊긴 상태: 화면을 유지해 재연결 후 다시 제출할 수 있게 한다.
        this.toast("서버와 연결이 끊어져 답을 제출하지 못했습니다. 재연결 후 다시 선택해 주세요.", "danger");
        return;
      }

      this.setWaiting("답안 제출 완료! 결과를 기다리는 중...");
    },
    setWaiting(message, countdown) {
      this.waitMessage = message;
      this.countdown = countdown || 0;
      this.pageType = 5;
      this.showLch = true;
    },
    receiveError(resBody) {
      let msg;
      try {
        msg = JSON.parse(resBody);
      } catch (e) {
        return;
      }
      this.toast(msg.content, "warning");
      // 서버와 화면 상태가 어긋났을 수 있으므로 다시 동기화
      this.syncState();
    },
    receiveMessage(resBody) {
      let msg;
      try {
        msg = JSON.parse(resBody);
      } catch (e) {
        console.log("잘못된 메시지", resBody);
        return;
      }

      if (msg.type === "JOIN") {
        if (msg.participantName === this.userName && this.awaitingJoin) {
          this.awaitingJoin = false;
          clearTimeout(this.joinFallbackTimer);
          this.syncState();
        }
        let found = this.userList.find((user) => user === msg.participantName);
        if (found === undefined)
          this.userList.push(msg.participantName);

        if (msg.participantName !== this.userName && !this.participants.find((p) => p.id === msg.participantName))
          this.participants = [ ...this.participants, { id: msg.participantName,  name: msg.participantName } ]
      } else if (msg.type === "UNJOIN") {
        this.userList = this.userList.filter((user) => user !== msg.participantName);

        this.participants = this.participants.filter((part) => part.id !== msg.participantName)
      } else if (msg.type === "START") {
        this.setWaiting("게임이 곧 시작돼요!");
      } else if (msg.type === "WAITING") {
        this.pageType = 0;
        this.showLch = true;
      } else if (msg.type === "LOADING" || msg.type === "COUNTDOWN") {
        this.setWaiting("다음 문제를 준비하고 있어요", parseInt(msg.content) || 0);
        if (msg.questionIndex !== null && msg.questionIndex !== undefined)
          this.questionIndex = msg.questionIndex;
      } else if (msg.type === "OXP") {
        this.questionIndex = msg.questionIndex;
        this.pageType = 1;
        this.showLch = false;
        this.closeChat();
      } else if (msg.type === "OBP") {
        this.questionIndex = msg.questionIndex;
        this.pageType = 2;
        this.showLch = false;
        this.closeChat();
        this.answer1Text = msg.answer1Text;
        this.answer2Text = msg.answer2Text;
        this.answer3Text = msg.answer3Text;
        this.answer4Text = msg.answer4Text;
      } else if (msg.type === "TIMEOUT") {
        this.pageType = 3;
        this.showLch = true;
        this.resType = 2;
      } else if (msg.type === "CORRECT") {
        this.pageType = 3;
        this.showLch = true;
        this.resType = 0;
        this.score = msg.score;
        if (msg.result && msg.result.totalScore !== undefined)
          this.scoreTotal = msg.result.totalScore;
        else
          this.scoreTotal += msg.score;
      } else if (msg.type === "INCORRECT") {
        this.pageType = 3;
        this.showLch = true;
        this.resType = 1;
      } else if (msg.type === "CHAT") {
        this.recvChatMessage(msg.participantName, msg.chatType, msg.content);
      } else if (msg.type === "FINALRANK") {
        this.grade = msg.content;
        this.pageType = 4;
        this.showLch = true;
      } else if (msg.type === "EXIT") {
        this.leaveWithMessage(null);
      }
    },
    // 방 종료 등으로 더 이상 참여할 수 없을 때
    leaveWithMessage(message) {
      if (this.leaving) return;
      this.leaving = true;
      this.reconnecting = false;
      clearParticipant();
      if (message) alert(message);
      this.$router.replace({ name: "Home" });
    },
    // 사용자가 직접 나가기
    exit(event) {
      this.leaving = true;
      this.sendJoinMessage("UNJOIN");
      clearParticipant();
      this.$router.replace({ name: "Home" });
    },
    sendJoinMessage(type) {
      return this.socket ? this.socket.send("/publish/play/join", {
        roomNumber: this.roomNum,
        participantName: this.userName,
        token: this.token,
        type: type,
      }) : false;
    },
    sendMessage(type, message, extra) {
      return this.socket ? this.socket.send("/publish/play/message", {
        roomNumber: this.roomNum,
        participantName: this.userName,
        content: message,
        type: type,
        ...(extra || {}),
      }) : false;
    },
    sendChatMessage(chatType, message) {
      return this.sendMessage("CHAT", message, { chatType: chatType });
    },
    toast(message, variant) {
      if (!message) return;
      this.$bvToast.toast(message, {
        title: "알림",
        variant: variant || "info",
        solid: true,
        autoHideDelay: 3000,
        toaster: "b-toaster-top-center",
      });
    },
    recvChatMessage(nick, msgt, cont) {
      if (cont && cont.length > 0) {
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
      let sent = false;
      if (message.type === 'text')
        sent = this.sendChatMessage(message.type, message.data.text)
      else if (message.type === 'emoji')
        sent = this.sendChatMessage(message.type, message.data.emoji)

      if (!sent) {
        this.toast("연결이 끊어져 메시지를 보내지 못했습니다.", "danger");
        return;
      }
      this.messageList = [ ...this.messageList, message ]
    },
    openChat() {
      this.isChatOpen = true
      this.newMessagesCount = 0
    },
    closeChat() {
      this.isChatOpen = false
    },
    handleScrollToTop () {
      //console.log('Handle scroll to top event')
    },
    handleOnType () {
      //console.log('Emit typing event')
    },
    editMessage(message){
      const m = this.messageList.find(m=>m.id === message.id);
      m.isEdited = true;
      m.data.text = message.data.text;
    }
  },
}
</script>

<style scoped>
.play {
  display: flex;
  flex-direction: column;
}
.play-bar {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 20px;
  background: rgba(0, 0, 0, 0.18);
}
.play-bar .uk-badge { background: rgba(255, 255, 255, 0.2); color: #fff; }
.play-bar .me { flex: 1; text-align: left; font-weight: 800; font-size: 17px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.play-bar .total { font-weight: 900; font-size: 22px; color: var(--uk-accent); }
.play-bar .total small { font-size: 13px; margin-left: 2px; color: rgba(255, 255, 255, 0.8); }

.play-body {
  position: relative;
  z-index: 1;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}
.stage {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 20px 96px;
  text-align: center;
}
.stage-icon { font-size: 64px; margin-bottom: 8px; animation: uk-pop 0.5s; }
.stage-icon.big { font-size: 88px; }
.stage-title { font-size: clamp(28px, 6vw, 44px); font-weight: 900; margin: 0 0 8px; }
.stage-sub { color: rgba(255, 255, 255, 0.8); font-size: 17px; margin: 0 0 8px; }
.stage-foot { margin-top: 28px; color: rgba(255, 255, 255, 0.75); }
.stage-foot strong { color: #fff; }
.players {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
  margin-top: 24px;
  max-width: 720px;
}
.uk-chip.me { background: var(--uk-accent); color: #2a1470; }

.answer-stage {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px 16px 24px;
}
.answer-hint { font-weight: 800; font-size: 18px; margin: 4px 0 14px; color: rgba(255, 255, 255, 0.9); }
.answer-stage .uk-answers { flex: 1; }

.final-rank { font-size: clamp(80px, 18vw, 140px); font-weight: 900; line-height: 1; margin: 0 0 12px; }
.final-rank small { font-size: 0.35em; margin-left: 4px; }
</style>
