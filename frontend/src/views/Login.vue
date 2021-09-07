<template>
  <div class="h-100">
    <b-overlay :show="show" rounded="sm" class="h-100">
      <b-form>
        <b-form-group id="input-group-1" label="Id" label-for="input-1">
          <b-form-input
            id="input-1"
            v-model="form.id"
            type="text"
            placeholder="Enter ID"
            required
          ></b-form-input>
        </b-form-group>

        <b-form-group id="input-group-2" label="Password" label-for="input-2">
          <b-form-input
            id="input-2"
            v-model="form.password"
            type="password"
            placeholder="Enter Password"
            required
          ></b-form-input>
        </b-form-group>

        <b-button variant="primary" @click="login()">Login</b-button>
        <b-button variant="success" @click="signup()">Signup</b-button>
        <b-button variant="danger" @click="back()">Back</b-button>
      </b-form>
    </b-overlay>
  </div>
</template>

<script>
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
    methods: {
      login() {
        this.show = true;
        this.$http
          .post("/api/v1/user/login", {
            "id": this.form.id,
            "password": this.form.password
          })
          .then((res) => {
            if (res.data.code === 0) {
              alert(res.data.result.token);
              this.$http.defaults.headers.common['Authorization'] = 'Bearer ' + res.data.result.token;
              this.$router.push({name: 'RoomMng'});
            } else {
              alert(res.data.message);
            }
            this.show = false;
          })
          .catch((error) => {console.log(error); this.show = false;});
      },
      signup() {
        this.$router.push({name: 'Signup'});
      },
      back() {
        this.$router.go(-1);
      },
    }
  }
</script>