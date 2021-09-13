<template>
    <div>
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
            show: true
        }
    },
    created() {
        window.addEventListener('beforeunload', this.unLoadEvent);

        this.roomNum = this.number;
        this.userName = this.nickname;
        console.log(this.number);
        console.log(this.nickname);
        this.connect()
    },
    destroyed() {
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
    methods: {
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
    }
}
</script>