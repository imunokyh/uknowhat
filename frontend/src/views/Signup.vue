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

      <b-form-group id="input-group-3" label="Password Check:" label-for="input-3">
        <b-form-input
          id="input-3"
          v-model="form.passwordCheck"
          type="password"
          placeholder="Enter Password"
          required
        ></b-form-input>
      </b-form-group>

      <b-button variant="primary" @click="signup()">Submit</b-button>
      <b-button variant="danger" @click="cancel()">Cancel</b-button>
    </b-form>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        name: "Signup",
        form: {
          id: '',
          password: '',
          passwordCheck: ''
        }
      };
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
          .catch((error) => console.log(error));
      },
      cancel() {
        this.$router.push({name: "Login"});
      },
    }
  }
</script>