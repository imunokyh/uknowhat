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

        <b-card bg-variant="white" text-variant="white" class="text-center h-center col-md-4 mb-3">
          <b-form onsubmit="return false;">
            <b-form-group id="input-group-1" label-for="input-1">
              <b-form-input
                id="input-1"
                v-model="form.id"
                type="text"
                placeholder="ID"
                required
                @keyup.enter="login()"
              ></b-form-input>
            </b-form-group>

            <b-form-group id="input-group-2" label-for="input-2">
              <b-form-input
                id="input-2"
                v-model="form.password"
                type="password"
                placeholder="Password"
                required
                @keyup.enter="login()"
              ></b-form-input>
            </b-form-group>

            <b-button class="col-md-12" variant="primary" @click="login()">Login</b-button>
            <!--<b-button variant="success" @click="signup()">Signup</b-button>-->
            <!--<b-button variant="danger" @click="back()">Back</b-button>-->
          </b-form>
        </b-card>
        <b-link class="text-secondary" @click="signup()">Signup</b-link>
       </b-jumbotron>
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
              //alert(res.data.result.token);
              //this.$http.defaults.headers.common['Authorization'] = 'Bearer ' + res.data.result.token;
              
              sessionStorage.setItem('token', res.data.result.token);
              sessionStorage.setItem('userId', this.form.id);
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