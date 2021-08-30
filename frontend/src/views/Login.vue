<template>
  <div>
    <b-form>
      <b-form-group id="input-group-1" label="Id:" label-for="input-1">
        <b-form-input
          id="input-1"
          v-model="form.id"
          type="text"
          placeholder="Enter ID"
          required
        ></b-form-input>
      </b-form-group>

      <b-form-group id="input-group-2" label="Password:" label-for="input-2">
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
    </b-form>
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
        }
      };
    },
    methods: {
      login() {
        this.$http
          .post("/api/v1/user/login", {
            "id": this.form.id,
            "password": this.form.password
          })
          .then((res) => {
            if (res.data.code === 0) {
              alert(res.data.result.token);
              this.$router.push({name: 'RoomMng'});
            } else {
              alert(res.data.message);
            }
          })
          .catch((error) => console.log(error));
      },
      signup() {
        this.$router.push({name: 'Signup'});
      },
    }
  }
</script>