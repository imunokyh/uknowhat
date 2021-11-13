<template>
  <div class="h-100">
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
      pageType: 5,
      userList: [],
      resType: 0,
      score: 0,
      scoreTotal: 0,
      grade: 0,
      answer1Text: "",
      answer2Text: "",
      answer3Text: "",
      answer3Text: "",
      participants: [
        {
          "id": "★방 장★",
          "name": "★방 장★"
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
              //this.show = false;
              this.pageType = 0;
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

                    if (user !== this.userName)
                      this.participants = [ ...this.participants, { id: user,  name: user } ]
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

      //this.show = true;
      this.pageType = 5;
      this.showLch = true;

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

        if (msg.participantName !== this.userName)
          this.participants = [ ...this.participants, { id: msg.participantName,  name: msg.participantName } ]
      } else if (msg.type === "UNJOIN") {
        this.userList = this.userList.filter((user) => user !== msg.participantName);

        this.participants = this.participants.filter((part) => part.id !== msg.participantName)
      } else if (msg.type === "START") {
        //this.show = true;
        this.pageType = 5;
        this.showLch = true;
      } else if (msg.type === "WAITING") {
        this.pageType = 0;
        this.showLch = true;
        //this.show = false;
      } else if (msg.type === "LOADING") {
        //this.show = true;
        this.pageType = 5;
        this.showLch = true;
      }else if (msg.type === "OXP") {
        //this.show = false;
        this.pageType = 1;
        this.showLch = false;
        this.closeChat();
      } else if (msg.type === "OBP") {
        //this.show = false;
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
        //this.show = false;
        this.pageType = 3;
        this.showLch = true;
        this.resType = 0;
        this.score = msg.score;
        this.scoreTotal += msg.score;
      } else if (msg.type === "INCORRECT") {
        //this.show = false;
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
        this.exit();
      }
    },
    exit(event) {
      //this.$router.go(-1);
      this.$router.replace({ name: "Home" });
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
    sendChatMessage(chatType, message) {
      if (this.stompClient && this.stompClient.connected) {
        const msg = {
          roomNumber: this.roomNum,
          participantName: this.userName,
          content: message,
          chatType: chatType,
          type: 'CHAT',
        };

        this.stompClient.send("/publish/play/message", JSON.stringify(msg));
      }
    },
    recvChatMessage(nick, msgt, cont) {
      console.log(nick + " " + msgt + " " + cont)
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