<template>
  <div class="uk-game">
    <div class="uk-center">
      <h1 class="uk-logo" style="cursor: pointer;" @click="home()">Uknowhat<span>?</span></h1>
      <p class="uk-tagline">출제자 계정을 만들어보세요</p>

      <form class="uk-card uk-card-narrow" @submit.prevent="signup()">
        <h2 class="uk-card-title">회원가입</h2>
        <p class="uk-card-sub">아이디와 비밀번호만 있으면 바로 시작할 수 있어요</p>

        <div class="uk-field">
          <label class="uk-label" for="su-id">아이디</label>
          <input id="su-id" ref="idInput" v-model="form.id" class="uk-input" autocomplete="username" placeholder="아이디" />
        </div>
        <div class="uk-field">
          <label class="uk-label" for="su-pw">비밀번호</label>
          <input id="su-pw" v-model="form.password" class="uk-input" type="password" autocomplete="new-password" placeholder="비밀번호" />
        </div>
        <div class="uk-field">
          <label class="uk-label" for="su-pw2">비밀번호 확인</label>
          <input id="su-pw2" v-model="form.passwordCheck" class="uk-input" type="password" autocomplete="new-password" placeholder="비밀번호 확인" />
          <p v-if="mismatch" class="hint">비밀번호가 일치하지 않아요</p>
        </div>

        <button class="uk-btn block lg mt-4" type="submit" :disabled="show">
          {{ show ? "가입 중..." : "가입하기" }}
        </button>

        <div class="footer-links">
          <span>이미 계정이 있으신가요?</span>
          <a class="uk-link" @click="cancel()">로그인</a>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
  import { errorMessage } from "@/utils/gameSession";

  export default {
    data() {
      return {
        name: "Signup",
        form: {
          id: '',
          password: '',
          passwordCheck: ''
        },
        show: false
      };
    },
    computed: {
      mismatch() {
        return this.form.passwordCheck.length > 0 && this.form.password !== this.form.passwordCheck;
      },
    },
    mounted() {
      this.$refs.idInput.focus();
    },
    methods: {
      signup() {
        if (this.form.id === '' || this.form.password === '' || this.form.passwordCheck === '') {
          alert("비어 있는 칸이 있습니다.");
          return;
        }

        if (this.form.password !== this.form.passwordCheck) {
          alert("비밀번호가 같지 않습니다.");
          return;
        }
        this.show = true;
        this.$http
          .post("/api/v1/user/signup", {
            "id": this.form.id,
            "password": this.form.password,
            "authority": "ROLE_USER"
          })
          .then((res) => {
            if (res.data.code !== 0) {
              alert(res.data.message);
            } else {
              alert("계정이 생성되었습니다.");
              this.$router.push({name: "Login"});
            }
          })
          .catch((error) => {
            console.log(error);
            alert(errorMessage(error, "회원가입 중 오류가 발생했습니다."));
          })
          .finally(() => { this.show = false; });
      },
      cancel() {
        this.$router.push({name: "Login"});
      },
      home() {
        this.$router.push({name: 'Home'});
      },
    }
  }
</script>

<style scoped>
.uk-card { text-align: left; }
.hint { color: var(--uk-danger); font-size: 13px; font-weight: 600; margin: 6px 0 0; }
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
