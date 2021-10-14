<template>
  <div class="h-100">
    <b-overlay :show="show" rounded="sm" class="h-100">
      <b-jumbotron
        class="h-100"
        bg-variant="dark"
        text-variant="white"
        border-variant="dark"
      >
        <img class="mt-5 mb-5 img-fluid" alt="Title" src="../assets/title.png">

        <b-form>
          <b-form-group id="input-group-1" label-for="input-1">
            <b-form-input
              class="col-sm-4 h-center"
              id="input-1"
              v-model="form.id"
              type="text"
              placeholder="ID"
              required
            ></b-form-input>
          </b-form-group>

          <b-form-group id="input-group-2" label-for="input-2">
            <b-form-input
              class="col-sm-4 h-center"
              id="input-2"
              v-model="form.password"
              type="password"
              placeholder="Password"
              required
            ></b-form-input>
          </b-form-group>

          <b-form-group id="input-group-3" label-for="input-3">
            <b-form-input
              class="col-sm-4 h-center"
              id="input-3"
              v-model="form.passwordCheck"
              type="password"
              placeholder="Check the Password"
              required
              @keyup.enter="signup()"
            ></b-form-input>
          </b-form-group>

          <b-button class="col-sm-2 mr-1" variant="primary" @click="signup()">Submit</b-button>
          <b-button class="col-sm-2 ml-1" variant="danger" @click="cancel()">Cancel</b-button>
        </b-form>
      </b-jumbotron>
    </b-overlay>
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
        },
        show: false
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
            this.show = false;
          })
          .catch((error) => {console.log(error); this.show = false;});
      },
      cancel() {
        this.$router.go(-1);
        //this.$router.push({name: "Login"});
      },
    }
  }
</script>