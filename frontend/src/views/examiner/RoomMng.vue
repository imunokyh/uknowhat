<template>
  <div class="text-success bg-dark" style="height: 100%">
    <main>
      <section class="py-5 text-center container">
        <div class="row py-lg-5">
          <div class="col-lg-6 col-md-8 mx-auto">
            <h1 class="fw-light">출제자 메인</h1>
            <p class="lead text-muted">출제자 메인화면입니다</p>
            <p>
              <b-button class="col-sm-3 mr-1" @click="goProbBank()">문제은행</b-button>
              <b-button class="col-sm-3 ml-1" v-b-modal.modal-room-create>방 생성</b-button>
              <b-modal
                id="modal-room-create"
                ref="modal"
                title="방 타이틀을 입력하세요"
                @show="resetModal"
                @hidden="resetModal"
                @ok="handleOk"
              >
                <form ref="form" @submit.stop.prevent="handleSubmit">
                  <b-form-group
                    label="Title"
                    label-for="name-input"
                    invalid-feedback="Title is required"
                    :state="nameState"
                  >
                    <b-form-input
                      id="name-input"
                      v-model="name"
                      :state="nameState"
                      required
                    ></b-form-input>
                  </b-form-group>
                </form>
              </b-modal>
            </p>
          </div>
        </div>
      </section>

      <!-- card container -->
      <b-container fluid="md">
        <b-row cols="3">
          <b-col v-for="room in roomData" :key="room.id">
            <b-card :id="room.roomId" :title="room.roomTitle" class="mb-3">
              <b-card-text> (((o(*ﾟ▽ﾟ*)o))) </b-card-text>
              <b-button @click="modifyGroup(room.roomId, room.roomTitle)" variant="primary">
                수정
              </b-button>
              <b-button @click="deleteGroup(room.roomId)" variant="danger">
                삭제
              </b-button>
              <b-button @click="startGroup(room.roomId, room.roomTitle)" variant="success">
                시작
              </b-button>
            </b-card>
          </b-col>
        </b-row>
      </b-container>
      <!-- e.card container -->
    </main>
  </div>
</template>

<script>
export default {
  data() {
    return {
      roomData: [],
      roomTitle: "",
      name: "",
      nameState: null,
      submittedNames: [],
    };
  },
  mounted() {
    this.getRoomData();
  },
  methods: {
    getRoomData() {
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
      };
      let promise = this.$http.get("/api/v1/room/me", config);
      promise.then((res) => {
        this.roomData = [];
        this.roomData = res.data.result;
      });
    },
    goProbBank() {
      this.$router.push({ name: "ProbBank" });
    },
    modifyGroup(roomId, roomTitle) {
      this.$router.push({
        name: "ProbReg",
        params: {
          roomId: roomId,
          roomTitle: roomTitle,
          newRoomYn: "N",
        },
      });
    },
    deleteGroup(roomId) {
      if (confirm("방을 삭제하시겠습니까?") == false) {
        return;
      }
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
      };
      this.$http.delete("/api/v1/room/" + roomId, config).then((res) => {
        
        alert("삭제 되었습니다");
        this.getRoomData();
      });
    },
    startGroup(roomId, roomTitle) {
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
      };

      this.$http
        .put("/api/v1/room/state", {
          "id": roomId,
          "state": "PLAY"
        }, config)
        .then((res) => {
          if (res.data.code !== 0) {
            alert(res.data.message);
          } else {
            alert(res.data.result.roomNumber + "방이 시작되었습니다.");
            this.$router.push({ name: "RoomProc", params: { identification: roomId, number: res.data.result.roomNumber, examiner: sessionStorage.getItem("userId")} });
          }
        })
        .catch((error) => { console.log(error); });
    },
    checkFormValidity() {
      const valid = this.$refs.form.checkValidity();
      this.nameState = valid;
      return valid;
    },
    resetModal() {
      this.name = "";
      this.nameState = null;
    },
    handleOk(bvModalEvt) {
      // Prevent modal from closing
      bvModalEvt.preventDefault();
      // Trigger submit handler
      this.handleSubmit();
    },
    handleSubmit() {
      // Exit when the form isn't valid
      if (!this.checkFormValidity()) {
        return;
      }
      // Push the name to submitted names
      //this.submittedNames.push(this.name)
      const config = {
        headers: { Authorization: `Bearer ${sessionStorage.getItem("token")}` },
      };
      this.$http
        .post(
          "/api/v1/room",
          {
            title: this.name,
          },
          config
        )
        .then((res) => {
          // Hide the modal manually
          this.$nextTick(() => {
            this.$bvModal.hide("modal-room-create");
          });
          this.$router.push({
            name: "ProbReg",
            params: {
              roomId: res.data.result.roomId,
              roomTitle: res.data.result.roomTitle,
              newRoomYn: "Y",
            },
          });
        });
    },
  },
};
</script>

<style>
</style>