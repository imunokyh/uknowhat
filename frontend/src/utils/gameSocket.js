import Stomp from "webstomp-client";
import SockJS from "sockjs-client";

const MAX_RETRY_DELAY = 10000;

/**
 * 자동 재연결을 지원하는 STOMP 클라이언트.
 *
 * 연결이 끊기면 1s, 2s, 4s ... 최대 10s 간격으로 재연결을 시도하고,
 * 브라우저가 다시 온라인이 되면 즉시 재연결한다.
 * 재연결될 때마다 subscribe() 와 onConnect() 를 다시 호출하므로
 * 화면에서는 onConnect 에서 서버 상태를 다시 조회해 이어서 진행하면 된다.
 *
 * options
 *  - headers: STOMP CONNECT 헤더 (객체 또는 함수)
 *  - subscribe(client): 구독 등록
 *  - onConnect(): 연결(재연결 포함) 성공
 *  - onDisconnect(error): 연결 끊김
 */
export default class GameSocket {
  constructor(options) {
    this.options = options || {};
    this.client = null;
    this.closed = false;
    this.retry = 0;
    this.retryTimer = null;

    this.handleOnline = () => {
      if (!this.isConnected()) this.reconnectNow();
    };
    window.addEventListener("online", this.handleOnline);
  }

  isConnected() {
    return !!(this.client && this.client.connected);
  }

  connect() {
    if (this.closed) return;
    clearTimeout(this.retryTimer);
    this.retryTimer = null;

    const headers = typeof this.options.headers === "function"
      ? this.options.headers()
      : (this.options.headers || {});

    let client;
    try {
      client = Stomp.over(new SockJS("/stomp"), {
        debug: false,
        heartbeat: { outgoing: 10000, incoming: 10000 },
      });
    } catch (e) {
      console.log("소켓 생성 실패", e);
      this.scheduleReconnect();
      return;
    }
    this.client = client;

    client.connect(
      headers,
      () => {
        if (this.client !== client || this.closed) return;
        this.retry = 0;
        try {
          if (this.options.subscribe) this.options.subscribe(client);
          if (this.options.onConnect) this.options.onConnect();
        } catch (e) {
          console.log("연결 후 처리 중 오류", e);
        }
      },
      (error) => {
        // 이미 교체된(오래된) 클라이언트의 이벤트는 무시
        if (this.client !== client) return;
        this.client = null;
        try {
          if (client.connected) client.disconnect();
        } catch (e) {
          // ignore
        }
        if (this.closed) return;
        console.log("소켓 연결 끊김", error);
        if (this.options.onDisconnect) this.options.onDisconnect(error);
        this.scheduleReconnect();
      }
    );
  }

  scheduleReconnect() {
    if (this.closed || this.retryTimer) return;
    const delay = Math.min(1000 * Math.pow(2, this.retry), MAX_RETRY_DELAY);
    this.retry++;
    this.retryTimer = setTimeout(() => {
      this.retryTimer = null;
      this.connect();
    }, delay);
  }

  reconnectNow() {
    if (this.closed) return;
    clearTimeout(this.retryTimer);
    this.retryTimer = null;
    const old = this.client;
    this.client = null;
    try {
      if (old && old.connected) old.disconnect();
    } catch (e) {
      // ignore
    }
    this.connect();
  }

  /** 연결되어 있지 않으면 false 를 반환한다. */
  send(destination, body) {
    if (!this.isConnected()) return false;
    try {
      this.client.send(destination, JSON.stringify(body));
      return true;
    } catch (e) {
      console.log("메시지 전송 실패", e);
      return false;
    }
  }

  close() {
    this.closed = true;
    clearTimeout(this.retryTimer);
    this.retryTimer = null;
    window.removeEventListener("online", this.handleOnline);
    const client = this.client;
    this.client = null;
    try {
      if (client && client.connected) client.disconnect();
    } catch (e) {
      // ignore
    }
  }
}
