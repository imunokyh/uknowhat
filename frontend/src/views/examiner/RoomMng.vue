<template>
  <div class="text-success bg-dark" style="height: 768px">
    <main>
      <section class="py-5 text-center container">
        <div class="row py-lg-5">
          <div class="col-lg-6 col-md-8 mx-auto">
            <h1 class="fw-light">방관리</h1>
            <p class="lead text-muted">방을 관리합니다</p>
            <p>
              <!--
              <a href="/exam/probreg" class="btn btn-success my-2"
                >방문제등록하기</a
              >

              <b-button v-b-modal.modal-1>방문제등록</b-button>

              <b-modal id="modal-1" title="방정보입력">
                <p class="my-4">방제목을 넣어주세요</p>
                <b-form-input v-model="roomTitle" placeholder="방제목입력"></b-form-input>
              </b-modal>
              -->
              <b-button v-b-modal.modal-room-create>방생성</b-button>
              <b-modal
                id="modal-room-create"
                ref="modal"
                title="장제목을 입력하세요"
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
      <b-container>
        <b-row>
          <b-col>
            <b-card id="pgid1" :title="pg1Title">
              <b-card-text>
                {{ pg1Text }}
              </b-card-text>

              <b-button @click="modifyGroup('pgid1')" variant="primary"
                >수정</b-button
              >
              <b-button @click="deleteGroup('pgid1')" variant="danger"
                >삭제</b-button
              >
            </b-card>
          </b-col>
          <b-col>
            <b-card id="pgid2" :title="pg2Title">
              <b-card-text>
                {{ pg2Text }}
              </b-card-text>

              <b-button @click="modifyGroup('pgid2')" variant="primary"
                >수정</b-button
              >
              <b-button @click="deleteGroup('pgid2')" variant="danger"
                >삭제</b-button
              >
            </b-card>
          </b-col>
          <b-col>
            <b-card id="pgid3" :title="pg3Title">
              <b-card-text>
                {{ pg3Text }}
              </b-card-text>

              <b-button @click="modifyGroup('pgid3')" variant="primary"
                >수정</b-button
              >
              <b-button @click="deleteGroup('pgid3')" variant="danger"
                >삭제</b-button
              >
            </b-card>
          </b-col>
          <b-col>
            <b-card id="pgid4" :title="pg4Title">
              <b-card-text>
                {{ pg4Text }}
              </b-card-text>

              <b-button @click="modifyGroup('pgid4')" variant="primary"
                >수정</b-button
              >
              <b-button @click="deleteGroup('pgid4')" variant="danger"
                >삭제</b-button
              >
            </b-card>
          </b-col>
        </b-row>
        <b-row>&nbsp;</b-row>
        <b-row>
          <b-col>
            <b-card id="pgid5" :title="pg5Title">
              <b-card-text>
                {{ pg5Text }}
              </b-card-text>

              <b-button @click="modifyGroup('pgid5')" variant="primary"
                >수정</b-button
              >
              <b-button @click="deleteGroup('pgid5')" variant="danger"
                >삭제</b-button
              >
            </b-card>
          </b-col>
          <b-col></b-col>
          <b-col></b-col>
          <b-col></b-col>
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
      pg1Title: "문제그룹1",
      pg2Title: "문제그룹2",
      pg3Title: "문제그룹3",
      pg4Title: "문제그룹4",
      pg5Title: "문제그룹5",
      pg1Text: "문제그룹1설명",
      pg2Text: "문제그룹2설명",
      pg3Text: "문제그룹3설명",
      pg4Text: "문제그룹4설명",
      pg5Text: "문제그룹5설명",
      roomTitle: "",
      name: "",
      nameState: null,
      submittedNames: [],
    };
  },
  mounted() {},
  methods: {
    modifyGroup(probGrpId) {
      this.$router.push({ name: "ProbReg", params: { probGrpId: probGrpId } });
    },
    deleteGroup(probGrpId) {
      this.$http
        .post("/api/v1/sample/deleteGroup", {
          probGrpId: probGrpId,
        })
        .then((res) => {});
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
      this.$http.post("/api/v1/room", {
          title: this.name
      }).then((res) => {
        // Hide the modal manually
        console.log(res);
        this.$nextTick(() => {
          this.$bvModal.hide("modal-room-create");
        });
        this.$router.push({name: 'ProbReg', params:{roomId :res.data.result.roomId, roomTitle: res.data.result.roomTitle, newRoom: 1}});
      });
    },
  },
};
</script>

<style>
</style>