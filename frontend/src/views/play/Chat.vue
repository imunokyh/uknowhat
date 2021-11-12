<template>
    <div>
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
            :showLauncher="true"
            :showCloseButton="false"
            :colors="colors"
            :alwaysScrollToBottom="alwaysScrollToBottom"
            :disableUserListToggle="true"
            :messageStyling="messageStyling"
            @onType="handleOnType"
            @edit="editMessage" />
        <!--
        <b-overlay :show="show" rounded="sm">
            <h2>{{roomNum}}번 방</h2>
            내용: 
            <input
            v-model="message"
            type="text"
            @keyup.enter="sendMessage()"
            >
            <b-button variant="primary" @click="sendMessage()">Send</b-button>
            <div>
                <ul class="list-group" :key="idx" v-for="(item, idx) in recvList">
                    <li class="list-group-item">[{{ item.participantName }}]: {{ item.content }}</li>
                </ul>
            </div>
        </b-overlay>
        -->
    </div>
</template>

<script>
import Stomp from 'webstomp-client'
import SockJS from 'sockjs-client'

export default {
    name: 'Chat',
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
            message: "",
            recvList: [],
            show: true,
            participants: [
                {
                    id: 'user1',
                    name: 'Matteo',
                    //imageUrl: 'https://avatars3.githubusercontent.com/u/1915989?s=230&v=4'
                },
                {
                    id: 'user2',
                    name: 'Support',
                    //imageUrl: 'https://avatars3.githubusercontent.com/u/37018832?s=200&v=4'
                }
            ], // the list of all the participant of the conversation. `name` is the user name, `id` is used to establish the author of a message, `imageUrl` is supposed to be the user avatar.
            //titleImageUrl: 'https://a.slack-edge.com/66f9/img/avatars-teams/ava_0001-34.png',
            messageList: [
                { type: 'text', author: `me`, data: { text: `Say yes!` } },
                { type: 'text', author: `user1`, data: { text: `No.` } }
            ], // the list of the messages to show, can be paginated and adjusted dynamically
            newMessagesCount: 0,
            isChatOpen: false, // to determine whether the chat window should be open or closed
            showTypingIndicator: '', // when set to a value matching the participant.id it shows the typing indicator for the specific user
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
            }, // specifies the color scheme for the component
            alwaysScrollToBottom: false, // when set to true always scrolls the chat to the bottom when new events are in (new message, user starts typing...)
            messageStyling: true // enables *bold* /emph/ _underline_ and such (more info at github.com/mattezza/msgdown)
        }
    },
    created() {
        /*
        window.addEventListener('beforeunload', this.unLoadEvent);

        this.roomNum = this.number;
        this.userName = this.nickname;
        console.log(this.number);
        console.log(this.nickname);
        this.connect()
        */
    },
    destroyed() {
        /*
        if (this.stompClient !== null) {
            const msg = { 
                roomNumber: this.roomNum,
                participantName: this.userName,
                content: "",
                type: "UNJOIN"
            };
            this.stompClient.send('/publish/chat/join', JSON.stringify(msg));

            this.stompClient.unsubscribe("/subscribe/chat/room/" + this.roomNum, {});
            this.stompClient.disconnect();
            this.stompClient = null;
        }
        */
    },
    methods: {
        /*
        connect() {
            let sock = new SockJS("/stomp");
            this.stompClient = Stomp.over(sock);
            console.log('소켓 연결을 시도합니다.');
            this.stompClient.connect(
                {},
                frame => {
                    // 소켓 연결 성공
                    this.connected = true;
                    console.log('소켓 연결 성공');
                    // 서버의 메시지 전송 endpoint를 구독합니다.
                    // 이런형태를 pub sub 구조라고 합니다.                    
                    this.stompClient.subscribe("/subscribe/chat/room/" + this.roomNum, res => {
                        console.log('구독으로 받은 메시지 입니다.');
                        console.log(res.body);
                        // 받은 데이터를 json으로 파싱하고 리스트에 넣어줍니다.
                        this.recvList.push(JSON.parse(res.body));
                        this.show = false;
                    });

                    const msg = { 
                        roomNumber: this.roomNum,
                        participantName: this.userName,
                        content: "",
                        type: "JOIN"
                    };

                    this.stompClient.send('/publish/chat/join', JSON.stringify(msg));
                },
                error => {
                    // 소켓 연결 실패
                    console.log('소켓 연결 실패', error);
                    this.connected = false;
                }
            );
        },
        send() {
            if (this.stompClient && this.stompClient.connected) {
                console.log("Send message: " + this.message);
                const msg = { 
                    roomNumber: this.roomNum,
                    participantName: this.userName,
                    content: this.message,
                    type: "CHAT"
                };
                this.stompClient.send("/publish/chat/message", JSON.stringify(msg));
            }
        },
        sendMessage() {
            if (this.userName !== '' && this.message !== '') {
                this.send();
                this.message = '';
            }
        },
        unLoadEvent(event) {
            if (this.stompClient !== null) {
                const msg = { 
                    roomNumber: this.roomNum,
                    participantName: this.userName,
                    content: "",
                    type: "UNJOIN"
                };
                this.stompClient.send('/publish/chat/join', JSON.stringify(msg));

                this.stompClient.unsubscribe("/subscribe/chat/room/" + this.roomNum, {});
                this.stompClient.disconnect();
                this.stompClient = null;
            }
        },
        */
        sendMessage (text) {
            if (text.length > 0) {
                this.newMessagesCount = this.isChatOpen ? this.newMessagesCount : this.newMessagesCount + 1
                this.onMessageWasSent({ author: 'support', type: 'text', data: { text } })
            }
        },
        onMessageWasSent (message) {
            // called when the user sends a message
            this.messageList = [ ...this.messageList, message ]
        },
        openChat () {
            // called when the user clicks on the fab button to open the chat
            this.isChatOpen = true
            this.newMessagesCount = 0
        },
        closeChat () {
            // called when the user clicks on the botton to close the chat
            this.isChatOpen = false
        },
        handleScrollToTop () {
            // called when the user scrolls message list to top
            // leverage pagination for loading another page of messages
        },
        handleOnType () {
            console.log('Emit typing event')
        },
        editMessage(message){
            const m = this.messageList.find(m=>m.id === message.id);
            m.isEdited = true;
            m.data.text = message.data.text;
        }
    }
}
</script>