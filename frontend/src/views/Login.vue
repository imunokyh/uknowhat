<template>
  <div class="uk-game">
    <div class="uk-center">
      <h1 class="uk-logo" style="cursor: pointer;" @click="home()">Uknowhat<span>?</span></h1>
      <p class="uk-tagline">출제자로 로그인하고 퀴즈방을 열어보세요</p>

      <form class="uk-card uk-card-narrow" @submit.prevent="login()">
        <h2 class="uk-card-title">로그인</h2>
        <p class="uk-card-sub">퀴즈를 만들고 진행하려면 로그인이 필요해요</p>

        <div class="uk-field">
          <label class="uk-label" for="login-id">아이디</label>
          <input id="login-id" ref="idInput" v-model="form.id" class="uk-input" autocomplete="username" placeholder="아이디" />
        </div>
        <div class="uk-field">
          <label class="uk-label" for="login-pw">비밀번호</label>
          <input id="login-pw" v-model="form.password" class="uk-input" type="password" autocomplete="current-password" placeholder="비밀번호" />
        </div>

        <button class="uk-btn block lg mt-4" type="submit" :disabled="show">
          {{ show ? "로그인 중..." : "로그인" }}
        </button>

        <div class="footer-links">
          <span>계정이 없으신가요?</span>
          <a class="uk-link" @click="signup()">회원가입</a>
        </div>
      </form>

      <a class="uk-link mt-4" @click="home()">← 참여자로 입장하기</a>
    </div>
  </div>
</template>

<script>
  import { errorMessage } from "@/utils/gameSession";

  export default {
    data() {
      return {
        name: "Login",
        form: {
          id: '',
          password: ''
        },
        show: false
      };
    },
    mounted() {
      this.$refs.idInput.focus();
    },
    methods: {
      login() {
        if (!this.form.id || !this.form.password) {
          alert("아이디와 비밀번호를 입력하세요.");
          return;
        }
        this.show = true;
        this.$http
          .post("/api/v1/user/login", {
            "id": this.form.id,
            "password": this.form.password
          })
          .then((res) => {
            if (res.data.code === 0) {
              sessionStorage.setItem('token', res.data.result.token);
              sessionStorage.setItem('userId', this.form.id);
              this.$router.push({name: 'RoomMng'});
            } else {
              alert(res.data.message);
            }
          })
          .catch((error) => {
            console.log(error);
            alert(errorMessage(error, "로그인 중 오류가 발생했습니다."));
          })
          .finally(() => { this.show = false; });
      },
      signup() {
        this.$router.push({name: 'Signup'});
      },
      home() {
        this.$router.push({name: 'Home'});
      },
    }
  }
</script>

<style scoped>
.uk-card { text-align: left; }
.footer-links {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 20px;
  font-size: 14px;
  color: var(--uk-text-sub);
}
.footer-links .uk-link { color: var(--uk-primary); }
</style>
