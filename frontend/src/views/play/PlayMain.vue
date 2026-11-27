<template>
  <div class="h-100">
    <b-alert :show="reconnecting" variant="warning" class="m-0 text-center" style="position: fixed; top: 0; left: 0; right: 0; z-index: 2000;">
      연결이 끊어졌습니다. 재연결 중입니다... (게임은 계속 진행되고 있습니다)
    </b-alert>
    <div style="height:5%">
      <div class="h-100 outer-center">
        <h2 class="tbb-p100 ml-1"> {{userName}} </h2>
        <h2 class="tbb-p100 mr-1"> {{scoreTotal}} </h2>
      </div>
    </div>
    <div style="height:95%">
    <!--<b-overlay :show="show" rounded="lg" style="height:95%">-->
      <!-- 대기실 페이지 -->
      <div v-if="pageType===0" class="h-100">
        <h1>You're in!</h1>
        <h2>See your nickname on screen?</h2>
        <b-button variant="danger" class="w-10 mr-3 float-right" @click="exit($event)">Exit</b-button>
        <div>&nbsp;</div>
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
        <div class="left h-100">
          <b-button id="true" variant="primary" class="col-sm-12 h-100" @click="submit($event)">O</b-button>
        </div>
        <div class="right h-100">
          <b-button id="false" variant="danger" class="col-sm-12 h-100" @click="submit($event)">X</b-button>
        </div>
        <!--
        <b-button id="true" variant="primary" class="col-sm-6" @click="submit($event)">O</b-button>
        <b-button id="false" variant="danger" class="col-sm-6" @click="submit($event)">X</b-button>
        -->
      </div>
      <!-- OB(객관식) 문제 정답 선택 페이지 -->
      <div v-else-if="pageType===2" class="h-100">
        <div class="h-50">
          <div class="left h-100">
            <b-button id="one" variant="primary" class="col-sm-12 h-100" @click="submit($event)">{{answer1Text}}</b-button>
          </div>
          <div class="right h-100">
            <b-button id="two" variant="danger" class="col-sm-12 h-100" @click="submit($event)">{{answer2Text}}</b-button>
          </div>
        </div>
        <div class="h-50">
          <div class="left h-100">
            <b-button id="three" variant="success" class="col-sm-12 h-100" @click="submit($event)">{{answer3Text}}</b-button>
          </div>
          <div class="right h-100">
            <b-button id="four" variant="warning" class="col-sm-12 h-100" @click="submit($event)">{{answer4Text}}</b-button>
          </div>
        </div>
        <!--
        <div class="row h-50">
          <b-button id="one" variant="primary" class="col-sm-6" @click="submit($event)">{{answer1Text}}</b-button>
          <b-button id="two" variant="danger" class="col-sm-6" @click="submit($event)">{{answer2Text}}</b-button>
        </div>
        <div class="row h-50">
          <b-button id="three" variant="success" class="col-sm-6" @click="submit($event)">{{answer3Text}}</b-button>
          <b-button id="four" variant="warning" class="col-sm-6" @click="submit($event)">{{answer4Text}}</b-button>
        </div>
        -->
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
      <!-- 대기 화면 페이지 -->
      <div v-else-if="pageType===5" class="h-100">
        <div class="h-100">
          <h1>Waiting...</h1>
          <div>&nbsp;</div>
          <b-overlay :show="true" rounded="lg"></b-overlay>
        </div>
      </div>
    <!--</b-overlay>-->
    </div>
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
      show: true,
      pageType: 5,
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
          this.sendJoinMessage("JOIN");
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
      // 새로고침/창 닫기는 퇴장이 아님 → UNJOIN 을 보내지 않아야 재접속 시 이어서 진행 가능
      if (this.socket) {
        this.socket.close();
        this.socket = null;
      }
    },
    // 서버의 현재 진행 상태로 화면 복원
    syncState() {
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
          this.pageType = 5;
          this.showLch = true;
          break;
        case "QUESTION":
          if (state.answered) {
            this.pageType = 5;
            this.showLch = true;
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
        let found = this.userList.find((user) => user === msg.participantName);
        if (found === undefined)
          this.userList.push(msg.participantName);

        if (msg.participantName !== this.userName && !this.participants.find((p) => p.id === msg.participantName))
          this.participants = [ ...this.participants, { id: msg.participantName,  name: msg.participantName } ]
      } else if (msg.type === "UNJOIN") {
        this.userList = this.userList.filter((user) => user !== msg.participantName);

        this.participants = this.participants.filter((part) => part.id !== msg.participantName)
      } else if (msg.type === "START") {
        this.pageType = 5;
        this.showLch = true;
      } else if (msg.type === "WAITING") {
        this.pageType = 0;
        this.showLch = true;
      } else if (msg.type === "LOADING" || msg.type === "COUNTDOWN") {
        this.pageType = 5;
        this.showLch = true;
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
#true, #false {
  font-size: 100px;
  font-weight: bold;
}

#one, #two, #three, #four {
  color: white;
  font-size: 30px;
  font-weight: bold;
}
</style>

<style>
.tbb-20 {
  color: black;
  font-size: 20px;
  font-weight: bold;
  text-align: center;
  display: flex;
}

.tbb-p100 {
  color: black;
  font-size: 100%;
  font-weight: bold;
  text-align: center;
  display: flex;
}

.outer-center {
  display: flex;
  align-items: center;
  flex-direction: row;
  justify-content: space-between;
}

.h-center {
  float: none; 
  margin: 0 auto
}
</style>