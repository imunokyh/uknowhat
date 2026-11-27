<template>
  <header class="uk-appbar">
    <div class="uk-appbar-inner">
      <a class="uk-logo uk-logo-sm" style="cursor: pointer;" @click="go('RoomMng')">Uknowhat<span>?</span></a>
      <nav>
        <a :class="{ active: active === 'room' }" @click="go('RoomMng')">내 퀴즈방</a>
        <a :class="{ active: active === 'bank' }" @click="go('ProbBank')">문제은행</a>
      </nav>
      <div class="uk-user">
        <span class="uk-avatar">{{ initial }}</span>
        <span class="uid">{{ userId }}</span>
        <button class="uk-btn ghost sm" @click="logout">로그아웃</button>
      </div>
    </div>
  </header>
</template>

<script>
export default {
  name: "AppBar",
  props: {
    active: { type: String, default: "" },
  },
  computed: {
    userId() {
      return sessionStorage.getItem("userId") || "";
    },
    initial() {
      return (this.userId || "?").charAt(0).toUpperCase();
    },
  },
  methods: {
    go(name) {
      if (this.$route.name !== name) this.$router.push({ name });
    },
    logout() {
      sessionStorage.removeItem("token");
      sessionStorage.removeItem("userId");
      this.$router.replace({ name: "Login" });
    },
  },
};
</script>
